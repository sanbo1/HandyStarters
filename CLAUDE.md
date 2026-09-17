# Handy Starters — MC 26.2

このチェックアウトは MC 26.2 専用。対象バージョンと依存関係は `gradle.properties` が正
（このファイルには転記しない。必ず陳腐化するため）。

MOD全体の設計・モジュール構成は `handy_starters/CLAUDE.md`、マイクラMOD開発全般の話は
`minecraft_mod/CLAUDE.md` を参照。

## このバージョン特有の注意

**このプロジェクトが対象とするMC 26.2は、既知のバージョンとAPIが大きく異なる箇所が多い。**
記憶で判断せず、必ず実物のバイトコードを確認してから実装する。思い込みで書くと、
コンパイルは通るのに挙動が違うという失敗をする（`minecraft_mod/CLAUDE.md` の確認手順を参照）。

Java 25以上が必要。既定のJavaがそれ未満の環境では `JAVA_HOME` を JDK 25+ に向けて実行する。

## MC 26.2 の変更点資料

26.1.x → 26.2 のAPI変更点（リポジトリ外の共通資料。使い方は `minecraft_mod/CLAUDE.md` を参照）:

@../../docs/modding/26.2.md

## 過去に実際に踏んだ罠（MC 26.2のバニラAPI固有）

他バージョンでも同じとは限らないため、ここに留めている。他バージョンに移植する際は
改めてバイトコードで確認すること。

- `Item.Properties` / `BlockBehaviour.Properties` は `setId(ResourceKey)` が**必須**。
  未設定だとコンストラクタで例外になる。
- ブロック破壊での耐久消費には `TOOL` コンポーネントが要る（無いと `Item.mineBlock` が
  早期returnする）。攻撃での消費は `WEAPON` コンポーネント。`TOOL` のルールを空リストに
  すれば「素手と同じ採掘性能だが耐久は減る」が作れる。
- エンチャント可否のタグは `#minecraft:enchantable/<種類>` で、ファイルは
  `data/minecraft/tags/item/enchantable/<種類>.json` に置く。
  `<種類>_enchantable.json` という名前では**誰にも読まれない**。
