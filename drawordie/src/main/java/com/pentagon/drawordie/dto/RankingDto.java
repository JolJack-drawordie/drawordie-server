package com.pentagon.drawordie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class RankingDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {
        private Long resultId;
        private int rank;
        private String nickname;
        private int score;
        private boolean cleared;
        private int reachedAct;
        private int reachedFloor;
        private int playTime;
        private LocalDateTime endedAt;
    }

    // 결과 등록 응답: 방금 등록한 기록의 순위
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultResponse {
        private Long resultId;
        private long rank;
        private int score;
    }

    // 유니티 JsonUtility는 최상위 배열을 읽지 못하므로 객체로 감싼다
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankingList {
        private List<Entry> rankings;
    }
}
