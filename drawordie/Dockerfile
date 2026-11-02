# Draw or Die 서버 이미지 (Render 배포용)
# 1단계에서 Gradle로 jar를 만들고, 2단계에서 그 jar만 가벼운 JRE 이미지에 담아 실행한다.

# ===== 1단계: 빌드 =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradle 설정을 먼저 복사해서 의존성 다운로드를 캐시 (소스만 바뀌면 다시 받지 않음)
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null

# 소스 복사 후 실행용 jar 생성 (테스트는 DB가 필요해서 건너뜀)
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ===== 2단계: 실행 =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# 무료 서버는 메모리가 작아서(512MB) 컨테이너 메모리의 75%까지만 쓰도록 제한
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

# 포트는 Render가 PORT 환경 변수로 알려 줌 (application.properties의 server.port 참고)
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
