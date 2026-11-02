package com.pentagon.drawordie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 카드 데이터 (resources/data의 카드 JSON 구조 그대로 클라이언트에 전달)
public class CardDto {

    // 형용사 카드: 동명사 카드의 수치를 더하거나 빼는 수식어
    @Data
    public static class Adjective {
        private int id;
        private String name;
        private int costMod;
        private int dmgMod;
        private int shdMod;
        private int healMod;
        private String desc;
    }

    // 동명사 카드: 기본 행동 (공격 / 방어 / 회복)
    @Data
    public static class Gerund {
        private int id;
        private String name;
        private String category; // ATTACK / DEFENSE / HEAL (카드 UI 색상 구분용)
        private int baseCost;
        private int baseDmg;
        private int baseShd;
        private int baseHeal;
        private String desc;
    }

    // 조합 카드: 형용사 + 동명사를 합친 결과와 최종 수치
    @Data
    public static class Combination {
        private String combinationId;
        private int adjectiveId;
        private int gerundId;
        private String skillName;
        private int finalCost;
        private int finalDamage;
        private int finalShield;
        private int finalHeal;
        private String description;
    }

    // 새 게임 시작 덱 (카드 id 목록, 같은 id가 여러 번 들어갈 수 있음)
    @Data
    public static class DefaultDeckData {
        private List<Integer> adjectiveIds;
        private List<Integer> gerundIds;
    }

    // 아래는 JSON 파일 전체 / 응답을 감싸는 클래스 (유니티 JsonUtility는 최상위 배열을 읽지 못함)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdjectiveList { private List<Adjective> adjectives; }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GerundList { private List<Gerund> gerunds; }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CombinationList {
        private List<Combination> combinations;
    }
}