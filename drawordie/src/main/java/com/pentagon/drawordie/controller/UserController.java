package com.pentagon.drawordie.controller;

import com.pentagon.drawordie.entity.User;
import com.pentagon.drawordie.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController // 이 클래스가 REST API 요청을 받는 곳임을 명시
@RequestMapping("/api/users") // 이 컨트롤러의 기본 주소 설정
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🟢 1. 서버 생존 확인용 테스트 API
    @GetMapping("/ping")
    public String ping() {
        return "Draw or Die 서버가 정상 작동 중입니다! (통신 성공)";
    }

    // 🔵 2. 회원가입 API (유니티에서 POST 방식으로 데이터를 보낼 곳)
    @PostMapping("/register")
    public User register(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String nickname) {
        return userService.registerUser(username, password, nickname);
    }

    // 🟡 3. 로그인 API (새로 추가된 부분!)
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        User user = userService.loginUser(username, password);
        if (user != null) {
            return "로그인 성공! 유저 번호: " + user.getId();
        } else {
            // 스프링에서 에러를 발생시켜 유니티 쪽으로 실패 메시지를 보냅니다.
            throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
        }
    }
}