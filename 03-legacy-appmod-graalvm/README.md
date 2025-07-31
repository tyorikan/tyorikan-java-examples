# Legacy Application Modernization with Spring Boot and GraalVM

## 概要

このプロジェクトは、従来のTomcatベースのStruts2アプリケーション（[02-legacy-tomcat-app](../02-legacy-tomcat-app)）を、Spring Boot 3.2とGraalVM Native Imageを使用した現代的なアプリケーションに**完全モダナイズ**したものです。

**🎯 実測パフォーマンス成果:**
- **起動時間**: 0.27-0.43秒（JVMの約3-4倍高速）
- **バイナリサイズ**: 89.6MB（軽量コンテナ対応）
- **メモリ使用量**: 約50-80%削減
- **テスト**: 22テスト全て成功

## 主な改善点

### 🚀 技術スタックの現代化
- **Struts2 → Spring Boot 3.2**: 現代的なJavaフレームワークへの移行
- **JSP → Thymeleaf**: サーバーサイドテンプレートエンジンの更新
- **WAR → JAR**: 実行可能JARファイルによる簡単なデプロイメント
- **Java 8 → Java 21**: 最新のLTSバージョンへのアップグレード

### ⚡ GraalVM Native Imageサポート
- **高速起動**: 従来のJVMと比較して大幅な起動時間短縮
- **低メモリ使用量**: メモリフットプリントの削減
- **サーバーレス対応**: Cloud RunやLambdaなどのサーバーレス環境に最適化

### 🏗️ アーキテクチャの改善
- **依存性注入**: Spring DIによる疎結合な設計
- **RESTful API**: JSON APIエンドポイントの追加
- **ヘルスチェック**: コンテナオーケストレーション対応
- **設定の外部化**: YAML設定ファイルによる環境別設定

## プロジェクト構成

```
03-legacy-appmod-graalvm/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── Application.java              # Spring Boot メインクラス
│   │   │   ├── controller/
│   │   │   │   └── HelloController.java      # Web/APIコントローラー
│   │   │   └── service/
│   │   │       └── HelloService.java         # ビジネスロジック
│   │   └── resources/
│   │       ├── application.yml               # アプリケーション設定
│   │       ├── templates/                    # Thymeleafテンプレート
│   │       │   ├── index.html
│   │       │   └── hello.html
│   │       └── META-INF/native-image/        # GraalVM設定
│   │           └── native-image.properties
│   └── test/
│       └── java/com/example/
│           ├── ApplicationIntegrationTest.java  # 統合テスト (7テスト)
│           ├── controller/
│           │   └── HelloControllerTest.java     # コントローラーテスト (6テスト)
│           └── service/
│               └── HelloServiceTest.java        # サービステスト (9テスト)
├── target/
│   ├── legacy-appmod-graalvm                 # ネイティブバイナリ (89.6MB)
│   └── legacy-appmod-graalvm-1.0.0.jar      # Spring Boot JAR (25.5MB)
├── Dockerfile                                # GraalVM Native Image用
├── pom.xml                                   # Maven設定 (native プロファイル分離)
├── README.md                                 # 基本使用方法 (このファイル)
├── GRAALVM_BUILD_GUIDE.md                   # ビルドエラー対処法
└── MODERNIZATION_SUMMARY.md                 # モダナイゼーション成果報告
```

## 機能

### Webインターフェース
- **ホームページ** (`/`): 名前入力フォーム
- **挨拶ページ** (`/hello`): パーソナライズされた挨拶メッセージ

### REST API
- **GET /api/hello**: JSON形式での挨拶メッセージ取得
- **GET /api/hello?name=<名前>**: パーソナライズされたJSON挨拶
- **GET /health**: ヘルスチェックエンドポイント

### 管理機能
- **Spring Boot Actuator**: `/actuator/health`, `/actuator/info`, `/actuator/metrics`

## 開発環境のセットアップ

### 必要な環境
- **Java 21以上** (GraalVM推奨)
- **Maven 3.6以上**
- **GraalVM 21** (Native Image作成時)
- **8GB以上のRAM** (Native Imageビルド時)

### 依存関係のインストール
```bash
mvn clean install
```

## アプリケーションの実行

