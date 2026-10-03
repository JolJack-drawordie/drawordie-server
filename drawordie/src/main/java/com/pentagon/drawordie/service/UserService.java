package com.pentagon.drawordie.service;

import com.pentagon.drawordie.entity.User;
import com.pentagon.drawordie.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 기능
    public User registerUser(String username, String password, String nickname) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password)); // 평문 대신 BCrypt 해시로 저장
        newUser.setNickname(nickname);

        // DB에 저장하고, 저장된 결과를 반환
        return userRepository.save(newUser);
    }

    // 아이디 중복 확인 (true = 이미 사용중)
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    // 로그인 검증 로직
    public User loginUser(String username, String password) {
        // 1. DB에서 아이디로 유저를 찾습니다.
        User user = userRepository.findByUsername(username);

        // 2. 유저가 존재하고, 입력한 비밀번호가 저장된 BCrypt 해시와 일치하면 성공!
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        // 3. 실패하면 null을 반환합니다.
        return null;
    }
}