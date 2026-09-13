# Handy Starters

序盤の不便さを解消する小物を追加する Minecraft MOD。Fabric と NeoForge の両対応で、
実装は `common` に集約している。

## 対象バージョン

**このチェックアウトは 1つのMCバージョン専用。** 対象バージョンと依存関係は
`gradle.properties` が正（このファイルには転記しない。必ず陳腐化するため）。

別のMCバージョンに対応するときは、**そのバージョン専用のフォルダを別に用意し、
別セッションで作業する**。1つのセッションで複数バージョンを扱うと、
別バージョンのAPIを正しいと思い込んで適用する事故が起きる。

## 実装前に必ず読むこと

**MCのAPIは記憶で判断せず、必ず実物のバイトコードを確認してから実装する。**
このプロジェクトが対象とするMCは、既知のバージョンとAPIが大きく異なる箇所が多い。
思い込みで書くと、コンパイルは通るのに挙動が違うという失敗をする。

```
# MCのjarを探す（Gradleのキャッシュ配下にある）
find ~/.gradle/caches/fabric-loom -name "minecraft-client.jar"

# クラスの構造を見る
javap -p -classpath <jar> net.minecraft.world.level.block.HopperBlock

# 実装の中身まで見る（定数値や呼び出し順を確認したいとき）
javap -p -c -classpath <jar> net.minecraft.world.level.block.HopperBlock
```

バニラのjsonリソース（レシピ・タグ・ルートテーブル・モデル）も同じjarから
`unzip -p <jar> data/minecraft/...` で取り出して参照できる。書式を推測しない。

### 過去に実際に踏んだ罠

- `Item.Properties` / `BlockBehaviour.Properties` は `setId(ResourceKey)` が**必須**。
  未設定だとコンストラクタで例外になる。
- ブロック破壊での耐久消費には `TOOL` コンポーネントが要る（無いと `Item.mineBlock` が
  早期returnする）。攻撃での消費は `WEAPON` コンポーネント。`TOOL` のルールを空リストに
  すれば「素手と同じ採掘性能だが耐久は減る」が作れる。
- エンチャント可否のタグは `#minecraft:enchantable/<種類>` で、ファイルは
  `data/minecraft/tags/item/enchantable/<種類>.json` に置く。
  `<種類>_enchantable.json` という名前では**誰にも読まれない**。
- コンテナ間でアイテムを移動したら**転送先にも `setChanged()` を呼ぶ**。既存スタックへの
  合流時は `setItem()` が呼ばれないため、呼ばないと保存対象にならずアイテムが消える。
- NeoForge では `DeferredHolder.get()` を mod construction 中に呼べない。
  イベントやコールバックまで解決を遅らせる。
- Fabric側で、共通の初期化経路からクライアント専用クラスを**参照するだけでも**
  専用サーバーが落ちる（クラスロード時に `net.minecraft.client.Minecraft` に触れるため）。
- **インターフェースを実装しただけでは機能せず、別途レジストリ登録が要る**ものがある。
  実例：`ProjectileItem` を実装してもディスペンサーは発射してくれない。挙動は
  `DispenserBlock.DISPENSER_REGISTRY` から引かれ、未登録アイテムのフォールバックは
  装備品・硫黄キューブ・スポーンエッグしか見ないため、それ以外はアイテムとして
  排出されるだけになる。`DispenserBlock.registerBehavior()` での登録が必要。
  新しい要素を追加するときは「実装した型が実際にどこから参照されるか」を
  バイトコードで追い、レジストリ経由なら登録処理も忘れないこと。

## モジュール構成

| | 役割 |
|---|---|
| `common/` | 共通ロジック。実装は基本ここに書く |
| `fabric/` | Fabric API で `ModPlatform` を実装 |
| `neoforge/` | NeoForge標準機能で `ModPlatform` を実装 |

ローダー差の吸収は自前の `common/.../platform/ModPlatform.java` で行う。
**Architectury API には依存しない**（Architectury Loom はビルド時のみ使用）。
ローダー固有のAPIが必要になったら `ModPlatform` にメソッドを足して両方で実装する。
common から特定ローダーのクラスを直接参照しないこと。

エントリポイントは `HandyStarters.init(ModPlatform)`（common）、`HandyStartersFabric`、
`HandyStartersNeoForge`。

## ビルドと動作確認

```
./gradlew build --offline
```

Java 25以上が必要。既定のJavaがそれ未満の環境では `JAVA_HOME` を JDK 25+ に向けて実行する。

配布用JARは `fabric/build/libs/` と `neoforge/build/libs/` に出る。ファイル名にMCバージョンが
入るので、他バージョンのビルドと取り違えない。`-dev` `-sources` が付かないものが配布用。

```
./gradlew :fabric:runServer --offline
./gradlew :neoforge:runServer --offline
```

`Done (` が出れば起動成功。**必ず両ローダーで確認する**（片方だけ壊れる不具合が何度も出ている）。
サーバーはポート25565を掴んだまま常駐するので、連続起動すると `FAILED TO BIND TO PORT` に
なる。前のプロセスを止めてから次を起動する。

レシピ・実績・タグを変更したら、ログの `Loaded N recipes` / `Loaded N advancements` の
増減が想定通りかを確認する。意図しない巻き込みの検出に有効。

**モデルとテクスチャはクライアント専用アセットなので、サーバー起動では検証できない。**
見た目の確認は実機で行ってもらう必要がある。

## リソースの配置

```
common/src/main/resources/
  assets/handy_starters/
    items/<name>.json         アイテムのモデル定義（新形式）
    models/item/<name>.json   モデル本体
    models/block/<name>.json  ブロックモデル（従来形式のまま）
    blockstates/<name>.json
    lang/{en_us,ja_jp}.json
    textures/{item,block}/
  data/handy_starters/
    recipe/<name>.json
    advancement/<name>.json   レシピ解放用
    loot_table/blocks/<name>.json
    tags/item/<name>.json
  data/minecraft/tags/...     バニラのタグへの追記
```

- アイテムは `items/` と `models/item/` の**2ファイル**が要る。ブロックは従来通り
  `blockstates/` + `models/block/`。
- **ブロックには loot_table が必須。** 無いと破壊しても何も落ちない。
- 見た目が完全な立方体でないブロックには `.noOcclusion()` を付ける。付けないと隣接
  ブロックの面が誤って描画されず、地面が透けて見える。
- ブロックモデルには `"parent": "minecraft:block/block"` を付ける。付けないと表示サイズの
  既定値が効かず、ドロップ品などが巨大になる。

## 運用ルール

**`git commit` と `git push` は、その都度明示的な指示があるまで実行しない。**
作業は完了報告までとし、変更は未コミットのまま残す。ステージングするときは
`git add -A` ではなくパスを明示する。

ローカルの絶対パスやユーザー名をこのリポジトリ内のファイルに書かない。
