package com.pentagon.drawordie.repository;

import com.pentagon.drawordie.entity.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {
    List<GameResult> findAllByOrderByScoreDesc(); // 랭킹용
}