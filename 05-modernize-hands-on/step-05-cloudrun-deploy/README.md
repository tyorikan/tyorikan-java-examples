# Step 5: Cloud Runへのデプロイ

## 🎯 このステップのゴール
ローカル環境でコンテナとして動作させたモダンなSpring Bootアプリケーションを、Google Cloudのフルマネージドなサーバーレス環境である **Cloud Run** にデプロイし、世界中に公開することを目指します。

## ☁️ 前提条件

デプロイを進める前に、お使いの環境で以下の準備が整っていることを確認してください。

1.  **Google Cloudプロジェクト**: 課金が有効になっているGoogle Cloudプロジェクトが必要です。
2.  **gcloud CLI**: [Google Cloud SDK](https://cloud.google.com/sdk/install) がインストールされていること。
3.  **認証**: ターミナルで以下の2つのコマンドを実行し、Google Cloudへの認証が済んでいること。
    ```bash
    gcloud auth login
    gcloud auth application-default login
    ```

準備ができたら、以下の手順に進みましょう。

## 🛠️ デプロイ手順

### Step 5.1: 環境変数の設定とAPIの有効化

まず、作業を効率化するために、ターミナルで環境変数を設定します。`<YOUR_PROJECT_ID>` と `<YOUR_REGION>` をあなたの環境に合わせて書き換えてください（例: `asia-northeast1`）。

```bash
export PROJECT_ID=<YOUR_PROJECT_ID>
export REGION=<YOUR_REGION>
export REPO_NAME=legacy-app-repo

gcloud config set project $PROJECT_ID
```

次に、Cloud RunとArtifact RegistryのAPIを有効化します。

```bash
gcloud services enable run.googleapis.com artifactregistry.googleapis.com
```

### Step 5.2: Artifact Registryリポジトリの作成

コンテナイメージを保存するための場所（リポジトリ）をArtifact Registryに作成します。

```bash
gcloud artifacts repositories create $REPO_NAME \
    --repository-format=docker \
    --location=$REGION
```

### Step 5.3: Dockerイメージのビルドとタグ付け

まず、`hands-on/step-05-cloudrun-deploy` ディレクトリで、これまでと同様にDockerイメージをビルドします。

```bash
docker build -t springboot-app:1.0 .
```

次に、ビルドしたイメージをArtifact Registryにプッシュするために、リポジトリ名を含んだ新しいタグを付けます。

```bash
docker tag springboot-app:1.0 ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/springboot-app:1.0
```

### Step 5.4: Artifact Registryへのイメージプッシュ

ローカルのDockerからArtifact Registryへ認証を設定し、イメージをプッシュ（アップロード）します。

```bash
gcloud auth configure-docker $REGION-docker.pkg.dev
docker push ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/springboot-app:1.0
```

### Step 5.5: Cloud Runへのデプロイ

いよいよデプロイです。Artifact Registryにプッシュしたイメージを使って、Cloud Runサービスを作成・公開します。

```bash
gcloud run deploy legacy-springboot-app \
    --image=${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/springboot-app:1.0 \
    --platform=managed \
    --region=$REGION \
    --allow-unauthenticated
```

- `legacy-springboot-app`: Cloud Runのサービス名です。好きな名前を付けられます。
- `--allow-unauthenticated`: インターネット上の誰でもアクセスできるように設定します。（もしアクセスを制限したい場合は、このオプションを外します）

デプロイには1~2分かかります。`Service [legacy-springboot-app] revision [legacy-springboot-app-00001-xxx] has been deployed and is serving 100 percent of traffic at` のようなメッセージが表示されれば成功です。

### Step 5.6: 動作確認

デプロイが成功すると、ターミナルに **Service URL** が表示されます。そのURLをWebブラウザで開いてみましょう。

ローカルで実行したときと同じ画面が、インターネット上に公開されていることが確認できるはずです。

## 🧹 クリーンアップ (任意)

ハンズオンが終わったら、余計な課金が発生しないように、作成したリソースを削除しておきましょう。

```bash
# Cloud Runサービスの削除
gcloud run services delete legacy-springboot-app --region=$REGION --quiet

# Artifact Registryのイメージを削除
gcloud artifacts docker images delete ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/springboot-app:1.0 --quiet

# Artifact Registryのリポジトリを削除
gcloud artifacts repositories delete $REPO_NAME --location=$REGION --quiet
```

---


## 🎉 ハンズオン完了！

お疲れ様でした！

このハンズオンを通じて、典型的なレガシーJavaアプリケーションを分析し、段階的にリファクタリングしながら、最終的にCloud Runというモダンなクラウドネイティブ環境にデプロイするまでの流れを体験しました。

この経験が、今後のアプリケーションモダナイゼーションの旅の助けになれば幸いです。