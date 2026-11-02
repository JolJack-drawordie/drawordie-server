package com.pentagon.drawordie.service;

import com.pentagon.drawordie.entity.GameSave;
import com.pentagon.drawordie.entity.User;
import com.pentagon.drawordie.repository.GameSaveRepository;
import com.pentagon.drawordie.repository.UserRepository;
import org.springframework.stereotype.Service;

// 게임 세이브 저장
@Service
public class GameSaveService {

    private final GameSaveRepository gameSaveRepository;
    private final UserRepository userRepository;

    public GameSaveService(GameSaveRepository gameSaveRepository, UserRepository userRepository) {
        this.gameSaveRepository = gameSaveRepository;
        this.userRepository = userRepository;
    }

    // 유저의 세이브를 지금 진행 상태로 덮어쓴다 (없으면 새로 만듦)
    public GameSave saveGame(Long userId,
                              int masterSeed, int mapSeed, int nodeSeed,
                              int hp, int maxHp, int shield, int cost, int maxCost,
                              String deckData, String monsterData, String relicData,
                              int currentFloor, int currentIndex,
                              int currentAct, int currentNodeType, boolean rested,
                              int playTime, int eliteKills) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));

        GameSave gameSave = gameSaveRepository.findById(userId).orElse(new GameSave());

        gameSave.setUser(user);
        gameSave.setMasterSeed(masterSeed);
        gameSave.setMapSeed(mapSeed);
        gameSave.setNodeSeed(nodeSeed);
        gameSave.setCurrentHp(hp);
        gameSave.setMaxHp(maxHp);
        gameSave.setCurrentShield(shield);
        gameSave.setCurrentCost(cost);
        gameSave.setMaxCost(maxCost);
        gameSave.setDeckData(deckData);
        gameSave.setMonsterData(monsterData);
        gameSave.setRelicData(relicData);
        gameSave.setCurrentFloor(currentFloor);
        gameSave.setCurrentIndex(currentIndex);
        gameSave.setCurrentAct(currentAct);
        gameSave.setCurrentNodeType(currentNodeType);
        gameSave.setRested(rested);
        gameSave.setPlayTime(playTime);
        gameSave.setEliteKills(eliteKills);

        return gameSaveRepository.save(gameSave);
    }
}