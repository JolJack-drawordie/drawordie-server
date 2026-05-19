package com.pentagon.drawordie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_result")
@Getter
@Setter
@NoArgsConstructor
public class GameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int score;
    private int maxStage;
    private int playTime;
    private boolean cleared;

    private LocalDateTime endedAt;

    @PrePersist
    protected void onEnded() { this.endedAt = LocalDateTime.now(); }
}