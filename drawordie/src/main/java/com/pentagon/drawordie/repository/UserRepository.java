package com.pentagon.drawordie.repository;

import com.pentagon.drawordie.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 회원 저장소
public interface UserRepository extends JpaRepository<User, Long> {
    // 로그인할 때 아이디로 회원 찾기 (없으면 null)
    User findByUsername(String username);

    // 회원가입 중복 확인
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
}