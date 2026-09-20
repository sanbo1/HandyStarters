# Handy Starters — MC 26.3

このチェックアウトは MC 26.3 専用（ブランチ `mc/26.3`）。対象バージョンと依存関係は
`gradle.properties` が正（このファイルには転記しない。必ず陳腐化するため）。

MOD全体の設計・モジュール構成は `handy_starters/CLAUDE.md`、マイクラMOD開発全般の話は
`minecraft_mod/CLAUDE.md` を参照。

## このバージョン特有の注意

**Architectury Template Generator にはまだ 26.3 向けのテンプレートが無い。** そのため
このブランチのビルド構成は、26.2 版（`main` ブランチ、`../26.2`）を手で更新して作っている。
テンプレート由来の差分として実際に変えたのは次の3点だけで、それ以外の構成
（`loom-no-remap`、architectury-plugin、shadow、Java 25）は 26.2 版と同一。

- Gradle wrapper: 9.5.1 → **9.6.0**（Fabric公式ブログ 2026-09-15 の推奨）
- Loom: **1.17 系のまま**（同ブログが 26.3 でも Loom 1.17 を指定。`loom-no-remap` の
  リリースも 1.17 系が最新）
- 依存3つを 26.3 対応の最古版に固定（内訳と根拠は `gradle.properties` のコメント）

Java 25以上が必要（MC 26.3 の version manifest の `javaVersion` は 25。26.2 と同じ）。
既定のJavaがそれ未満の環境では `JAVA_HOME` を JDK 25+ に向けて実行する。

**26.3 の MC jar・neoform・Gradle 9.6.0 はまだ Gradle キャッシュに無い。**
初回ビルドは `--offline` を付けずに実行してダウンロードさせる必要がある。

`../26.2`（`main` ブランチの worktree）は参照のみとし、編集・コミットしない。
**26.2 版のコードは参考であり、26.3 で正しいとは限らない。** 記憶や 26.2 版のコードで
判断せず、必ず実物のバイトコードを確認してから実装する
（`minecraft_mod/CLAUDE.md` の確認手順を参照）。

MOD ID は `handy_starters`、パッケージは `com.snd.handystarters` で他バージョンと揃える。

`common/` のコードは 26.3 向けに移植済み（両ローダーで `runServer` が `Done (` まで到達することを確認）。

## MC 26.3 の変更点資料

26.2 → 26.3 のAPI変更点（リポジトリ外の共通資料。使い方は `minecraft_mod/CLAUDE.md` を参照）:

@../../docs/modding/26.3.md

資料の出典である NeoForged primer はドラフトで、NeoForge 26.3 固有の変更は「要確認」のまま。
食い違いに気づいたらバイトコードを正とし、資料の修正を提案する。

## 過去に実際に踏んだ罠（MC 26.3 のバニラAPI固有）

26.2 版で踏んだ罠は `../26.2/CLAUDE.md` にある。26.3 でも同じかはバイトコードで
確認してから適用すること。26.3 で新しく踏んだものは以下。

- **燃料の燃焼時間は生の定数で渡さない。** バニラの `cooking/time_*` プロバイダーは
  定数ではなく「値 ÷ (燻製器・溶鉱炉なら2)」という式で、燻製器での燃焼時間半減が
  そこに入っている。`Item.Properties#cookingFuel(ResourceKey)` でバニラのキーを
  参照すれば挙動が一致する（本MODは wood_items_large=200 / dry_plants=100 を参照）。
  詳細と実値の一覧は `docs/modding/26.3.md` の燃料の項。
- **ブロックの loot_table JSON の書式が変わった。** `conditions`（配列）→ `condition`（単数、
  判別キーは `type`）、`functions`（配列）→ `modifier`（単数）、`rolls` が float → int。
  **ブロックには loot_table が必須**なので、直し忘れると破壊しても何も落ちなくなる。
- **NeoForge の `mods.toml` は `logoFile` が非推奨**になり、正方形アイコンは `iconFile`、
  横長バナーは `bannerFile` に分かれた。放置すると起動時に
  `Mods loaded with 1 issues` の警告が出る。
- **26.3 では `Loaded N recipes` のログ行が出ない**（レシピがデータパックレジストリ化され
  `RegistryDataLoader` 経由になったため）。`Loaded N advancements` は残っているので、
  レシピの件数確認はログでは行えない。
