package com.pentagon.drawordie.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pentagon.drawordie.dto.RelicDto;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 유물 데이터 (resources/data/Relic.json)
@Service
public class RelicService {

    private List<RelicDto.Info> allRelics = new ArrayList<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 서버가 켜질 때 유물 표를 읽어 둔다 (읽지 못하면 서버가 시작되지 않음)
    @PostConstruct
    public void loadRelicData() {
        try {
            RelicDto.RelicList relicList = objectMapper.readValue(
                    new ClassPathResource("data/Relic.json").getInputStream(),
                    RelicDto.RelicList.class
            );

            if (relicList.getRelics() == null) {
                throw new IllegalStateException(
                        "Relic.json에 relics 목록이 없습니다."
                );
            }

            allRelics = relicList.getRelics();

            System.out.println(
                    "🟢 [RelicService] 유물 데이터 로드 완료! 총 유물: "
                            + allRelics.size() + "개"
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Relic.json 로드 실패", e
            );
        }
    }

    // 원본 목록이 바뀌지 않도록 복사해서 돌려준다
    public RelicDto.RelicList getRelics() {
        return new RelicDto.RelicList(
                new ArrayList<>(allRelics)
        );
    }
}
