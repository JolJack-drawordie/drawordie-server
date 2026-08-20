package com.pentagon.drawordie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class CardDto {

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

    @Data
    public static class Gerund {
        private int id;
        private String name;
        private int baseCost;
        private int baseDmg;
        private int baseShd;
        private int baseHeal;
        private String desc;
    }

    @Data
    public static class Combination{ // 홍성구 추가
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

    @Data
    public static class DefaultDeckData { // 혹은 DefaultDeckDto
        private List<Integer> adjectiveIds;
        private List<Integer> gerundIds;
    }

    // JSON 전체를 파싱하기 위한 래퍼 클래스들
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
    public static class CombinationList { // 홍성구 추가
        private List<Combination> combinations;
    }

    // 🔥 최종적으로 유니티에게 넘겨줄 응답 박스 (형용사 2개 + 동명사 3개)
    @Data
    public static class BattleStartHand {
        private List<Adjective> adjectives;
        private List<Gerund> gerunds;

        public BattleStartHand(List<Adjective> adjectives, List<Gerund> gerunds) {
            this.adjectives = adjectives;
            this.gerunds = gerunds;
        }
    }
}