package com.pentagon.drawordie.repository;

import com.pentagon.drawordie.entity.GameResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {
    // 랭킹용: 점수 높은 순, 같으면 먼저 달성한 순 (유저 정보 함께 조회)
    @EntityGraph(attributePaths = "user")
    List<GameResult> findTop100ByOrderByScoreDescEndedAtAsc();

    // 순위 계산용: 나보다 점수가 높거나, 점수가 같고 먼저 달성한 기록 수
    long countByScoreGreaterThan(int score);
    long countByScoreAndEndedAtBefore(int score, LocalDateTime endedAt);
}