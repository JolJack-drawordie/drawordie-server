package com.pentagon.drawordie.repository;

import com.pentagon.drawordie.entity.GameSave;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameSaveRepository extends JpaRepository<GameSave, Long> {
}