package com.pentagon.drawordie.repository;

import com.pentagon.drawordie.entity.GameSave;
import org.springframework.data.jpa.repository.JpaRepository;

// 게임 세이브 저장소 (키 = 유저 번호, 유저당 세이브 하나)
public interface GameSaveRepository extends JpaRepository<GameSave, Long> {
}