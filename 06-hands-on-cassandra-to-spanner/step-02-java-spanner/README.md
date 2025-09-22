# Step 2: Google Cloud Spannerへの移行

このディレクトリには、ハンズオンの第2部、そして最終パートが含まれています。Step 1のアプリケーションを、Cassandraの代わりにGoogle Cloud Spannerを使用するように完全に移行したものです。

これは、私たちの移行における「移行後」の状態を表し、Javaアプリケーションに対するSpannerのリレーショナル機能の強力さを示します。

## 移行における主要な変更点

CassandraからSpannerへの移行には、いくつかの主要な変更が伴いました。

### 1. 依存関係 (`pom.xml`)

Cassandraドライバを、Spanner用のSpring Cloud GCPスターターに置き換えました。

**変更前:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-cassandra</artifactId>
</dependency>
```

**変更後:**
```xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-data-spanner</artifactId>
</dependency>
```

### 2. 設定 (`application.properties`)

接続プロパティを、Spannerデータベースを指すように更新しました。

**変更前:**
```properties
spring.data.cassandra.keyspace-name=music_keyspace
spring.data.cassandra.contact-points=127.0.0.1
spring.data.cassandra.port=9042
spring.data.cassandra.local-datacenter=dc1
```

**変更後:**
```properties
spring.cloud.gcp.spanner.project-id=[YOUR_GCP_PROJECT_ID]
spring.cloud.gcp.spanner.instance-id=[YOUR_SPANNER_INSTANCE_ID]
spring.cloud.gcp.spanner.database=[YOUR_SPANNER_DATABASE_NAME]
```

### 3. データモデリング: インターリーブテーブル

これが最も重要な変更です。アプリケーションが手動でJOINする必要がある別々のテーブルの代わりに、Spannerの **インターリーブテーブル** を使用します。これにより、子行（アルバムなど）が親行（歌手など）と物理的に同じ場所に配置され、クエリのパフォーマンスと論理的なグループ化が大幅に向上します。

- `Album` は `Singer` にインターリーブされます。
- `Track` は `Album` にインターリーブされます。

これは、エンティティのアノテーション (`@Interleaved`) と複合主キーの使用に反映されています。

### 4. サービスレイヤー: シンプルになったロジック

この移行で最も素晴らしい点は、アプリケーションコードがシンプルになることです。`getSingerDiscography` メソッドの違いを見てください：

**変更前 (Cassandra):** 複数の手動データベースクエリ
```java
// 1. 歌手を取得
Singer singer = singerRepository.findById(singerId).orElseThrow(...);
// 2. その歌手の全アルバムを取得
List<Album> albums = albumRepository.findBySingerId(singerId);
// 3. 各アルバムについて、その曲をすべて取得
List<AlbumWithTracksDTO> albumWithTracksList = albums.stream()
    .map(album -> {
        List<Track> tracks = trackRepository.findByAlbumId(album.getId());
        return new AlbumWithTracksDTO(album, tracks);
    })
    .collect(Collectors.toList());
```

**変更後 (Spanner):** クリーンで単一のデータベース呼び出し！
```java
// 単一の呼び出しで、歌手とそのすべてのインターリーブされた子孫を取得
Singer singer = singerRepository.findById(singerId).orElseThrow(...);

// エンティティグラフをDTOグラフにマッピング。これ以上のDB呼び出しは不要。
// ... DTOマッピングロジック ...
```
エンティティリストの`@Interleaved`アノテーションのおかげで、Spring Data Spannerはオブジェクトグラフ全体を1回の効率的な操作で取得します。

## Spannerのセットアップ (前提条件)

このアプリケーションを実行するには、Google CloudプロジェクトにSpannerインスタンスとデータベースをセットアップする必要があります。

### 1. gcloudの認証

GCPへの認証が済んでいることを確認してください。

```bash
gcloud auth application-default login
```

### 2. プロジェクトIDの設定

```bash
export GCP_PROJECT=[YOUR_GCP_PROJECT_ID]
gcloud config set project $GCP_PROJECT
```

### 3. APIの有効化

```bash
gcloud services enable spanner.googleapis.com
```

### 4. Spannerインスタンスの作成

```bash
export SPANNER_INSTANCE=music-instance
gcloud spanner instances create $SPANNER_INSTANCE \
    --config=regional-us-central1 \
    --description="Music Catalog Instance" \
    --processing-units=100
