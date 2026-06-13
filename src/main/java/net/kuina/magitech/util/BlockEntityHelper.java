package net.kuina.magitech.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * ブロックエンティティでよく使う処理をまとめたユーティリティ。
 *
 * <p><b>何をするもの:</b> 「中身が変わったので保存する」「変わったことをクライアントに伝えて
 * 見た目を更新する」という定番処理を 1 行で行えるようにする。</p>
 *
 * <p><b>使い方:</b> ブロックエンティティの内容（在庫・マナ量など）を変更したあとに呼ぶ。</p>
 * <pre>{@code
 * // 保存だけでよい場合（見た目に影響しないデータ）
 * BlockEntityHelper.markDirty(this);
 *
 * // 保存＋クライアントへ同期（ブロックの表示が変わる場合）
 * BlockEntityHelper.markDirtyAndSync(this);
 * }</pre>
 */
public final class BlockEntityHelper {

    private BlockEntityHelper() {
    }

    /**
     * 内容が変わったことを記録して保存対象にする（{@link BlockEntity#setChanged()} と同じ）。
     * 見た目に影響しないデータ変更ならこれで十分。
     */
    public static void markDirty(BlockEntity blockEntity) {
        if (blockEntity != null && blockEntity.getLevel() != null) {
            blockEntity.setChanged();
        }
    }

    /**
     * 保存に加えて、ブロックの状態更新をクライアントへ通知する。
     * パーティクルやモデル差し替えなど見た目を更新したいときに使う。
     */
    public static void markDirtyAndSync(BlockEntity blockEntity) {
        Level level = blockEntity != null ? blockEntity.getLevel() : null;
        if (level == null) {
            return;
        }
        blockEntity.setChanged();
        BlockPos pos = blockEntity.getBlockPos();
        BlockState state = blockEntity.getBlockState();
        // 同じ state でも update を送ることで、クライアントにブロックエンティティの再取得を促す
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }
}
