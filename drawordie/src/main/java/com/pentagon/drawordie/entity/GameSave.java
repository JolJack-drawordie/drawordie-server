package com.pentagon.drawordie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_save")
@Getter @Setter @NoArgsConstructor
public class GameSave {
    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private int currentHp;
    private int currentCost; // 기본 코스트 + 주사위 합산 결과값 저장
    private int mapSeed;     // 지형 복구용 시드

    @Column(columnDefinition = "TEXT")
    private String deckData; // 형용사+동명사 덱 정보 (JSON 문자열)
}