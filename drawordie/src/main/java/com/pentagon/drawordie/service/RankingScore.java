package com.pentagon.drawordie.service;

// 랭킹 점수 계산.
// 클리어 여부 → 도달 Act → 도달 층 → 중간보스 처치 → 플레이 시간 순서로 비교되도록 자릿수를 나눈다.
//
//   클리어                10,000,000
//   Act 1개당              1,000,000
//   층 1개당                  10,000
//   중간보스 처치 1회당         3,000  (최대 3회까지, 합쳐도 층 하나보다 작음)
//   시간 보너스            0 ~ 9,999  (클리어한 판만, 9,999 - 플레이 시간(초))
//
// 점수가 같으면 먼저 달성한 기록이 위 (GameResultRepository에서 endedAt 오름차순)
public final class RankingScore {

    // 결과 등록 요청 값 검증용 최대치
    public static final int MAX_ACT = 3;
    public static final int MAX_FLOOR = 99;
    public static final int MAX_ELITE_KILLS = 99; // 점수에는 SCORED_ELITE_KILLS까지만 반영

    private static final int CLEAR_POINTS = 10_000_000;
    private static final int ACT_POINTS = 1_000_000;
    private static final int FLOOR_POINTS = 10_000;
    private static final int ELITE_POINTS = 3_000;
    private static final int SCORED_ELITE_KILLS = 3; // 3 x 3,000 < 10,000 → 더 멀리 간 판이 항상 위
    private static final int MAX_TIME_BONUS = 9_999;

    // 계산 전용이라 객체를 만들지 않음
    private RankingScore() {
    }

    public static int calculate(boolean cleared, int reachedAct, int reachedFloor, int eliteKills, int playTime) {
        int score = reachedAct * ACT_POINTS + reachedFloor * FLOOR_POINTS;
        score += Math.min(eliteKills, SCORED_ELITE_KILLS) * ELITE_POINTS;

        // 클리어한 판만 빨리 깰수록 보너스
        if (cleared) {
            score += CLEAR_POINTS;
            score += MAX_TIME_BONUS - Math.min(playTime, MAX_TIME_BONUS);
        }

        return score;
    }
}
