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
    private int maxCost;     // 이번 턴 최대 코스트 (에너지 게이지 최대치, 구버전 세이브는 0)
    private boolean rested;  // 현재 휴식 노드에서 이미 휴식했는지

    @Column(columnDefinition = "TEXT")
    private String deckData; // 형용사+동명사 덱 정보 (JSON 문자열)

    @Column(columnDefinition = "TEXT")
    private String monsterData; // 전투 중인 몬스터 정보 (JSON 문자열, 전투 중이 아니면 null)

    private int currentFloor; // 현재 노드의 층
    private int currentIndex; // 현재 노드의 인덱스

    private int currentAct;      // 현재 진행 중인 Act (맵) 번호
    private int currentNodeType; // 현재 선택한 노드의 타입 (MapNode.NodeType 순서값)

    private int playTime; // 누적 플레이 타임 (초)
}