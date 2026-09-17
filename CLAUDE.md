# Handy Starters — MC 1.21.11

このチェックアウトは MC 1.21.11 専用（ブランチ `mc/1.21.11`）。対象バージョンと依存関係は
`gradle.properties` が正（このファイルには転記しない。必ず陳腐化するため）。

MOD全体の設計・モジュール構成は `handy_starters/CLAUDE.md`、マイクラMOD開発全般の話は
`minecraft_mod/CLAUDE.md` を参照。

## このバージョン特有の注意

**MC 1.21.11 は難読化された最後の版で、26.1 以降（main ブランチの 26.2 版）とはビルド構成が異なる。**

- Loom はリマップありの構成（Mojang マッピングを指定）にする。依存は `modImplementation` 系、
  配布用JARは `remapJar` を経由する。26.2 版の `loom-no-remap` 構成をそのまま使わない。
- Java 21 を使う。
- プラグインや依存のバージョンは推測で書かず、公式情報とビルド結果で確認してから反映する。

このブランチは、公式テンプレートから 1.21.11 向けの Fabric・NeoForge 両対応環境を作り、
そこへ 26.2 版（main ブランチ、`../26.2`）を参照して機能を移植する方針で作っている。
**26.2 版のコードは参考であり、1.21.11 で正しいとは限らない。**
記憶や 26.2 版のコードで判断せず、必ず実物のバイトコードを確認してから実装する
（`minecraft_mod/CLAUDE.md` の確認手順を参照）。

MOD ID は `handy_starters`、パッケージは `com.snd.handystarters` で 26.2 版と揃える。

`../26.2`（main ブランチの worktree）は参照のみとし、編集・コミットしない。

## MC 1.21.11 の変更点資料

1.21.10 → 1.21.11 のAPI変更点（リポジトリ外の共通資料。使い方は `minecraft_mod/CLAUDE.md` を参照）:

@../../docs/modding/1.21.11.md

26.2 版からの移植では、1.21.11 より後に入った変更を逆向きに戻す必要がある。
`docs/modding/26.2.md` と `26.1-26.1.2.md` は、移植作業中に必要な箇所だけ参照する（ここでは読み込まない）。

## 過去に実際に踏んだ罠（MC 1.21.11 のバニラAPI固有）

他バージョンでも同じとは限らないため、ここに留める。

- Fabric API（0.141.6+1.21.11）はまだ26.1以降の改名前。`CreativeModeTabEvents` ではなく
  `net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents`（`modifyEntriesEvent(tab).register(entries -> entries.accept(...))`）、
  `FuelValueEvents` ではなく `net.fabricmc.fabric.api.registry.FuelRegistryEvents`（シグネチャ自体は同じ）を使う。
- `ItemPredicate` は `net.minecraft.advancements.criterion.ItemPredicate`（26.2の`advancements.predicates`パッケージ再編はまだ来ていない）。
- データコンポーネント `Weapon` のコンストラクタは `Weapon(int)` の1引数のみ（26.2にある `Weapon(int, float)` は無い）。
  ブロッキング無効化秒数を渡さない用途なら1引数版で置き換えられる。
- `BlockEntityType` のコンストラクタ・`BlockEntitySupplier` インターフェースはpackage-private（26.2で公開化）。
  common側で直接 `new BlockEntityType<>(...)` は書けない。Fabricは `FabricBlockEntityTypeBuilder.create(factory, blocks...).build()`、
  NeoForgeは自身のAccess Transformerで公開されたコンストラクタを使う。**対象ブロックはSupplierのまま渡し、
  NeoForge側では登録イベント発火時まで解決を遅らせること**（`DeferredRegister`は遅延登録前提のため、呼び出し時点で
  `Supplier<Block>#get()` すると "Trying to access unbound value" のNPEになる）。
- 26.2 版で踏んだ罠は `../26.2/CLAUDE.md` にある。1.21.11 でも同じかはバイトコードで確認してから適用する。
