# Step 2: そのままコンテナ化 (As-Is Containerization)

## 🎯 このステップのゴール
このステップでは、Step 1で確認したレガシーアプリケーションを、**コードを一切変更せずに** Dockerコンテナとして動かすことを目指します。

これにより、アプリケーションの実行環境をコード化でき、開発者のPCでも、テスト環境でも、本番環境（GKE, Cloud Runなど）でも、同じように動かすことが可能になります。

## 🐳 `Dockerfile` とは？
`Dockerfile` は、コンテナの「設計図」や「レシピ」のようなものです。「どのOSイメージをベースにするか」「どのファイルをコピーするか」「どのコマンドを実行するか」といった手順を記述します。

このプロジェクトには、すでに `Dockerfile` が含まれています。その中身を見ていきましょう。

```Dockerfile
# Use an official Tomcat image with JDK 8
FROM tomcat:9-jdk8-openjdk

# Maintainer (optional)
LABEL maintainer="your-email@example.com"

# Copy the WAR file to Tomcat's webapps directory
# The WAR file is expected to be in the 'target/' directory after 'mvn clean install'
COPY target/legacy-tomcat-app.war /usr/local/tomcat/webapps/legacy-tomcat-app.war

# Expose Tomcat's default HTTP port
EXPOSE 8080

# Command to run Tomcat (default command of the base image)
CMD ["catalina.sh", "run"]
```

- `FROM tomcat:9-jdk8-openjdk`: Docker Hubで公開されている公式の `tomcat` イメージをベースとして使用します。`9-jdk8-openjdk` というタグにより、**Tomcat 9** と **Java 8** がインストールされた環境であることが保証されます。
- `COPY target/legacy-tomcat-app.war ...`: Mavenでビルドして作成した `legacy-tomcat-app.war` ファイルを、コンテナ内のTomcatがアプリケーションを読み込むためのディレクトリ (`/usr/local/tomcat/webapps/`) にコピーします。
- `EXPOSE 8080`: このコンテナは `8080` 番ポートで通信を受け付けます、という宣言です。
- `CMD ["catalina.sh", "run"]`: コンテナが起動したときに実行されるデフォルトのコマンドです。これによりTomcatサーバーが起動します。

## 🛠️ コンテナのビルドと実行
それでは、この `Dockerfile` を使って、実際にコンテナをビルドし、実行してみましょう。

**前提:** お使いのPCに [Docker Desktop](https://www.docker.com/products/docker-desktop/) がインストールされている必要があります。

### 1. アプリケーションのビルド (WARファイルの作成)
まず、コンテナにコピーするための `WAR` ファイルをMavenでビルドします。`hands-on/step-02-containerize` ディレクトリで、以下のコマンドを実行してください。

```bash
mvn clean package
```

これにより、`target/legacy-tomcat-app.war` が作成されます。

### 2. Dockerイメージのビルド
次に、`Dockerfile` を元にコンテナの「イメージ」をビルドします。イメージは、コンテナの雛形のようなものです。

```bash
docker build -t legacy-app:1.0 .
```

- `-t legacy-app:1.0`: ビルドするイメージに `legacy-app` という名前と `1.0` というタグを付けます。
- `.` : カレントディレクトリにある `Dockerfile` を使う、という意味です。

### 3. Dockerコンテナの実行
ビルドしたイメージから、コンテナを起動します。

```bash
docker run --rm -p 8080:8080 legacy-app:1.0
```

- `-p 8080:8080`: あなたのPC（ホスト）の `8080` 番ポートへのアクセスを、コンテナの `8080` 番ポートに転送します。
- `--rm`: コンテナ停止時に、そのコンテナを自動的に削除するためのオプションです。お掃除が楽になります。

### 4. 動作確認
サーバーが起動したら、Webブラウザで以下のURLにアクセスしてください。

[http://localhost:8080/legacy-tomcat-app/](http://localhost:8080/legacy-tomcat-app/)

Step 1と全く同じ画面が表示されれば成功です！アプリケーションのコードは何も変えずに、コンテナとして動かすことができました。

---

これで、アプリケーションのポータビリティが格段に向上しました。次のステップでは、いよいよこのアプリケーションの内部に手を入れて、Spring Bootへと移行させていきます。
