package com.pentagon.drawordie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 회원 API 응답 (엔티티를 그대로 보내면 비밀번호 해시까지 나가므로 필요한 값만 담는다)
public class UserDto {

    // 로그인 응답
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private Long userId;
        private String username;
        private String nickname;
        private String token; // JWT (이후 요청의 Authorization 헤더에 넣음)
    }

    // 회원가입 응답
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterResponse {
        private Long userId;
        private String username;
        private String nickname;
    }
}
