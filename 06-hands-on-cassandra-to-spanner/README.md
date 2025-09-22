# ハンズオン: JavaアプリケーションをCassandraからCloud Spannerへ移行する

このリポジトリは、Java Spring BootアプリケーションをApache CassandraからGoogle Cloud Spannerへ移行するプロセスをステップバイステップで学ぶハンズオンラボです。

## シナリオ

このハンズオンでは、歌手(Singer)、そのアルバム(Album)、そしてアルバムの曲(Track)を管理するシンプルな音楽カタログアプリケーションを扱います。この階層的なデータモデル (`Singer` -> `Album` -> `Track`) は、CassandraとSpannerにおけるデータモデリングとクエリのアプローチの違いを明確にするために、意図的に選択されました。

## ハンズオンの構成

このラボは2つの主要なステップに分かれており、それぞれが専用のディレクトリに配置されています。

### [Step 1: 移行元のCassandraアプリケーション](./step-01-java-cassandra/)

ここが私たちの出発点です。これは、Spring Data Cassandraを使用してCassandraデータベースと連携する、完全に動作するSpring Bootアプリケーションです。関連データを別々のテーブルでモデリングし、それらを結合するためにアプリケーション層で複数のクエリを発行する必要がある、という一般的な方法をデモンストレーションします。

**[Step 1から始める](./step-01-java-cassandra/README.md)**

### [Step 2: 移行先のSpannerアプリケーション](./step-02-java-spanner/)

こちらが最終的な、移行が完了したアプリケーションです。Step 1のロジックをリファクタリングし、Google Cloud Spannerを使用するように変更します。ここでの重要なポイントは、親子関係のデータを扱うアプリケーションコードを劇的に簡素化する **Spannerのインターリーブテーブル** を使用することです。

**[Step 2へ進む](./step-02-java-spanner/README.md)**

## 学習目標

このハンズオンを完了することで、以下のことを学びます：

- Cassandraで階層データをモデリングする方法
- Spannerのインターリーブテーブルを使用して同じデータをモデリングする方法
- `spring-boot-starter-data-cassandra` と `spring-cloud-gcp-starter-data-spanner` の実践的な違い
- JavaアプリケーションにおけるSpannerの強力な整合性とリレーショナル機能の利点
- データベースの移行が、よりシンプルで効率的なアプリケーションコードにどのようにつながるか