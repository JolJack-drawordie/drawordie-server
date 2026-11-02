package com.pentagon.drawordie.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 몬스터 데이터 (resources/data/Monster.json 구조 그대로 클라이언트에 전달)
public class MonsterDto {

    // 몬스터 하나
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
        private int defend;   // 방어 행동 때 얻는 방어도
        private BossData boss; // 중간보스 / 보스 기믹 (일반 몬스터는 없음 → 응답에서도 빠짐)
    }

    // 공격 기술 하나
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

    // 아래 기믹 블록들의 공통 규칙
    // - "defined": true는 항상 응답에 붙어서, 클라이언트가 블록이 JSON에 있었는지 구분한다 (Monster.json에 쓸 필요 없음)
    // - 숫자는 Wrapper 타입: JSON에 안 적으면 null → 응답에서 빠짐 → 클라이언트는 프리팹 인스펙터 값을 사용
    // - 블록이 있으면 그 기믹이 켜지고, 없으면 꺼진다

    // 보스 기믹 모음
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BossData {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;

        private List<String> pattern; // 행동 순서: "Attack" / "Defend" / "Charge" / "StrongAttack" (비우면 무작위)

        private Charge charge;
        private Regen regen;
        private Enrage enrage;
        private Lifesteal lifesteal;
        private Thorns thorns;
        private Phase2 phase2;
    }

    // 기 모으기 → 다음 턴 강공격
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Charge {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Double chance;     // 패턴이 없을 때 기 모으기 확률 (0~1)
        private Double multiplier; // 강공격 배율
    }

    // 매 턴 체력 회복
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Regen {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Integer amount; // 회복량
    }

    // 체력이 낮아지면 분노 (공격력 증가)
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Enrage {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Double hpRatio;          // 이 체력 비율 이하에서 분노
        private Double attackMultiplier; // 분노 시 공격 배율
    }

    // 흡혈 (준 피해 일부 회복)
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Lifesteal {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Double ratio; // 회복 비율 (0~1)
    }

    // 가시 (방어도가 있을 때 맞으면 반사 피해)
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thorns {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Integer damage; // 반사 피해
    }

    // 2페이즈 (체력이 낮아지면 한 번 전환)
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Phase2 {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final boolean defined = true;
        private Double hpRatio;       // 이 체력 비율 이하에서 전환
        private Integer shield;       // 전환 시 얻는 방어도
        private Integer heal;         // 전환 시 회복량
        private List<String> pattern; // 2페이즈 행동 순서 (비우면 기존 유지)
    }

    // Monster.json 전체 / 응답 (유니티 JsonUtility용으로 객체로 감쌈)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonsterList {
        private List<Info> monsters;
    }
}
