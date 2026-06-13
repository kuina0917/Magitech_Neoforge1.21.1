package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.magitechblockentities;
import net.kuina.magitech.energy.IEtherEnergyReceiver;
import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.energy.custom.EtherEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
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
public class ManaTankBlockEntity extends BlockEntity implements IEtherEnergyReceiver {

    /** タンクの最大容量。 */
    public static final long CAPACITY = 100_000L;

    private final EtherEnergyStorage storage = new EtherEnergyStorage(0, CAPACITY);

    /**
     * 外部公開用のラッパー。
     * 実際に増減があったときだけ {@link #setChanged()} を呼び、保存漏れを防ぐ。
     */
    private final IManaStorage manaPort = new IManaStorage() {
        @Override
        public long getManaStored() {
            return storage.getManaStored();
        }

        @Override
        public long getMaxMana() {
            return storage.getMaxMana();
        }

        @Override
        public long insertMana(long amount, boolean simulate) {
            long result = storage.insertMana(amount, simulate);
            if (!simulate && result > 0) {
                setChanged();
            }
            return result;
        }

        @Override
        public long extractMana(long amount, boolean simulate) {
            long result = storage.extractMana(amount, simulate);
            if (!simulate && result > 0) {
                setChanged();
            }
            return result;
        }
    };

    public ManaTankBlockEntity(BlockPos pos, BlockState state) {
        super(magitechblockentities.MANA_TANK_BLOCK_ENTITY.get(), pos, state);
    }

    /** Capability 経由で公開するマナ窓口。 */
    public IManaStorage getManaStorage() {
        return manaPort;
    }

    /** 既存のクリエイティブ給電ブロック用の窓口。 */
    @Override
    public EtherEnergyStorage getEtherStorage() {
        return storage;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong("Mana", storage.getManaStored());
        tag.putLong("Capacity", storage.getMaxMana());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Capacity")) {
            storage.setCapacity(tag.getLong("Capacity"));
        }
        storage.setEnergy(tag.getLong("Mana"));
    }
}
