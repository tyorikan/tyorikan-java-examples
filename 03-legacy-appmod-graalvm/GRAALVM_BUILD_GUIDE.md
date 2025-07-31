# GraalVM Native Image Build Guide

## 概要
このガイドでは、native-maven-pluginを使用してGraalVMネイティブイメージをビルドする際のメモリ不足エラーの回避方法と最適化設定について説明します。

## メモリ要件
GraalVMネイティブイメージのビルドには大量のメモリが必要です：
- **最小要件**: 8GB RAM
- **推奨**: 16GB RAM以上
- **コンテナ環境**: 10GB以上のメモリ割り当て

## ビルド方法

### 1. ローカルビルド
```bash
# 環境変数を設定してビルド
export MAVEN_OPTS="-Xmx4g -XX:MaxMetaspaceSize=1g"
export NATIVE_IMAGE_OPTS="-J-Xmx8g -J-XX:MaxDirectMemorySize=2g"

# ネイティブイメージビルド
mvn clean package -Pnative -DskipTests
```

### 2. Dockerビルド
```bash
# Dockerイメージをビルド（メモリ設定済み）
podman build -t legacy-appmod-graalvm:native .

# または、メモリ制限を明示的に設定
podman build --memory=12g -t legacy-appmod-graalvm:native .
```

## メモリ不足エラーの対処法

### エラー例
```
Exit code 137 - Container killed due to memory exhaustion
java.lang.OutOfMemoryError: Java heap space
```

### 対処法1: Podman/Docker メモリ増加
```bash
# Podman machine のメモリを増加（初回設定時）
podman machine init --memory=12288 --cpus=4

# 既存のmachineのメモリを増加
podman machine stop
podman machine set --memory=12288
podman machine start

# Docker Desktop の場合はGUIでメモリ設定を変更
```

### 対処法2: Maven設定の調整
`pom.xml`の設定を調整：
```xml
<buildArgs>
    <buildArg>-J-Xmx6g</buildArg>          <!-- より少ないメモリで試行 -->
    <buildArg>-J-XX:MaxDirectMemorySize=1g</buildArg>
    <buildArg>--gc=serial</buildArg>        <!-- メモリ効率の良いGC -->
</buildArgs>
```

### 対処法3: 段階的ビルド
```bash
# 1. 依存関係のダウンロード
mvn dependency:go-offline -B

# 2. コンパイル
mvn clean compile -DskipTests

# 3. ネイティブイメージ作成
mvn package -Pnative -DskipTests
```

### 対処法4: ビルド最適化フラグ
```bash
# より少ないメモリでビルド
mvn clean package -Pnative -DskipTests \
  -Dnative.build.args="--gc=serial,-J-Xmx4g,-J-XX:MaxDirectMemorySize=1g"
```

## 設定ファイル解説

### native-image.properties
`src/main/resources/META-INF/native-image/native-image.properties`
```properties
# メモリ最適化設定
Args = -J-Xmx8g \
       -J-XX:MaxDirectMemorySize=2g \
       --gc=serial \
       --no-fallback
```

### pom.xml設定
```xml
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
    <configuration>
        <buildArgs>
            <buildArg>-J-Xmx8g</buildArg>
            <buildArg>--gc=serial</buildArg>
            <buildArg>--no-fallback</buildArg>
        </buildArgs>
    </configuration>
</plugin>
```

## パフォーマンス最適化

### ビルド時間短縮
```bash
# 並列ビルドを無効化（メモリ使用量削減）
mvn -T1 clean package -Pnative -DskipTests

# テストをスキップ
mvn clean package -Pnative -DskipTests -Dmaven.test.skip=true
```

### イメージサイズ最適化
```xml
<buildArgs>
    <buildArg>--no-fallback</buildArg>
    <buildArg>-Ob</buildArg>  <!-- 最適化レベル -->
    <buildArg>--gc=serial</buildArg>
</buildArgs>
```

## トラブルシューティング

### よくあるエラーと解決法

1. **`gu` tool not found**
   - GraalVM JDKを使用していることを確認
   - `JAVA_HOME`がGraalVMを指していることを確認

2. **ClassNotFoundException during build**
   - リフレクション設定ファイルを追加
   - `--initialize-at-build-time`フラグを使用

## Docker実行時エラーの対処法

### 実際に解決した問題

#### 1. GLIBC互換性エラー
**エラー例**:
```
/app/application: /lib/aarch64-linux-gnu/libc.so.6: version `GLIBC_2.32' not found
/app/application: /lib/aarch64-linux-gnu/libc.so.6: version `GLIBC_2.34' not found
```

**原因**: ビルド環境とランタイム環境のGLIBCバージョン不一致

**解決策**:
```dockerfile
# Dockerfileでビルドとランタイムで同じベースイメージを使用
FROM ghcr.io/graalvm/graalvm-community:17 AS builder
# ...
FROM ghcr.io/graalvm/graalvm-community:17  # ランタイムも同じイメージ
```

#### 2. 共有ライブラリ不足エラー
**エラー例**:
```
/app/application: error while loading shared libraries: libz.so.1: cannot open shared object file
```

**解決策**:
```dockerfile
# 必要なライブラリをインストール
RUN microdnf install -y ca-certificates && microdnf clean all
```

#### 3. 最終的な動作確認
```bash
# コンテナ実行（成功例）
podman run -p 8080:8080 legacy-appmod-graalvm
# → Started Application in 0.27-0.43 seconds

# APIテスト
curl http://localhost:8080/api/hello
# → {"message":"Hello, World!","detailedMessage":"..."}
```

3. **Build timeout**
   - ビルドタイムアウトを延長
   - より多くのCPUコアを割り当て

### デバッグ方法
```bash
# 詳細ログでビルド
mvn clean package -Pnative -DskipTests -X

# ネイティブイメージビルドの詳細情報
mvn clean package -Pnative -DskipTests \
  -Dnative.build.args="--verbose"
```

## 代替案

メモリ不足でネイティブイメージビルドができない場合：

1. **JVMベースのDockerイメージ**
   ```bash
   mvn clean package -DskipTests
   docker build -f Dockerfile.jvm -t legacy-appmod-graalvm:jvm .
   ```

2. **クラウドビルド**
   - GitHub ActionsやGitLab CIでより多くのメモリを使用
   - Google Cloud Build等のクラウドサービスを利用

3. **ローカルマシンでのビルド**
   - コンテナではなくローカル環境で直接ビルド
   - より多くのメモリが利用可能

## 推奨環境

- **開発環境**: 16GB RAM, 4+ CPU cores
- **CI/CD環境**: 32GB RAM, 8+ CPU cores
- **コンテナ環境**: 12GB+ memory limit

このガイドに従って設定することで、GraalVMネイティブイメージのビルドを成功させることができます。
