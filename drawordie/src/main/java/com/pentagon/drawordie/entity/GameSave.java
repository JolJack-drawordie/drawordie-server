package com.pentagon.drawordie.entity;

import jakarta.persistence.*;
import lombok.*;

// 진행 중인 판의 세이브 (유저당 하나, 키 = 유저 번호). 판이 끝나 결과를 등록하면 삭제된다.
// 나중에 추가된 값(maxHp, maxCost, relicData, eliteKills 등)은 예전 세이브에서 0 / null로 남아 있을 수 있다.
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

    // 같은 시드로 맵 / 전투를 똑같이 다시 만든다
    private int masterSeed; // 판 전체의 최상위 시드
    private int mapSeed;    // 맵 지형 시드
    private int nodeSeed;   // 지금 선택한 노드의 시드 (몬스터 / 덱 섞기 / 보상)

    private int currentHp;
    private int maxHp;         // 유물로 늘어난 값 포함
    private int currentShield;
    private int currentCost;   // 이번 턴 남은 코스트
    private int maxCost;       // 이번 턴 최대 코스트 (에너지 게이지 최대치)
    private boolean rested;    // 지금 휴식 노드에서 이미 휴식했는지

    @Column(columnDefinition = "TEXT")
    private String deckData;    // 덱 상태 JSON (더미 / 버린 더미 / 손패 / 슬롯)

    @Column(columnDefinition = "TEXT")
    private String monsterData; // 전투 중인 몬스터 상태 JSON (전투 중이 아니면 null)

    @Column(columnDefinition = "TEXT")
    private String relicData;   // 보유 유물 key 목록 JSON

    private int currentFloor;    // 지금 노드의 층
    private int currentIndex;    // 지금 노드의 층 안 번호

    private int currentAct;      // 진행 중인 Act
    private int currentNodeType; // 지금 노드 종류 (클라이언트 MapNode.NodeType 순서값)

    private int playTime;   // 누적 플레이 시간 (초)
    private int eliteKills; // 이번 판에서 처치한 중간보스 수
}