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

    private int score;        // 랭킹 점수 (서버에서 계산, RankingScore 참고)
    private int reachedAct;   // 도달한 Act (1 ~ 3)
    private int reachedFloor; // 도달한 층 (1부터, 맵에 진입하지 않았으면 0)
    private int playTime;     // 총 플레이 타임 (초)
    private boolean cleared;  // 최종 보스 처치 여부

    private LocalDateTime endedAt;

    @PrePersist
    protected void onEnded() { this.endedAt = LocalDateTime.now(); }
}