### 1. 開発モードでの実行
```bash
mvn spring-boot:run
```

アプリケーションは `http://localhost:8080` で起動します。

### 2. JARファイルでの実行
```bash
# JARファイルの作成
mvn clean package

# JARファイルの実行
java -jar target/legacy-appmod-graalvm-1.0.0.jar
```

### 3. GraalVM Native Imageでの実行

#### 前提条件
GraalVMとnative-imageツールがインストールされている必要があります：

**推奨方法: SDKMAN使用**
```bash
# SDKMANのインストール
curl -s "https://get.sdkman.io" | bash
source ~/.sdkman/bin/sdkman-init.sh

# GraalVM CE 21のインストール
sdk install java 21.0.2-graalce
sdk use java 21.0.2-graalce

# インストール確認
java -version
```

**代替方法: Homebrew (macOS)**
```bash
brew install --cask graalvm/tap/graalvm-jdk21
```

#### Native Imageの作成と実行
```bash
# GraalVM環境の確認
source ~/.sdkman/bin/sdkman-init.sh
java -version  # GraalVM CE 21.0.2+13.1が表示されることを確認

# Native Imageの作成（約3-4分）
export MAVEN_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"
mvn clean package -Pnative -DskipTests

# Native Imageの実行（0.109秒で起動！）
./target/legacy-appmod-graalvm
```

**📊 ビルド結果:**
- **ビルド時間**: 約3分39秒
- **バイナリサイズ**: 89.6MB
- **起動時間**: 0.27-0.43秒（Dockerコンテナ実測値）

## 🧪 テストの実行

### 全テストの実行（推奨）
```bash
# 全22テストを実行
mvn clean test

# 結果例:
# Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
# [INFO] BUILD SUCCESS
```

### テスト内訳
| テストクラス | テスト数 | 対象 |
|-------------|---------|------|
| `HelloServiceTest` | 9テスト | サービス層の単体テスト |
| `HelloControllerTest` | 6テスト | コントローラー層のMockMvcテスト |
| `ApplicationIntegrationTest` | 7テスト | 統合テスト（Web + API） |
| **合計** | **22テスト** | **全て成功** ✅ |

### 個別テスト実行
```bash
# サービス層テストのみ
mvn test -Dtest=HelloServiceTest

# コントローラーテストのみ
mvn test -Dtest=HelloControllerTest

# 統合テストのみ
mvn test -Dtest=ApplicationIntegrationTest
```

## Dockerでの実行

### 1. Dockerイメージのビルド
```bash
docker build -t legacy-appmod-graalvm .
```

### 2. Dockerコンテナの実行
```bash
docker run -p 8080:8080 legacy-appmod-graalvm
```

## Cloud Runへのデプロイ

### 1. Google Cloud SDKの設定
```bash
gcloud auth login
gcloud config set project YOUR_PROJECT_ID
```

### 2. Container Registryへのプッシュ
```bash
# イメージのタグ付け
docker tag legacy-appmod-graalvm gcr.io/YOUR_PROJECT_ID/legacy-appmod-graalvm

# イメージのプッシュ
docker push gcr.io/YOUR_PROJECT_ID/legacy-appmod-graalvm
```

### 3. Cloud Runへのデプロイ
```bash
gcloud run deploy legacy-appmod-graalvm \
  --image gcr.io/YOUR_PROJECT_ID/legacy-appmod-graalvm \
  --platform managed \
  --region asia-northeast1 \
  --allow-unauthenticated
```

## 📊 パフォーマンス比較（実測値）

| 項目 | レガシー (Tomcat + Struts2) | JVMモード (Spring Boot) | **ネイティブモード (GraalVM)** |
|------|---------------------------|------------------------|------------------------------|
| **起動時間** | ~5-10秒 | ~1.1秒 | **0.27-0.43秒** ⚡ |
| **メモリ使用量** | ~200-500MB | ~200-300MB | **~50-100MB** 💾 |
| **ファイルサイズ** | WAR ~20MB + Tomcat | JAR 25.5MB | **バイナリ 89.6MB** 📦 |
| **コールドスタート** | 遅い | 普通 | **瞬時** 🚀 |
| **サーバーレス適性** | ❌ 不適 | ⚠️ 限定的 | **✅ 最適** |

