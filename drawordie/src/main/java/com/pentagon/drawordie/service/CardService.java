package com.pentagon.drawordie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pentagon.drawordie.dto.CardDto; // 와일드카드(*)를 빼고 정확히 명시
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CardService {

    // 변수 타입에 CardDto. 를 붙여서 출처를 명확히 합니다.
    private List<CardDto.Adjective> allAdjectives = new ArrayList<>();
    private List<CardDto.Gerund> allGerunds = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<CardDto.Combination> allCombinations = new ArrayList<>();

    // 서버가 켜질 때 JSON 파일들을 읽어서 리스트에 채워둡니다.
    @PostConstruct
    public void loadCardData() {
        try {
            // objectMapper 안에도 CardDto.AdjectiveList.class 처럼 정확한 경로를 명시합니다.
            CardDto.AdjectiveList adjList = objectMapper.readValue(
                    new ClassPathResource("data/adjectives.json").getInputStream(), CardDto.AdjectiveList.class);
            allAdjectives = adjList.getAdjectives();

            CardDto.GerundList gerList = objectMapper.readValue(
                    new ClassPathResource("data/gerunds.json").getInputStream(), CardDto.GerundList.class);
            allGerunds = gerList.getGerunds();

            //홍성구 추가
            CardDto.CombinationList comboList = objectMapper.readValue(
                    new ClassPathResource("data/combinations.json").getInputStream(), CardDto.CombinationList.class);
            allCombinations = comboList.getCombinations();

            System.out.println("🟢 [CardService] 카드 데이터 로드 완료! 형용사: " + allAdjectives.size() + "개, 동명사: " + allGerunds.size() + "개, 결과카드: " + allCombinations.size() + "개"); //홍성구 수정
        } catch (IOException e) {
            System.err.println("🔴 [CardService] 카드 데이터 로드 실패: " + e.getMessage());
        }
    }

    // 🔥 무작위로 형용사 2개, 동명사 3개를 뽑아서 반환하는 핵심 비즈니스 로직
    public CardDto.BattleStartHand getRandomStartHand() {
        List<CardDto.Adjective> shuffledAdj = new ArrayList<>(allAdjectives);
        List<CardDto.Gerund> shuffledGer = new ArrayList<>(allGerunds);

        Collections.shuffle(shuffledAdj);
        Collections.shuffle(shuffledGer);

        // JSON 직렬화 에러를 막기 위해 잘라낸 리스트를 새 ArrayList로 안전하게 포장해 줍니다.
        return new CardDto.BattleStartHand(
                new ArrayList<>(shuffledAdj.subList(0, 2)),
                new ArrayList<>(shuffledGer.subList(0, 3))
        );
    }

    public CardDto.CombinationList getCombinations() {
        List<CardDto.Combination> combinations = new ArrayList<>(allCombinations);

        return new CardDto.CombinationList(combinations);
    }
}