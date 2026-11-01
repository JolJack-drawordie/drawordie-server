package com.pentagon.drawordie.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class MonsterDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {
        private int id;
        private String name;
        private int hp;
        private int shield;
        private List<AttackData> attacks;
        private int defend;
        // 중간보스/보스 기믹 (일반 몬스터는 생략 → 응답에서도 빠짐)
        private BossData boss;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttackData {
        private String skillId;
        private String name;
        private int damage;
        private int cooldown;
    }

    // 클라이언트는 "defined": true 로 블록/기믹이 JSON에 있었는지 구분함 (Monster.json에 직접 쓸 필요 없음)
    // 숫자는 Wrapper 타입: JSON에 안 적으면 null → 응답에서 빠짐 → 클라이언트가 프리팹 Inspector 값 사용
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BossData {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;

        // 행동 순서: "Attack" / "Defend" / "Charge" / "StrongAttack". 비우면 랜덤
        private List<String> pattern;

        // 아래 기믹은 블록이 있으면 켜짐, 없으면 꺼짐
        private Charge charge;
        private Regen regen;
        private Enrage enrage;
        private Lifesteal lifesteal;
        private Thorns thorns;
        private Phase2 phase2;
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Charge {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Double chance;      // 패턴을 안 쓸 때 기 모으기 확률 (0~1)
        private Double multiplier;  // 강공격 배율
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Regen {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Integer amount;     // 매 턴 회복량
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Enrage {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Double hpRatio;           // 이 체력 비율 이하에서 분노
        private Double attackMultiplier;  // 분노 시 공격 배율
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Lifesteal {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Double ratio;       // 준 피해 중 회복 비율 (0~1)
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thorns {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Integer damage;     // 방어도가 있을 때 맞으면 반사 피해
    }

    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Phase2 {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Double hpRatio;     // 이 체력 비율 이하에서 1회 전환
        private Integer shield;     // 전환 시 얻는 방어도
        private Integer heal;       // 전환 시 회복량
        private List<String> pattern; // 2페이즈 행동 순서 (비우면 기존 유지)
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonsterList {
        private List<Info> monsters;
    }
}
