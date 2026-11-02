package com.pentagon.drawordie.repository;

import com.pentagon.drawordie.entity.GameResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

// 게임 결과(랭킹 기록) 저장소
public interface GameResultRepository extends JpaRepository<GameResult, Long> {
    // 랭킹 상위 100개: 점수 높은 순, 같으면 먼저 달성한 순 (닉네임 표시를 위해 유저도 함께 조회)
    @EntityGraph(attributePaths = "user")
    List<GameResult> findTop100ByOrderByScoreDescEndedAtAsc();

    // 순위 계산: 나보다 점수가 높은 기록 수 + 점수는 같지만 먼저 달성한 기록 수
    long countByScoreGreaterThan(int score);
    long countByScoreAndEndedAtBefore(int score, LocalDateTime endedAt);
}