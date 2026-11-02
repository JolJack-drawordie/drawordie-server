package com.pentagon.drawordie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// 회원
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인 아이디
    @Column(unique = true, nullable = false)
    private String username;

    // BCrypt 해시. GameSave 등과 함께 응답으로 나갈 때도 빠지도록 JSON에서 제외
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    // 랭킹에 표시되는 이름이라 중복 불가
    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 처음 저장될 때 가입 시각을 채운다
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}