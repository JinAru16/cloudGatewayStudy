FROM openjdk:21
WORKDIR /app

# 2️⃣ JAR 파일 복사 ###
COPY ./build/libs/*.jar app.jar

# 3️⃣ EXPOSE (컨테이너에서 열릴 포트)
EXPOSE 8090

# 4️⃣ 실행 명령어
CMD ["java", "-jar", "app.jar"]