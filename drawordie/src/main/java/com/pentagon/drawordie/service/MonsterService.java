package com.pentagon.drawordie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pentagon.drawordie.dto.MonsterDto;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonsterService {

    private List<MonsterDto.Info> allMonsters = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void loadMonsterData() {
        try {
            MonsterDto.MonsterList monsterList = objectMapper.readValue(
                    new ClassPathResource("data/Monster.json").getInputStream(), MonsterDto.MonsterList.class);
            allMonsters = monsterList.getMonsters();

            System.out.println("🟢 [MonsterService] 몬스터 데이터 로드 완료! 총 몬스터: " + allMonsters.size() + "마리");
        } catch (IOException e) {
            System.err.println("🔴 [MonsterService] 몬스터 데이터 로드 실패: " + e.getMessage());
        }
    }

    public MonsterDto.MonsterList getMonsters() {
        List<MonsterDto.Info> monsters = new ArrayList<>(allMonsters);
        return new MonsterDto.MonsterList(monsters);
    }
}
