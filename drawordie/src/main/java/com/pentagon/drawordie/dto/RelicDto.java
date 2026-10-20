package com.pentagon.drawordie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class RelicDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private int id;
        private String key;
        private String name;
        private String description;

        // PASSIVE 또는 TRIGGERED
        private String effectType;

        // ON_ACQUIRE, ON_DICE_ROLLED 등
        private String trigger;

        private Condition condition;
        private Effect effect;
        private boolean stackable;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Condition {
        private String type;
        private int value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Effect {
        private String type;
        private int value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelicList {
        private List<Info> relics;
    }
}
