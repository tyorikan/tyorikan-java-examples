# Step 3: Spring Bootへの移行

## 🎯 このステップのゴール
このステップでは、レガシーアプリケーションの心臓部であるフレームワークを、Struts 1.x からモダンな **Spring Boot** へと移行（リファクタリング）します。これにより、開発体験、実行効率、保守性が大幅に向上します。

## ✨ 主な変更点
今回の移行で行った主な変更は以下の通りです。

### 1. `pom.xml` の刷新
- **Spring Bootのバージョンアップ**: JDK 21を正式にサポートする **Spring Boot 3.2.0** へ、Javaのバージョンも **21** へと引き上げました。
- **Struts/古いSpringライブラリの削除**: `struts2-*` や `spring-context` などの依存関係を削除しました。
- **Spring Boot Starterの導入**: `spring-boot-starter-parent` を親プロジェクトとし、`spring-boot-starter-web` (Web機能)、`spring-boot-starter-thymeleaf` (ビュー機能)、`spring-boot-starter-test` (テスト機能) を追加しました。これにより、依存関係の管理が劇的にシンプルになります。
- **パッケージングを `jar` へ**: Webサーバー（Tomcat）を内包した単体で実行可能な `jar` ファイルを作成するように変更しました。これにより、別途Tomcatなどを用意する必要がなくなります。

### 2. XML設定ファイルの撤廃
- `web.xml`, `struts.xml`, `applicationContext.xml` といったXMLベースの設定ファイルをすべて削除しました。
- 代わりに、`@SpringBootApplication` や `@Controller` などの **アノテーション** を使って、Javaコードで設定を記述する方式に移行しました。

### 3. Javaコードの移行
- **`Application.java`**: `@SpringBootApplication` アノテーションを持つ、アプリケーションの起動クラスを新設しました。
- **`HelloController.java`**: Strutsの `Action` クラスに代わり、`@Controller` アノテーションを持つクラスを作成しました。`@GetMapping` や `@PostMapping` でURLとメソッドを直接マッピングします。
- **`HelloService.java`**: `@Service` アノテーションを付け、SpringのDI（依存性注入）コンテナに管理されるようにしました。

### 4. ビューの移行 (JSP → Thymeleaf)
- `src/main/webapp` ディレクトリを廃止し、JSPファイルをすべて削除しました。
- 代わりに、`src/main/resources/templates` ディレクトリを作成し、Spring Bootと親和性の高い **Thymeleaf** を使った `index.html` と `hello.html` を配置しました。

### 5. テストコードの刷新
- 古いテストコードをすべて削除し、Spring Bootのテストフレームワークを使って書き直しました。
- `MockMvc` を使ってコントローラの動作をテストするなど、よりモダンで信頼性の高いテストを実装しました。

## 🛠️ ビルドと実行
アプリケーションの実行方法が、以前よりずっとシンプルになりました。

### 1. アプリケーションのビルド
`hands-on/step-03-springboot-migration` ディレクトリで、以下のコマンドを実行してください。

```bash
mvn clean package
```

成功すると、`target/springboot-app-1.0-SNAPSHOT.jar` というファイルが作成されます。

### 2. アプリケーションの実行
作成された `jar` ファイルを、`java` コマンドで直接実行します。

```bash
java -jar target/springboot-app-1.0-SNAPSHOT.jar
```

もしくは、開発中は以下のMavenコマンドでも起動できます。こちらの方が便利です。

```bash
mvn spring-boot:run
```

### 3. 動作確認
サーバーが起動したら、Webブラウザで以下のURLにアクセスしてください。（パスが `/legacy-tomcat-app` から `/` に変わっている点に注意してください）

[http://localhost:8080/](http://localhost:8080/)

これまでと同じ画面が表示され、同じように動作することを確認してください。

## ✅ テストの実行
更新されたテストは、以下のコマンドで実行できます。

```bash
mvn test
```

`BUILD SUCCESS` と表示されれば、モダナイズされたアプリケーションが期待通りに動作することが保証されます。

---

お疲れ様でした！これで、レガシーアプリケーションからモダンなSpring Bootアプリケーションへの移行が完了です。
次のステップでは、この新しいアプリケーションを、よりクラウドネイティブな形でコンテナ化していきます。