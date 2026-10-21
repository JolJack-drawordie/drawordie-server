package com.pentagon.drawordie.controller;

import com.pentagon.drawordie.dto.UserDto;
import com.pentagon.drawordie.entity.User;
import com.pentagon.drawordie.security.JwtTokenProvider;
import com.pentagon.drawordie.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 이 클래스가 REST API 요청을 받는 곳임을 명시
@RequestMapping("/api/users") // 이 컨트롤러의 기본 주소 설정
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 서버 연결 확인용 테스트
    @GetMapping("/ping")
    public String ping() {
        return "Draw or Die 서버가 정상 작동 중입니다! (통신 성공)";
    }

    // 2. 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password, @RequestParam String nickname) {
        if (UserService.normalizeNickname(nickname).isEmpty()) {
            return ResponseEntity.badRequest().body("NICKNAME_EMPTY");
        }

        // 중복 확인 후 가입 직전에 다른 사람이 먼저 가입했을 수 있으므로 다시 확인 (유니티는 409 본문으로 어느 쪽인지 구분)
        if (userService.isUsernameTaken(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("USERNAME_DUPLICATE");
        }
        if (userService.isNicknameTaken(nickname)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("NICKNAME_DUPLICATE");
        }

        try {
            // 엔티티를 그대로 반환하면 비밀번호 해시까지 응답에 포함되므로 DTO로 반환
            User user = userService.registerUser(username, password, nickname);
            return ResponseEntity.ok(new UserDto.RegisterResponse(user.getId(), user.getUsername(), user.getNickname()));
        } catch (DataIntegrityViolationException e) {
            // 위 확인과 저장 사이에 동시에 가입한 경우 DB UNIQUE 제약이 막아줌
            return ResponseEntity.status(HttpStatus.CONFLICT).body("DUPLICATE");
        }
    }

    // 2-1. 아이디 중복 확인 (true = 이미 사용중인 아이디)
    @GetMapping("/check-username")
    public boolean checkUsername(@RequestParam String username) {
        return userService.isUsernameTaken(username);
    }

    // 2-2. 닉네임 중복 확인 (true = 이미 사용중인 닉네임)
    @GetMapping("/check-nickname")
    public boolean checkNickname(@RequestParam String nickname) {
        return userService.isNicknameTaken(nickname);
    }

    // 3. 로그인
    @PostMapping("/login")
    public UserDto.LoginResponse login(@RequestParam String username, @RequestParam String password) {
        User user = userService.loginUser(username, password);
        if (user != null) {
            // 로그인 성공 시 JWT 발급 → 유니티는 이후 세이브/로드/결과 요청에 이 토큰을 헤더로 보냄
            String token = jwtTokenProvider.createToken(user.getId(), user.getUsername());
            return new UserDto.LoginResponse(user.getId(), user.getUsername(), user.getNickname(), token);
        } else {
            // 스프링에서 에러를 발생시켜 유니티 쪽으로 실패 메시지를 보냅니다.
            throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
        }
    }
}