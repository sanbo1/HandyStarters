# Handy Starters — MC 1.21.1

このチェックアウトは MC 1.21.1 専用（ブランチ `mc/1.21.1`）。対象バージョンと依存関係は
`gradle.properties` が正（このファイルには転記しない。必ず陳腐化するため）。

MOD全体の設計・モジュール構成は `handy_starters/CLAUDE.md`、マイクラMOD開発全般の話は
`minecraft_mod/CLAUDE.md` を参照。

## このバージョン特有の注意

**MC 1.21.1 は難読化された版で、26.1 以降（main ブランチの 26.2 版）とはビルド構成が異なる。**
ビルド構成は 1.21.11 版（ブランチ `mc/1.21.11`、`../1.21.11`）と同系統。

- Loom はリマップありの構成（Mojang マッピングを指定）にする。依存は `modImplementation` 系、
  配布用JARは `remapJar` を経由する。26.2 版の `loom-no-remap` 構成をそのまま使わない。
- Java 21 を使う。
- プラグインや依存のバージョンは推測で書かず、公式情報とビルド結果で確認してから反映する。
- JDK 21 がシェルの PATH・`JAVA_HOME` に無い環境がある（PATH の java が 8 など）。その場合は
  IDE が管理する JDK 21 の場所を確認し、コマンド単位で `JAVA_HOME` を指定して Gradle を実行する
  （`JAVA_HOME` の恒久設定は環境変更なので事前に確認する）。

このブランチは、公式テンプレート（Architectury Template Generator）から 1.21.1 向けの
Fabric・NeoForge 両対応環境を作り、そこへ既存版の機能を移植する方針で作っている。

- 移植の主な参考は **1.21.11 版**（`../1.21.11`）。ビルド構成・難読化・Java 21 が共通で、
  1.21.1 に最も近い。
- 機能仕様の正は **26.2 版**（`main` ブランチ、`../26.2`）。1.21.11 版と食い違う場合は
  26.2 版の仕様に合わせる（ただしAPIの書き方は 1.21.1 のバイトコードに従う）。
- **1.21.11 版・26.2 版のコードは参考であり、1.21.1 で正しいとは限らない。**
  1.21.1 は 1.21.2〜1.21.11 の大きな変更より前の版なので、APIの差は大きい。
  記憶や他版のコードで判断せず、必ず実物のバイトコードを確認してから実装する
  （`minecraft_mod/CLAUDE.md` の確認手順を参照）。

MOD ID は `handy_starters`、パッケージは `com.snd.handystarters` で他バージョンと揃える。

`../26.2`（`main` ブランチの worktree）、`../1.21.11`、`../26.3` は参照のみとし、
編集・コミットしない。

依存範囲（`fabric.mod.json` の `minecraft`、`neoforge.mods.toml` の `minecraft` の
`versionRange`）には上限を付け、このビルドが対応しない新しいMCバージョンを含めない
（1.21.11 版で、上限なしの範囲を Modrinth が「全後続バージョン対応」と解釈した教訓）。

## MC 1.21.1 の変更点資料

1.20.6 → 1.21 / 1.21.1 のAPI変更点（リポジトリ外の共通資料。使い方は `minecraft_mod/CLAUDE.md` を参照）:

@../../docs/modding/1.21-1.21.1.md

1.21.11 版からの移植では、1.21.1 より後に入った変更を逆向きに戻す必要がある。
次の資料を新しい順に、移植作業中に必要な箇所だけ参照する（ここでは読み込まない）:

`docs/modding/1.21.11.md` → `1.21.9-1.21.10.md` → `1.21.6-1.21.8.md` → `1.21.5.md` →
`1.21.4.md` → `1.21.2-1.21.3.md`

26.2 版のコードを直接参照する場合は、さらに `26.2.md`・`26.1-26.1.2.md` の変更も戻す必要がある。

## 過去に実際に踏んだ罠（MC 1.21.1 のバニラAPI固有）

他バージョンでも同じとは限らないため、ここに留める。

- **`BaseEntityBlock` の既定の描画形状が `RenderShape.INVISIBLE`**。`getRenderShape` で `MODEL` を返さないと
  ブロックが見えない（1.21.1 の `HopperBlock` も上書きしている）。1.21.11 では既定が `MODEL` なので、
  新しい版からの移植では気づきにくい。
- `BlockEntityType` のコンストラクタ（3引数。末尾は DataFixer の `Type` で、MOD では null）と `Builder` は
  public だが、引数の `BlockEntitySupplier` が package-private なので common からは組み立てられない。
  NeoForge は AT で公開、Fabric は `FabricBlockEntityTypeBuilder` を使う。このため
  `ModPlatform#registerBlockEntityType` を経由する（1.21.11 版と同じ構成）。
- **NeoForge 21.1.1 の `LootTableLoadEvent` には `getKey()` も `getRegistries()` も無い**（`getName()` が
  `ResourceLocation` を返すだけ）。エンチャントの Holder をコードで解決できないので、粗い繊維のドロップは
  `loot_table/inject/coarse_fiber.json` に書き、`NestedLootTable.lootTableReference` で参照するプールだけを追加している。
- `Weapon` コンポーネントが無い。攻撃ごとの耐久消費は `Item#hurtEnemy`（true を返す）と
  `postHurtEnemy`（`hurtAndBreak`）で実装する（`PoleSawItem`。1.21.1 の `DiggerItem` と同じ作り）。
  `Tool` は3引数（「クリエイティブで壊せるか」の引数は無い）。ツール素材は `Tiers`（enum）で、銅は無い。
- 1.21.1 に存在しないアイテム（例：1.21.11 で追加された `minecraft:wooden_spear`）をタグに書くと、
  そのタグ全体の読み込みに失敗する。新しい版からタグを移植するときは、各要素が存在するか確認する。
- 開発用サーバー（`runServer`・IntelliJ の実行構成）は、`stop` の後も JVM が終わらない。開発時だけ使われる
  Architectury Transformer 5.2.92 が作る非デーモンのスレッドプールが残るため（JFR の `jdk.ThreadStart` で特定。
  配布用 JAR には含まれない）。Gradle 経由では終了コード 1 で FAILED と表示されるが、成否はログ
  （`Done (`・`All dimensions are saved`・ERROR が無いこと）で判断する。
- NeoForge の開発環境は、起動のたびに `generated_<ハッシュ>` という MOD ID の一時 jar を作るため、
  2回目以降の起動で `generated_xxx (version 1 -> MISSING)` の WARN が出る。無害。
- 1.21.11 版で踏んだ罠は `../1.21.11/CLAUDE.md`、26.2 版は `../26.2/CLAUDE.md` にある。
  1.21.1 でも同じかはバイトコードで確認してから適用する。
