# 베이스 이미지로 JDK 17 버전을 사용합니다.
FROM openjdk:17-jdk-slim

# JAR 파일이 위치할 디렉토리를 지정합니다.
ARG JAR_FILE=build/libs/*.jar

# JAR 파일을 컨테이너 내부로 복사합니다.
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행 포트를 외부에 노출합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 실행할 명령어를 지정합니다.
ENTRYPOINT ["java","-jar","/app.jar"]