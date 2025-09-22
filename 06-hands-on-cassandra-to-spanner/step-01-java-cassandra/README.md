# Step 1: 移行元のCassandraアプリケーション

このディレクトリには、ハンズオンの最初のパートが含まれています。Java、Spring Boot、そしてApache Cassandraを使用して構築された、完全に動作するアプリケーションです。これは、私たちのデータベース移行における「移行前」の状態を表します。

## 概要

このアプリケーションは、音楽カタログを管理するためのシンプルなREST APIを提供します。以下の3つのコアコンセプトを中心に構築されています：

- **Singer**: アーティスト
- **Album**: 歌手によってリリースされたアルバム
- **Track**: アルバムに収録されている曲

これらはCassandraの3つの別々のテーブルとして保存されます。ここで注目すべき重要な特徴は、ある歌手の完全なディスコグラフィー（作品一覧）を取得するには、複数のデータベースクエリが必要になるという点です。具体的には、歌手を取得するために1回、その歌手のアルバムを取得するために1回、そして各アルバムの曲を取得するためにアルバムごとに1回ずつクエリが発行されます。これは一般的なパターンであり、Spannerへ移行する際にこの構造がどのように変わるかを見ていきます。

## 実行方法

### 前提条件

- Java 21 (またはそれ以降)
- Apache Maven
- `podman-compose` (または `docker-compose`) がインストールされたPodman (または Docker)

### 1. Cassandraの起動

まず、提供されているcomposeファイルを使用してCassandraデータベースを起動します。これにより、単一ノードのCassandraクラスタがコンテナ内で起動します。

```bash
# 06-hands-on-cassandra-to-spanner/step-01-java-cassandra ディレクトリの中から実行
podman compose up -d
```

`-d` フラグは、コンテナをデタッチモード（バックグラウンド）で実行します。コンテナが完全に起動するまで少し時間がかかる場合があります。

### 2. キースペースの作成

次に、Cassandraコンテナ内でキースペースを手動で作成します。

```bash
podman exec -it cassandra-for-hands-on cqlsh -e "CREATE KEYSPACE IF NOT EXISTS music_keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};"
```

### 3. Spring Bootアプリケーションの実行

Cassandraの準備ができたら、Mavenラッパーを使用してJavaアプリケーションを起動できます。

```bash
./mvnw spring-boot:run
```

アプリケーションは、先ほど起動したCassandraインスタンスに接続し、必要なテーブル (`singers`, `albums`, `tracks`) を自動的に作成します。

## APIの使用方法

`curl` または任意のAPIクライアントを使用してアプリケーションと対話できます。

### 1. 歌手の作成

```bash
SINGER_ID=$(curl -s -X POST http://localhost:8080/api/singers \
-H "Content-Type: application/json" \
-d '{"firstName": "Marc", "lastName": "Johnson"}' | grep -o '"id":"[^"]*"' | cut -d '"' -f 4)

echo "作成された歌手のID: $SINGER_ID"
```

### 2. 歌手のアルバムを作成

```bash
ALBUM_ID=$(curl -s -X POST http://localhost:8080/api/albums \
-H "Content-Type: application/json" \
-d '{ 
  "singerId": "'$SINGER_ID'", 
  "title": "Shades of Blue", 
  "releaseYear": 2023
}' | grep -o '"id":"[^"]*"' | cut -d '"' -f 4)

echo "作成されたアルバムのID: $ALBUM_ID"
```

### 3. アルバムに曲を追加

```bash
curl -X POST http://localhost:8080/api/tracks \
-H "Content-Type: application/json" \
-d '{ 
  "albumId": "'$ALBUM_ID'", 
  "trackNumber": 1, 
  "title": "Ocean Drive"
}'
```

### 4. 歌手のディスコグラフィーをすべて取得

これで、歌手の完全なディスコグラフィー（すべてのアルバムと曲を含む）を取得できます。

```bash
curl http://localhost:8080/api/singers/$SINGER_ID/discography | jq
```

### 5. ユーザーの作成とアルバムの購入

新しく追加されたユーザーと注文の機能を使ってみましょう。

```bash
# ユーザーを作成
USER_ID=$(curl -s -X POST http://localhost:8080/api/users \
-H "Content-Type: application/json" \
-d '{"name": "Yori"}' | grep -o '"id":"[^"]*"' | cut -d '"' -f 4)

echo "作成されたユーザーのID: $USER_ID"

# ユーザーがアルバムを購入
echo "\n--- ユーザー($USER_ID)がアルバム($ALBUM_ID)を購入します ---"
curl -s -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{ 
  "userId": "'$USER_ID'", 
  "albumId": "'$ALBUM_ID'"
}' | jq .

# ユーザーの購入履歴を取得
echo "\n--- ユーザー($USER_ID)の購入履歴 ---"
curl -s http://localhost:8080/api/users/$USER_ID/orders | jq

# アルバムの購入者リストを取得
echo "\n--- アルバム($ALBUM_ID)の購入者リスト ---"
curl -s http://localhost:8080/api/albums/$ALBUM_ID/users | jq
```

## 自動テストの実行方法

このプロジェクトには、アプリケーションの機能をエンドツーエンドで検証する統合テストも含まれています。これらのテストは **Testcontainers** を使用して、テスト専用のCassandraコンテナを自動的に起動・停止します。このため、手動で `podman compose` を実行する必要はありません。

テストを実行するには、単に以下のコマンドを実行してください：

```bash
./mvnw test
```

このコマンドは、コードをコンパイルし、Cassandraコンテナイメージをダウンロード（まだ存在しない場合）、クリーンで一時的なデータベースに対してテストを実行し、その後すべてをシャットダウンします。

## 次のステップ

ベースラインとなるCassandraアプリケーションができたので、次のフェーズに進む準備が整いました： **[Step 2: Google Cloud Spannerへの移行](../step-02-java-spanner)**.