package com.pentagon.drawordie.controller;

import com.pentagon.drawordie.dto.CardDto;
import com.pentagon.drawordie.dto.MonsterDto;
import com.pentagon.drawordie.dto.RankingDto;
import com.pentagon.drawordie.dto.RelicDto;
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
import com.pentagon.drawordie.service.RelicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

// 게임 API: 세이브 / 로드 / 결과 등록 / 랭킹, 카드·몬스터·유물 데이터 조회
// 세이브 / 로드 / 결과의 유저 번호는 클라이언트가 보낸 값이 아니라 JWT 토큰에서 꺼낸다 (다른 유저 데이터 조작 방지)
@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameSaveService gameSaveService;
    private final GameSaveRepository gameSaveRepository;
    private final GameResultRepository gameResultRepository;
    private final UserRepository userRepository;
    private final CardService cardService;
    private final MonsterService monsterService;
    private final RelicService relicService;

    public GameController(GameSaveService gameSaveService,
                          GameSaveRepository gameSaveRepository,
                          GameResultRepository gameResultRepository,
                          UserRepository userRepository,
                          CardService cardService,
                          MonsterService monsterService,
                          RelicService relicService) {
        this.gameSaveService = gameSaveService;
        this.gameSaveRepository = gameSaveRepository;
        this.gameResultRepository = gameResultRepository;
        this.userRepository = userRepository;
        this.cardService = cardService;
        this.monsterService = monsterService;
        this.relicService = relicService;
    }

    // 세이브: 지금 진행 상태 저장 (나중에 추가된 값은 예전 클라이언트를 위해 기본값을 둠)
    @PostMapping("/save")
    public GameSave saveProgress(@AuthenticationPrincipal Long userId,
                                 @RequestParam int masterSeed,
                                 @RequestParam int mapSeed,
                                 @RequestParam int nodeSeed,
                                 @RequestParam int hp,
                                 @RequestParam(defaultValue = "0") int maxHp,
                                 @RequestParam int shield,
                                 @RequestParam int cost,
                                 @RequestParam(defaultValue = "0") int maxCost,
                                 @RequestParam String deckData,
                                 @RequestParam(required = false) String monsterData,
                                 @RequestParam(required = false) String relicData,
                                 @RequestParam int currentFloor,
                                 @RequestParam int currentIndex,
                                 @RequestParam int act,
                                 @RequestParam int nodeType,
                                 @RequestParam(defaultValue = "false") boolean rested,
                                 @RequestParam(defaultValue = "0") int playTime,
                                 @RequestParam(defaultValue = "0") int eliteKills) {
        return gameSaveService.saveGame(userId,
                masterSeed, mapSeed, nodeSeed,
                hp, maxHp, shield, cost, maxCost,
                deckData, monsterData, relicData,
                currentFloor, currentIndex,
                act, nodeType, rested, playTime, eliteKills);
    }

    // 로드: 저장된 진행 상태 (세이브가 없으면 404, 판이 끝나 결과를 등록하면 세이브는 지워짐)
    @GetMapping("/load")
    public ResponseEntity<GameSave> loadProgress(@AuthenticationPrincipal Long userId) {
        return gameSaveRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 결과 등록: 판이 끝나면 랭킹에 기록하고 세이브를 지운 뒤 순위를 돌려준다
    // 클라이언트는 원래 값만 보내고 점수는 서버에서 계산한다 (모든 판을 기록)
    @PostMapping("/result")
    public RankingDto.ResultResponse saveResult(@AuthenticationPrincipal Long userId,
                                                @RequestParam boolean cleared,
                                                @RequestParam int reachedAct,
                                                @RequestParam int reachedFloor,
                                                @RequestParam int playTime,
                                                @RequestParam(defaultValue = "0") int eliteKills) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 말이 안 되는 값은 거부 (클리어했다면 마지막 Act여야 함)
        if (reachedAct < 1 || reachedAct > RankingScore.MAX_ACT
                || reachedFloor < 0 || reachedFloor > RankingScore.MAX_FLOOR
                || playTime < 0
                || eliteKills < 0 || eliteKills > RankingScore.MAX_ELITE_KILLS
                || (cleared && reachedAct != RankingScore.MAX_ACT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 게임 결과 값입니다.");
        }

        GameResult result = new GameResult();
        result.setUser(user);
        result.setCleared(cleared);
        result.setReachedAct(reachedAct);
        result.setReachedFloor(reachedFloor);
        result.setPlayTime(playTime);
        result.setEliteKills(eliteKills);
        result.setScore(RankingScore.calculate(cleared, reachedAct, reachedFloor, eliteKills, playTime));

        result = gameResultRepository.save(result);

        // 판이 끝났으므로 이어하기 세이브 삭제
        if (gameSaveRepository.existsById(userId)) {
            gameSaveRepository.deleteById(userId);
        }

        // 순위 = 나보다 위인 기록 수 + 1
        long rank = gameResultRepository.countByScoreGreaterThan(result.getScore())
                + gameResultRepository.countByScoreAndEndedAtBefore(result.getScore(), result.getEndedAt())
                + 1;

        return new RankingDto.ResultResponse(result.getId(), rank, result.getScore());
    }

    // 랭킹: 상위 100개 기록 (한 유저의 여러 판이 모두 들어갈 수 있음)
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
                    r.getEliteKills(),
                    r.getPlayTime(),
                    r.getEndedAt()));
        }

        return new RankingDto.RankingList(entries);
    }

    // ===== 게임 데이터 조회 (클라이언트가 시작할 때 받아 감) =====

    @GetMapping("/load-adjectives")
    public CardDto.AdjectiveList getAdjectives() { return cardService.getAdjectives(); }

    @GetMapping("/load-gerunds")
    public CardDto.GerundList getGerunds() { return cardService.getGerunds(); }

    @GetMapping("/load-combinations")
    public CardDto.CombinationList getCombinations() { return cardService.getCombinations(); }

    @GetMapping("/load-default-deck")
    public CardDto.DefaultDeckData getDefaultDeck() { return cardService.getDefaultDeck(); }

    @GetMapping("/load-monsters")
    public MonsterDto.MonsterList loadMonsters() { return monsterService.getMonsters(); }

    @GetMapping("/load-relics")
    public RelicDto.RelicList loadRelics() { return relicService.getRelics(); }
}