# Step 5: モダンなコンテナイメージの構築

## 🎯 このステップのゴール
Spring Bootに移行したアプリケーションを、より軽量でセキュアなコンテナイメージとして構築することを目指します。そのために、コンテナ化のベストプラクティスである **「マルチステージビルド」** を採用した `Dockerfile` を作成しました。

## ✨ マルチステージビルドのメリット

`Dockerfile` の中に `FROM` 命令を複数記述することで、ビルドのステージを分割する手法です。主なメリットは以下の2つです。

1.  **イメージサイズの削減**: 最終的なコンテナイメージには、ソースコードやビルドツール（Maven, JDK）など、アプリケーションの実行に不要なファイルが含まれなくなります。これにより、イメージサイズが劇的に小さくなり、ストレージコストの削減やデプロイの高速化に繋がります。

2.  **セキュリティの向上**: 攻撃の足がかりとなりうるビルドツールやソースコードがイメージ内に存在しないため、コンテナのセキュリティが向上します。

## 🐳 新しい `Dockerfile` の解説

今回作成した `Dockerfile` は、「ビルドステージ」と「実行ステージ」の2段階で構成されています。

```Dockerfile
# ===== ビルドステージ =====
# アプリケーションのビルドに必要なMavenとJDK 21を含むイメージを使用
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# ===== 実行ステージ =====
# Java 21の実行環境(JRE)のみを含む軽量なイメージを使用
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/springboot-app-1.0-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "springboot-app-1.0-SNAPSHOT.jar"]
```

- **`FROM maven:3.9.6-eclipse-temurin-21 AS builder`**: 第1ステージ（ビルドステージ）を開始します。`builder` という名前を付けています。MavenとJDK 21が使える信頼性の高いEclipse Temurinイメージです。
- **`RUN mvn dependency:go-offline`**: `pom.xml`だけを先にコピーして依存ライブラリをダウンロードしています。こうすることで、ソースコードを変更しても、依存関係が変わらなければキャッシュが利用され、ビルドが高速になります。
- **`RUN mvn package -DskipTests`**: アプリケーションをビルドし、実行可能JARを作成します。
- **`FROM eclipse-temurin:21-jre`**: 第2ステージ（実行ステージ）を開始します。Java 21の実行環境（JRE）のみを含む非常に軽量なイメージです。
- **`COPY --from=builder ...`**: `builder` ステージから、ビルドされたJARファイルだけをコピーしてきます。ここがマルチステージビルドのキモです。
- **`ENTRYPOINT [...]`**: コンテナ起動時に、Javaアプリケーションを実行するコマンドを指定します。

## 🛠️ コンテナのビルドと実行

`hands-on/step-05-modern-containerization` ディレクトリで、以下のコマンドを実行してください。

### 1. Dockerイメージのビルド

```bash
docker build -t springboot-app:1.0 .
```

- `-t springboot-app:1.0`: イメージに `springboot-app` という名前と `1.0` というタグを付けます。

### 2. Dockerコンテナの実行

```bash
docker run --rm -p 8080:8080 springboot-app:1.0
```

### 3. 動作確認

Webブラウザで [http://localhost:8080/](http://localhost:8080/) にアクセスし、これまでと同じ画面が表示されることを確認してください。

## ⚖️ イメージサイズの比較 (推奨)

ターミナルで以下のコマンドを実行して、Step 2で作成したイメージと、今回作成したイメージのサイズを比較してみましょう。

```bash
docker images
```

| REPOSITORY       | TAG | IMAGE ID     | CREATED      | SIZE   |
|------------------|-----|--------------|--------------|--------|
| **springboot-app** | 1.0 | (some id)    | (some time)  | **~250MB** |
| legacy-app       | 1.0 | (another id) | (some time)  | ~500MB |

`legacy-app` (Tomcatベース) と比べて、`springboot-app` のイメージサイズが半分近くになっていることが確認できるはずです。これがマルチステージビルドの大きな効果です。

---

これで、Spring Bootアプリケーションに最適化された、モダンなコンテナイメージが完成しました。
次のステップでは、このコンテナをいよいよGoogle Cloudの **Cloud Run** にデプロイします。