```

### 5. データベースとスキーマの作成

データベースとインターリーブテーブルを作成します。

```bash
export SPANNER_DATABASE=music_db
gcloud spanner databases create $SPANNER_DATABASE --instance=$SPANNER_INSTANCE

# スキーマの作成
gcloud spanner databases ddl update $SPANNER_DATABASE --instance=$SPANNER_INSTANCE --ddl=' 
  CREATE TABLE singers (
    singerId   STRING(36) NOT NULL,
    firstName  STRING(1024),
    lastName   STRING(1024),
  ) PRIMARY KEY (singerId);

  CREATE TABLE albums (
    singerId    STRING(36) NOT NULL,
    albumId     STRING(36) NOT NULL,
    title       STRING(1024),
    releaseYear INT64,
  ) PRIMARY KEY (singerId, albumId),
  INTERLEAVE IN PARENT singers ON DELETE CASCADE;

  CREATE TABLE tracks (
    singerId    STRING(36) NOT NULL,
    albumId     STRING(36) NOT NULL,
    trackId     STRING(36) NOT NULL,
    title       STRING(1024),
    trackNumber INT64,
  ) PRIMARY KEY (singerId, albumId, trackId),
  INTERLEAVE IN PARENT albums ON DELETE CASCADE;

  CREATE TABLE Users (
    userId   STRING(36) NOT NULL,
    name     STRING(1024),
  ) PRIMARY KEY (userId);

  CREATE TABLE UserAlbumOrders (
    orderId         STRING(36) NOT NULL,
    userId          STRING(36) NOT NULL,
    singerId        STRING(36) NOT NULL,
    albumId         STRING(36) NOT NULL,
    orderTimestamp  TIMESTAMP NOT NULL,
  ) PRIMARY KEY (orderId);

  CREATE INDEX UserAlbumOrdersByUserId ON UserAlbumOrders(userId);

  CREATE INDEX UserAlbumOrdersByAlbumId ON UserAlbumOrders(singerId, albumId);
'
```

## 実行方法

1.  **環境変数の設定**: アプリケーションは環境変数からSpannerの接続情報を読み取ります。`~/.bash_profile` や `~/.zshrc` に設定するか、実行時に直接指定してください。

    ```bash
    export SPANNER_PROJECT_ID="[YOUR_GCP_PROJECT_ID]"
    export SPANNER_INSTANCE_ID="music-instance" # or your instance id
    export SPANNER_DATABASE_NAME="music_db" # or your database name
    ```

2.  **アプリの実行**:
    ```bash
    ./mvnw spring-boot:run
    ```

## APIの使用方法

アルバムと曲を作成するためのAPIエンドポイントが、よりRESTfulになるように変更されています。

```bash
# 歌手の作成
SINGER_ID=$(curl -s -X POST http://localhost:8080/api/singers -H "Content-Type: application/json" -d '{"firstName": "Elena", "lastName": "Rodriguez"}' | jq -r .singerId)

# アルバムの作成
ALBUM_ID=$(curl -s -X POST http://localhost:8080/api/singers/$SINGER_ID/albums -H "Content-Type: application/json" -d '{"title": "City Lights", "releaseYear": 2024}' | jq -r .albumId)

# 曲の作成
curl -X POST http://localhost:8080/api/singers/$SINGER_ID/albums/$ALBUM_ID/tracks -H "Content-Type: application/json" -d '{"trackNumber": 1, "title": "Neon Dreams"}'

# ディスコグラフィーの取得
curl http://localhost:8080/api/singers/$SINGER_ID/discography | jq
```

### ユーザーと注文のAPI

```bash
# ユーザーの作成
USER_ID=$(curl -s -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"name": "Yori"}' | jq -r .userId)
echo "作成されたユーザーのID: $USER_ID"

# ユーザーがアルバムを購入
curl -s -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{ 
  "userId": "'$USER_ID'", 
  "singerId": "'$SINGER_ID'", 
  "albumId": "'$ALBUM_ID'" 
}' | jq .

# ユーザーの購入履歴を取得
curl -s http://localhost:8080/api/users/$USER_ID/orders | jq

# アルバムの購入者リストを取得
curl -s http://localhost:8080/api/singers/$SINGER_ID/albums/$ALBUM_ID/users | jq
```

## 自動テストの実行方法

このテストは、設定した本物のSpannerデータベースに対して実行されます。

```bash
./mvnw test
```

## まとめ

この移行は、特に階層データを扱う際に、Spannerのようなマネージド・リレーショナルデータベースを使用することによる、開発者の生産性とパフォーマンスの向上を示しています。
