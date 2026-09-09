package com.pentagon.drawordie.controller;

import com.pentagon.drawordie.dto.CardDto;
import com.pentagon.drawordie.dto.MonsterDto;
import com.pentagon.drawordie.entity.GameResult;
import com.pentagon.drawordie.entity.GameSave;
import com.pentagon.drawordie.entity.User;
import com.pentagon.drawordie.repository.GameResultRepository;
import com.pentagon.drawordie.repository.GameSaveRepository;
import com.pentagon.drawordie.repository.UserRepository;
import com.pentagon.drawordie.service.CardService;
import com.pentagon.drawordie.service.GameSaveService;
import com.pentagon.drawordie.service.MonsterService;
import org.springframework.web.bind.annotation.*;

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

    // 🔵 1. 세이브 API: 현재 진행 상태 저장
    @PostMapping("/save")
    public GameSave saveProgress(@RequestParam Long userId,
                                 @RequestParam int hp,
                                 @RequestParam int cost,
                                 @RequestParam int mapSeed,
                                 @RequestParam String deckData) {
        return gameSaveService.saveGame(userId, hp, cost, mapSeed, deckData);
    }

    // 🟢 2. 로드 API: 저장된 데이터 불러오기 (유니티 시작 시 호출)
    @GetMapping("/load")
    public GameSave loadProgress(@RequestParam Long userId) {
        return gameSaveRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("저장된 게임 세이브가 없습니다."));
    }

    // 🔴 3. 결과 API: 게임 종료 시 랭킹 등록 및 세이브 삭제
    @PostMapping("/result")
    public String saveResult(@RequestParam Long userId,
                             @RequestParam int score,
                             @RequestParam int maxStage,
                             @RequestParam int playTime,
                             @RequestParam boolean cleared) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 결과 객체 생성 및 데이터 세팅
        GameResult result = new GameResult();
        result.setUser(user);
        result.setScore(score);
        result.setMaxStage(maxStage);
        result.setPlayTime(playTime);
        result.setCleared(cleared);

        // 랭킹 테이블에 저장
        gameResultRepository.save(result);

        // 중요: 게임이 완전히 끝났으므로 해당 유저의 중간 세이브 데이터는 삭제합니다.
        if (gameSaveRepository.existsById(userId)) {
            gameSaveRepository.deleteById(userId);
        }

        return "게임 결과가 성공적으로 등록되었습니다. 기존 세이브는 삭제되었습니다.";
    }

    // 🟡 4. 랭킹 조회 API: 전 세계 유저 점수 리스트
    @GetMapping("/ranking")
    public List<GameResult> getTopRankings() {
        return gameResultRepository.findAllByOrderByScoreDesc();
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