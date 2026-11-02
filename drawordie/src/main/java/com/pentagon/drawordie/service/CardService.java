package com.pentagon.drawordie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pentagon.drawordie.dto.CardDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 카드 데이터: 형용사 / 동명사 / 조합 카드 표와 기본 덱 (resources/data의 JSON)
@Service
public class CardService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<CardDto.Adjective> allAdjectives = new ArrayList<>();
    private List<CardDto.Gerund> allGerunds = new ArrayList<>();
    private List<CardDto.Combination> allCombinations = new ArrayList<>();

    // 서버가 켜질 때 카드 표를 읽어 둔다
    @PostConstruct
    public void loadCardData() {
        try {
            CardDto.AdjectiveList adjList = objectMapper.readValue(
                    new ClassPathResource("data/adjectives.json").getInputStream(), CardDto.AdjectiveList.class);
            allAdjectives = adjList.getAdjectives();

            CardDto.GerundList gerList = objectMapper.readValue(
                    new ClassPathResource("data/gerunds.json").getInputStream(), CardDto.GerundList.class);
            allGerunds = gerList.getGerunds();

            CardDto.CombinationList comboList = objectMapper.readValue(
                    new ClassPathResource("data/combinations.json").getInputStream(), CardDto.CombinationList.class);
            allCombinations = comboList.getCombinations();

            System.out.println("🟢 [CardService] 카드 데이터 로드 완료! 형용사: " + allAdjectives.size() + "개, 동명사: " + allGerunds.size() + "개, 결과카드: " + allCombinations.size() + "개");
        } catch (IOException e) {
            System.err.println("🔴 [CardService] 카드 데이터 로드 실패: " + e.getMessage());
        }
    }

    // 새 게임 시작 덱 (형용사 / 동명사 카드 id 목록). 파일을 고치면 서버를 다시 켜지 않아도 반영되도록 요청마다 읽는다
    public CardDto.DefaultDeckData getDefaultDeck() {
        try {
            return objectMapper.readValue(
                    new ClassPathResource("data/deck.json").getInputStream(), CardDto.DefaultDeckData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 아래 조회는 원본 목록이 바뀌지 않도록 복사해서 돌려준다
    public CardDto.AdjectiveList getAdjectives() {
        return new CardDto.AdjectiveList(new ArrayList<>(allAdjectives));
    }

    public CardDto.GerundList getGerunds() {
        return new CardDto.GerundList(new ArrayList<>(allGerunds));
    }

    public CardDto.CombinationList getCombinations() {
        return new CardDto.CombinationList(new ArrayList<>(allCombinations));
    }
}