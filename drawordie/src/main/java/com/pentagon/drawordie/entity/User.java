package com.pentagon.drawordie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore // 다른 엔티티(GameSave 등)와 함께 응답으로 나갈 때도 비밀번호 해시는 제외
    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false) // 랭킹에 닉네임으로 표시되므로 중복 불가
    private String nickname;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}