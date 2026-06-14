package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.base.ManaContainerBlockEntity;
import net.kuina.magitech.block.magitechblockentities;
import net.kuina.magitech.energy.IEtherEnergyReceiver;
import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.energy.custom.EtherEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * マナ貯蔵タンクのブロックエンティティ。
 *
 * <p>内部に {@link EtherEnergyStorage} を 1 つ持ち、それを 2 通りの窓口で公開する。</p>
 * <ul>
 *   <li>{@link IManaStorage}（{@link #getManaStorage()}）… Capability 経由で
 *       アイテムや隣接装置とマナをやり取りするための新しい共通窓口。</li>
 *   <li>{@link IEtherEnergyReceiver}（{@link #getEtherStorage()}）… 既存の
 *       クリエイティブ給電ブロックから充電してもらうための従来の窓口。</li>
 * </ul>
 */
public class ManaTankBlockEntity extends ManaContainerBlockEntity implements IEtherEnergyReceiver {

    /** タンクの最大容量。 */
    public static final long CAPACITY = 100_000L;

    public ManaTankBlockEntity(BlockPos pos, BlockState state) {
        super(magitechblockentities.MANA_TANK_BLOCK_ENTITY.get(), pos, state, CAPACITY);
    }

    /** Capability 経由で公開するマナ窓口。 */
    public IManaStorage getManaStorage() {
        return manaPort;
    }

    /** 既存のクリエイティブ給電ブロック用の窓口。 */
    @Override
    public EtherEnergyStorage getEtherStorage() {
        return mana;
    }

}
