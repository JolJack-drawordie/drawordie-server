package com.pentagon.drawordie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class MonsterDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private int id;
        private String name;
        private int hp;
        private int shield;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonsterList {
        private List<Info> monsters;
    }
}