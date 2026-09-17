package com.pentagon.drawordie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_save")
@Getter
@Setter
@NoArgsConstructor
public class GameSave {
    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private int masterSeed;  // 게임 전체 최상위 시드
    private int mapSeed;     // 지형 복구용 시드
    private int nodeSeed;    // 현재 선택된 노드 전용 시드

    private int currentHp;  //현재 체력
    private int currentShield;  //현재 쉴드량
    private int currentCost; // 기본 코스트 + 주사위 합산 결과값 저장

    @Column(columnDefinition = "TEXT")
    private String deckData; // 형용사+동명사 덱 정보 (JSON 문자열)

    @Column(columnDefinition = "TEXT")
    private String monsterData; // 전투 중인 몬스터 정보 (JSON 문자열, 전투 중이 아니면 null)

    private int currentFloor; // 현재 노드의 층
    private int currentIndex; // 현재 노드의 인덱스
}