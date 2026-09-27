package com.pentagon.drawordie.service;

import com.pentagon.drawordie.entity.GameSave;
import com.pentagon.drawordie.entity.User;
import com.pentagon.drawordie.repository.GameSaveRepository;
import com.pentagon.drawordie.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class GameSaveService {

    private final GameSaveRepository gameSaveRepository;
    private final UserRepository userRepository;

    public GameSaveService(GameSaveRepository gameSaveRepository, UserRepository userRepository) {
        this.gameSaveRepository = gameSaveRepository;
        this.userRepository = userRepository;
    }

    // 🟢 게임 세이브 로직
    public GameSave saveGame(Long userId,
                              int masterSeed, int mapSeed, int nodeSeed,
                              int hp, int shield, int cost,
                              String deckData, String monsterData,
                              int currentFloor, int currentIndex,
                              int currentAct, int currentNodeType,
                              int playTime) {
        // 1. 저장할 유저가 DB에 있는지 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        // 2. 기존 세이브가 있으면 불러오고, 없으면 새로 만듦
        GameSave gameSave = gameSaveRepository.findById(userId).orElse(new GameSave());

        // 3. 데이터 업데이트
        gameSave.setUser(user);
        gameSave.setMasterSeed(masterSeed);
        gameSave.setMapSeed(mapSeed);
        gameSave.setNodeSeed(nodeSeed);
        gameSave.setCurrentHp(hp);
        gameSave.setCurrentShield(shield);
        gameSave.setCurrentCost(cost);
        gameSave.setDeckData(deckData);       // JSON 문자열
        gameSave.setMonsterData(monsterData); // JSON 문자열 (전투 중이 아니면 null)
        gameSave.setCurrentFloor(currentFloor);
        gameSave.setCurrentIndex(currentIndex);
        gameSave.setCurrentAct(currentAct);
        gameSave.setCurrentNodeType(currentNodeType);
        gameSave.setPlayTime(playTime);

        // 4. DB에 저장
        return gameSaveRepository.save(gameSave);
    }
}