package net.kuina.magitech.util;

import net.kuina.magitech.Magitech;
import net.minecraft.resources.ResourceLocation;

/**
 * mod 全体で使う小さな共通処理をまとめたユーティリティ。
 *
 * <p><b>何をするもの:</b> このmodの ID（ResourceLocation）を作る手間を省く。
 * テクスチャ・Capability・登録名などで {@code "Magitech:xxx"} を何度も書くのを避けられる。</p>
 *
 * <p><b>使い方:</b></p>
 * <pre>{@code
 * // "Magitech:mana_still" を作る
 * ResourceLocation id = ModUtil.rl("mana_still");
 *
 * // バニラ（minecraft:）の ID を作る
 * ResourceLocation stone = ModUtil.mc("stone");
 * }</pre>
 */
public final class ModUtil {

    private ModUtil() {
    }

    /** このmod（Magitech:）の名前空間で ID を作る。 */
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, path);
    }

    /** バニラ（minecraft:）の名前空間で ID を作る。 */
    public static ResourceLocation mc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }
}