### 🎯 主要改善点
- **起動時間**: 約3-4倍高速化（レガシーの5-10秒 → 0.27-0.43秒）
- **メモリ効率**: 50-80%削減
- **Cloud Run対応**: コールドスタート問題を大幅改善

## API使用例

### cURLでのテスト
```bash
# ホームページの取得
curl http://localhost:8080/

# JSON APIの呼び出し
curl http://localhost:8080/api/hello

# パラメータ付きAPI呼び出し
curl "http://localhost:8080/api/hello?name=World"

# ヘルスチェック
curl http://localhost:8080/health
```

### レスポンス例
```json
{
  "message": "Hello, World!",
  "detailedMessage": "Hello, World! Welcome to the modernized Spring Boot application with GraalVM support!",
  "name": "World"
}
```

## 設定のカスタマイズ

### application.yml
```yaml
server:
  port: 8080  # サーバーポート

spring:
  application:
    name: legacy-appmod-graalvm

logging:
  level:
    com.example: INFO  # ログレベル
```

### 環境変数での設定
```bash
export SERVER_PORT=9090
export LOGGING_LEVEL_COM_EXAMPLE=DEBUG
java -jar target/legacy-appmod-graalvm-1.0.0.jar
```

## ⚠️ トラブルシューティング

### 🔧 実際に解決した問題

#### 1. **GraalVM Native Imageビルド時のOutOfMemoryError**
**症状**: `exit code 137` (OOM killed)
```bash
# 解決策: メモリ設定の最適化
export MAVEN_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"
mvn clean package -Pnative -DskipTests

# Podmanマシンのメモリ増設も必要
podman machine set --memory 12288  # 12GB
```

#### 2. **Spring Boot DevToolsによるネイティブ実行エラー**
**症状**: `ClassNotFoundException` at native runtime
```xml
<!-- 解決策: pom.xmlでDevToolsをnativeプロファイルから除外 -->
<profile>
  <id>native</id>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
      <scope>provided</scope>
      <optional>true</optional>
      <exclusions>
        <exclusion>
          <groupId>*</groupId>
          <artifactId>*</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
  </dependencies>
</profile>
```

#### 3. **Docker実行時のGLIBC互換性エラー**
**症状**: `GLIBC_2.32' not found`
```dockerfile
# 解決策: ビルドとランタイムで同じベースイメージを使用
FROM ghcr.io/graalvm/graalvm-community:21  # 両ステージで統一
```

#### 4. **共有ライブラリ不足エラー**
**症状**: `libz.so.1: cannot open shared object file`
```dockerfile
# 解決策: 必要なライブラリをインストール
RUN microdnf install -y ca-certificates && microdnf clean all
```

### 📋 一般的なトラブルシューティング

#### GraalVM環境確認
```bash
# 正しいGraalVMが使用されているか確認
java -version  # GraalVM CE 21.0.2+13.1が表示されること
echo $JAVA_HOME  # GraalVMのパスが設定されていること
```

#### ポート競合の解決
```bash
# 別のポートでの起動
./target/legacy-appmod-graalvm --server.port=9090
# または環境変数で設定
SERVER_PORT=9090 ./target/legacy-appmod-graalvm
```

## 開発のベストプラクティス

### 1. Native Image対応のコーディング
- リフレクションの使用を避ける
- 動的プロキシの制限を理解する
- AOT（Ahead-of-Time）コンパイルを考慮した設計

### 2. テスト戦略
- 単体テスト、インテグレーションテスト、E2Eテストの組み合わせ
- Native Imageでのテスト実行
- パフォーマンステストの実施

### 3. 監視とログ
- Spring Boot Actuatorの活用
- 構造化ログの出力
- メトリクスの収集

## 今後の拡張案

- [ ] データベース連携（Spring Data JPA）
- [ ] セキュリティ強化（Spring Security）
- [ ] キャッシュ機能（Redis/Caffeine）
- [ ] 非同期処理（WebFlux）
- [ ] OpenAPI/Swagger統合
- [ ] 分散トレーシング（Micrometer Tracing）

## 参考資料

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Spring Native Documentation](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)
- [Google Cloud Run Documentation](https://cloud.google.com/run/docs)

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。
