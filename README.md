# Item Alchemy (English)
## Overview
This MOD adds philosopher's stone etc. which can be equivalently exchanged with EMC system like ProjectE.

## Download
- https://www.curseforge.com/minecraft/mc-mods/item-alchemy-fabric
- https://modrinth.com/mod/item-alchemy

## Wiki
- https://wikichree.com/pitan76/?Item+Alchemy/en

## Contributor (Thanks!)
- [Developer & Designer] OffsetMonkey538 Improved the textures, bug fix
- [Developer] mymai1208 Implemented team function and charge function, bug fix
- [Developer] anthonymendez TBD
- [Designer] Murderman25 Provided the textures
- [Translator] 54sda Translated to Chinese (zh_cn.json)

## Developing an Add-on

### Maven
https://maven.pitan76.net/v/#net/pitan76/itemalchemy/ <br />
Check the latest version on the above link.

- gradle.properties
```properties
# check these on https://maven.pitan76.net/v/#net/pitan76/
mcpitanlib_version=+1.18.2:3.0.3
itemalchemy_version=1.0.6
```

----

- build.gradle
```groovy
repositories {
    maven { url "https://maven.pitan76.net/" }
}

dependencies {
    modImplementation "net.pitan76:mcpitanlib:${project.mcpitanlib_version}"
    modImplementation "net.pitan76:itemalchemy:${project.itemalchemy_version}"
}

```

## License
- MIT License

----

# Item Alchemy (日本語)
## 概要
このMODはProjectEのようなEMCシステムと等価交換できる賢者の石などを追加します。

## ダウンロード
- https://www.curseforge.com/minecraft/mc-mods/item-alchemy-fabric
- https://modrinth.com/mod/item-alchemy

## ウィキ
- https://wikichree.com/pitan76/?Item+Alchemy

## 協力者 (Thanks!)
- [開発者＆デザイン] OffsetMonkey538 テクスチャ改良とバグ修正
- [開発者] mymai1208 チーム機能、チャージ機能の実装とバグ修正
- [開発者] anthonymendez TBD
- [デザイン] Murderman25 テクスチャ提供
- [翻訳者] 54sda 中国語への翻訳 (zh_cn.json)

## アドオン開発

### Maven
https://maven.pitan76.net/v/#net/pitan76/itemalchemy/ <br />
上記リンクから最新バージョンを確認してください。

- gradle.properties
```properties
# check these on https://maven.pitan76.net/v/#net/pitan76/
mcpitanlib_version=+1.18.2:2.4.9
itemalchemy_version=1.0.5
```

----

- build.gradle
```groovy
repositories {
    maven { url "https://maven.pitan76.net/" }
}

dependencies {
    modImplementation "net.pitan76:mcpitanlib:${project.mcpitanlib_version}"
    modImplementation "net.pitan76:itemalchemy:${project.itemalchemy_version}"
}

```

## ライセンス
- MIT License