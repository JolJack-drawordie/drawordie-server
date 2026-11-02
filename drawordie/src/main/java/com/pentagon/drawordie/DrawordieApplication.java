package com.pentagon.drawordie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Draw or Die 게임 서버 시작점 (회원 / 세이브 / 랭킹 / 카드·몬스터·유물 데이터 API)
@SpringBootApplication
public class DrawordieApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrawordieApplication.class, args);
	}

}
