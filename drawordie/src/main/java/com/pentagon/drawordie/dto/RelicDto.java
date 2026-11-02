package com.pentagon.drawordie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 유물 데이터 (data/Relic.json 구조 그대로 클라이언트에 전달)
public class RelicDto {

    // 유물 하나
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private int id;
        private String key;         // 세이브 / 아이콘 연결에 쓰는 고유 이름
        private String name;
        private String description;
        private String effectType;  // PASSIVE(얻을 때 바로 적용) / TRIGGERED(조건이 되면 발동)
        private String trigger;     // 발동 시점 (ON_ACQUIRE, ON_DICE_ROLLED 등)
        private Condition condition;
        private Effect effect;
        private boolean stackable;  // 같은 유물을 여러 개 가질 수 있는지
    }

    // 발동 조건 (종류 + 기준 값)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Condition {
        private String type;
        private int value;
    }

    // 효과 (종류 + 수치)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Effect {
        private String type;
        private int value;
    }

    // Relic.json 전체 / 응답 (유니티 JsonUtility용으로 객체로 감쌈)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelicList {
        private List<Info> relics;
    }
}
