package com.pentagon.drawordie.controller;

import com.pentagon.drawordie.dto.UserDto;
import com.pentagon.drawordie.entity.User;
import com.pentagon.drawordie.security.JwtTokenProvider;
import com.pentagon.drawordie.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 회원 API: 회원가입 / 아이디·닉네임 중복 확인 / 로그인
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 서버가 켜져 있는지 확인용 (브라우저로 열어 볼 때)
    @GetMapping("/ping")
    public String ping() {
        return "Draw or Die 서버가 정상 작동 중입니다! (통신 성공)";
    }

    // 회원가입. 실패 이유는 본문 문자열로 알려 준다 (유니티가 409 본문으로 아이디 / 닉네임 중복을 구분)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password, @RequestParam String nickname) {
        if (UserService.normalizeNickname(nickname).isEmpty()) {
            return ResponseEntity.badRequest().body("NICKNAME_EMPTY");
        }

        // 클라이언트가 중복 확인을 한 뒤 다른 사람이 먼저 가입했을 수 있으므로 다시 확인
        if (userService.isUsernameTaken(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("USERNAME_DUPLICATE");
        }
        if (userService.isNicknameTaken(nickname)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("NICKNAME_DUPLICATE");
        }

        try {
            // 엔티티를 그대로 보내면 비밀번호 해시까지 나가므로 DTO로
            User user = userService.registerUser(username, password, nickname);
            return ResponseEntity.ok(new UserDto.RegisterResponse(user.getId(), user.getUsername(), user.getNickname()));
        } catch (DataIntegrityViolationException e) {
            // 위 확인과 저장 사이에 동시에 가입한 경우 DB UNIQUE 제약이 막아 줌
            return ResponseEntity.status(HttpStatus.CONFLICT).body("DUPLICATE");
        }
    }

    // 아이디 중복 확인 (true = 이미 사용 중)
    @GetMapping("/check-username")
    public boolean checkUsername(@RequestParam String username) {
        return userService.isUsernameTaken(username);
    }

    // 닉네임 중복 확인 (true = 이미 사용 중)
    @GetMapping("/check-nickname")
    public boolean checkNickname(@RequestParam String nickname) {
        return userService.isNicknameTaken(nickname);
    }

    // 로그인: 성공하면 JWT 발급 (유니티는 이후 세이브 / 로드 / 결과 요청에 이 토큰을 헤더로 보냄)
    @PostMapping("/login")
    public UserDto.LoginResponse login(@RequestParam String username, @RequestParam String password) {
        User user = userService.loginUser(username, password);
        if (user != null) {
            String token = jwtTokenProvider.createToken(user.getId(), user.getUsername());
            return new UserDto.LoginResponse(user.getId(), user.getUsername(), user.getNickname(), token);
        } else {
            // 예외로 실패 응답을 보내면 유니티가 로그인 실패로 처리
            throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
        }
    }
}