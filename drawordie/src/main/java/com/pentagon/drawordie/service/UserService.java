package com.pentagon.drawordie.service;

import com.pentagon.drawordie.entity.User;
import com.pentagon.drawordie.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 회원가입 / 중복 확인 / 로그인
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 (비밀번호는 평문 대신 BCrypt 해시로 저장)
    public User registerUser(String username, String password, String nickname) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setNickname(normalizeNickname(nickname));

        return userRepository.save(newUser);
    }

    // 아이디 중복 확인 (true = 이미 사용 중)
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    // 닉네임 중복 확인 (true = 이미 사용 중)
    public boolean isNicknameTaken(String nickname) {
        return userRepository.existsByNickname(normalizeNickname(nickname));
    }

    // 닉네임 비교 기준: 앞뒤 공백만 제거 (유니티 AuthManager.NormalizeNickname과 같아야 함)
    public static String normalizeNickname(String nickname) {
        return nickname == null ? "" : nickname.trim();
    }

    // 로그인: 아이디가 있고 비밀번호가 저장된 해시와 맞으면 회원, 아니면 null
    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}