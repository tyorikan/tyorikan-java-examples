# ===== ビルドステージ =====
# アプリケーションのビルドに必要なMavenとJDK 21を含むイメージを使用
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# 作業ディレクトリを設定
WORKDIR /app

# まずpom.xmlをコピーし、依存関係をダウンロードする
# (ソースコードの変更よりも頻度が低いため、Dockerのレイヤーキャッシュが効きやすくなる)
COPY pom.xml .
RUN mvn dependency:go-offline

# ソースコード全体をコピー
COPY src ./src

# アプリケーションをビルドする
# (-DskipTestsでテストをスキップし、ビルド時間を短縮)
RUN mvn package -DskipTests

# ===== 実行ステージ =====
# Java 21の実行環境(JRE)のみを含む軽量なイメージを使用
FROM eclipse-temurin:21-jre

# 作業ディレクトリを設定
WORKDIR /app

# ビルドステージから、ビルドされたJARファイルのみをコピー
COPY --from=builder /app/target/springboot-app-1.0-SNAPSHOT.jar .

# コンテナが8080番ポートでリッスンすることを指定
EXPOSE 8080

# コンテナ起動時にアプリケーションを実行するコマンド
ENTRYPOINT ["java", "-jar", "springboot-app-1.0-SNAPSHOT.jar"]
