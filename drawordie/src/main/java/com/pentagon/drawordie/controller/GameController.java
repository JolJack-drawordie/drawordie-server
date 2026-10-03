package com.pentagon.drawordie.controller;

import com.pentagon.drawordie.dto.CardDto;
import com.pentagon.drawordie.dto.MonsterDto;
import com.pentagon.drawordie.dto.RankingDto;
import com.pentagon.drawordie.entity.GameResult;
import com.pentagon.drawordie.entity.GameSave;
import com.pentagon.drawordie.entity.User;
import com.pentagon.drawordie.repository.GameResultRepository;
import com.pentagon.drawordie.repository.GameSaveRepository;
import com.pentagon.drawordie.repository.UserRepository;
import com.pentagon.drawordie.service.CardService;
import com.pentagon.drawordie.service.GameSaveService;
import com.pentagon.drawordie.service.MonsterService;
import com.pentagon.drawordie.service.RankingScore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameSaveService gameSaveService;
    private final GameSaveRepository gameSaveRepository;
    private final GameResultRepository gameResultRepository;
    private final UserRepository userRepository;
    private final CardService cardService;
    private final MonsterService monsterService;

    // 모든 필요한 저장소와 서비스를 연결합니다.
    public GameController(GameSaveService gameSaveService,
                          GameSaveRepository gameSaveRepository,
                          GameResultRepository gameResultRepository,
                          UserRepository userRepository,
                          CardService cardService,
                          MonsterService monsterService) {
        this.gameSaveService = gameSaveService;
        this.gameSaveRepository = gameSaveRepository;
        this.gameResultRepository = gameResultRepository;
        this.userRepository = userRepository;
        this.cardService = cardService;
        this.monsterService = monsterService;
    }

    // 세이브/로드/결과 API의 유저 번호는 클라이언트가 보낸 값이 아니라 JWT 토큰에서 꺼냄 (다른 유저 데이터 조작 방지)

    // 🔵 1. 세이브 API: 현재 진행 상태 저장
    @PostMapping("/save")
    public GameSave saveProgress(@AuthenticationPrincipal Long userId,
                                 @RequestParam int masterSeed,
                                 @RequestParam int mapSeed,
                                 @RequestParam int nodeSeed,
                                 @RequestParam int hp,
                                 @RequestParam int shield,
                                 @RequestParam int cost,
                                 @RequestParam String deckData,
                                 @RequestParam(required = false) String monsterData,
                                 @RequestParam int currentFloor,
                                 @RequestParam int currentIndex,
                                 @RequestParam int act,
                                 @RequestParam int nodeType,
                                 @RequestParam(defaultValue = "0") int playTime) {
        return gameSaveService.saveGame(userId,
                masterSeed, mapSeed, nodeSeed,
                hp, shield, cost,
                deckData, monsterData,
                currentFloor, currentIndex,
                act, nodeType, playTime);
    }

    // 🟢 2. 로드 API: 저장된 데이터 불러오기 (유니티 시작 시 호출)
    // 세이브가 없으면 404 (게임 종료 후에는 결과 등록 시 세이브가 삭제됨)
    @GetMapping("/load")
    public ResponseEntity<GameSave> loadProgress(@AuthenticationPrincipal Long userId) {
        return gameSaveRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 🔴 3. 결과 API: 게임 종료 시 랭킹 등록 및 세이브 삭제
    // 클라이언트는 원래 값만 보내고 점수는 서버에서 계산한다. 모든 판을 기록한다.
    @PostMapping("/result")
    public RankingDto.ResultResponse saveResult(@AuthenticationPrincipal Long userId,
                                                @RequestParam boolean cleared,
                                                @RequestParam int reachedAct,
                                                @RequestParam int reachedFloor,
                                                @RequestParam int playTime) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 값 범위 검증
        if (reachedAct < 1 || reachedAct > RankingScore.MAX_ACT
                || reachedFloor < 0 || reachedFloor > RankingScore.MAX_FLOOR
                || playTime < 0
                || (cleared && reachedAct != RankingScore.MAX_ACT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 게임 결과 값입니다.");
        }

        // 결과 객체 생성 및 데이터 세팅
        GameResult result = new GameResult();
        result.setUser(user);
        result.setCleared(cleared);
        result.setReachedAct(reachedAct);
        result.setReachedFloor(reachedFloor);
        result.setPlayTime(playTime);
        result.setScore(RankingScore.calculate(cleared, reachedAct, reachedFloor, playTime));

        // 랭킹 테이블에 저장
        result = gameResultRepository.save(result);

        // 중요: 게임이 완전히 끝났으므로 해당 유저의 중간 세이브 데이터는 삭제합니다.
        if (gameSaveRepository.existsById(userId)) {
            gameSaveRepository.deleteById(userId);
        }

        long rank = gameResultRepository.countByScoreGreaterThan(result.getScore())
                + gameResultRepository.countByScoreAndEndedAtBefore(result.getScore(), result.getEndedAt())
                + 1;

        return new RankingDto.ResultResponse(result.getId(), rank, result.getScore());
    }

    // 🟡 4. 랭킹 조회 API: 상위 100개 기록 (한 유저의 여러 판이 모두 포함됨)
    @GetMapping("/ranking")
    public RankingDto.RankingList getTopRankings() {
        List<GameResult> results = gameResultRepository.findTop100ByOrderByScoreDescEndedAtAsc();

        List<RankingDto.Entry> entries = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            GameResult r = results.get(i);
            entries.add(new RankingDto.Entry(
                    r.getId(),
                    i + 1,
                    r.getUser().getNickname(),
                    r.getScore(),
                    r.isCleared(),
                    r.getReachedAct(),
                    r.getReachedFloor(),
                    r.getPlayTime(),
                    r.getEndedAt()));
        }

        return new RankingDto.RankingList(entries);
    }

    // 5. 전투 시작 시 무작위 카드 5장(형2, 동3) 가져오기 API
    @GetMapping("/start-cards")
    public CardDto.BattleStartHand getBattleStartCards() {
        return cardService.getRandomStartHand();
    }

    @GetMapping("/load-adjectives")
    public CardDto.AdjectiveList getAdjectives(){ return cardService.getAdjectives(); }

    @GetMapping("/load-gerunds")
    public CardDto.GerundList getGerunds(){ return cardService.getGerunds(); }

    @GetMapping("/load-combinations")
    public CardDto.CombinationList getCombinations(){ return cardService.getCombinations(); } //홍성구 추가

    @GetMapping("/load-default-deck")
    public CardDto.DefaultDeckData getDefaultDeck(){ return cardService.getDefaultDeck(); }

    @GetMapping("/load-monsters")
    public MonsterDto.MonsterList loadMonsters() { return monsterService.getMonsters(); }
}