package com.pentagon.drawordie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pentagon.drawordie.dto.MonsterDto;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 몬스터 수치 / 보스 기믹 데이터 (resources/data/Monster.json)
@Service
public class MonsterService {

    private List<MonsterDto.Info> allMonsters = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 서버가 켜질 때 몬스터 표를 읽어 둔다
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

    // 원본 목록이 바뀌지 않도록 복사해서 돌려준다
    public MonsterDto.MonsterList getMonsters() {
        return new MonsterDto.MonsterList(new ArrayList<>(allMonsters));
    }
}
