package com.pentagon.drawordie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// 끝난 판 하나의 기록 (랭킹에 표시). 한 유저가 여러 판을 기록할 수 있다.
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

    private int score;        // 랭킹 점수 (서버가 계산, RankingScore 참고)
    private int reachedAct;   // 도달한 Act (1 ~ 3)
    private int reachedFloor; // 도달한 층 (1부터, 맵에 들어가기 전이면 0)
    private int playTime;     // 총 플레이 시간 (초)
    private int eliteKills;   // 처치한 중간보스 수 (예전 기록은 0)
    private boolean cleared;  // 최종 보스 처치 여부

    private LocalDateTime endedAt; // 기록된 시각 (점수가 같으면 먼저 달성한 기록이 위)

    // 처음 저장될 때 기록 시각을 채운다
    @PrePersist
    protected void onEnded() { this.endedAt = LocalDateTime.now(); }
}