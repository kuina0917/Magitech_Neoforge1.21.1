package net.kuina.magitech.block.base;

import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.energy.custom.EtherEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * マナストレージを持つ BlockEntity の基底クラス。
 *
 * <p>{@link EtherEnergyStorage} を内蔵し、変更時に自動で {@link #setChanged()} を
 * 呼ぶ {@link #manaPort} を提供する。save/load も共通化済み。
 *
 * <p>利用例：
 * <pre>{@code
 * public class MyMachineBE extends ManaContainerBlockEntity {
 *     public MyMachineBE(BlockPos pos, BlockState state) {
 *         super(MY_TYPE.get(), pos, state, 10_000L);
 *     }
 * }}</pre>
 */
public abstract class ManaContainerBlockEntity extends BlockEntity {

    protected final EtherEnergyStorage mana;
    /** 外部公開用のマナ窓口。デフォルトは双方向（insert/extract 両対応）。
     *  受け取り専用にしたいサブクラスはコンストラクタで差し替えること。 */
    protected IManaStorage manaPort;

    public ManaContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity) {
        super(type, pos, state);
        this.mana = new EtherEnergyStorage(0, capacity);
        this.manaPort = createDefaultPort();
    }

    /** 双方向（insert/extract 両対応）のデフォルトマナ窓口を生成する。 */
    private IManaStorage createDefaultPort() {
        return new IManaStorage() {
            @Override
            public long getManaStored() { return mana.getManaStored(); }
            @Override
            public long getMaxMana() { return mana.getMaxMana(); }
            @Override
            public long insertMana(long amount, boolean simulate) {
                long result = mana.insertMana(amount, simulate);
                if (!simulate && result > 0) setChanged();
                return result;
            }
            @Override
            public long extractMana(long amount, boolean simulate) {
                long result = mana.extractMana(amount, simulate);
                if (!simulate && result > 0) setChanged();
                return result;
            }
            @Override public boolean canExtract() { return true; }
            @Override public boolean canReceive() { return true; }
        };
    }

    /** 受け取り専用（extract 不可）のマナ窓口を生成する。
     *  サブクラスのコンストラクタで {@code this.manaPort = createReceiveOnlyPort();} と使う。 */
    protected IManaStorage createReceiveOnlyPort() {
        return new IManaStorage() {
            @Override
            public long getManaStored() { return mana.getManaStored(); }
            @Override
            public long getMaxMana() { return mana.getMaxMana(); }
            @Override
            public long insertMana(long amount, boolean simulate) {
                long result = mana.insertMana(amount, simulate);
                if (!simulate && result > 0) setChanged();
                return result;
            }
            @Override
            public long extractMana(long amount, boolean simulate) { return 0; }
            @Override public boolean canExtract() { return false; }
            @Override public boolean canReceive() { return true; }
        };
    }

    public IManaStorage getManaPort() { return manaPort; }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong("Mana", mana.getManaStored());
        tag.putLong("Capacity", mana.getMaxMana());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        mana.setEnergy(tag.getLong("Mana"));
        if (tag.contains("Capacity")) {
            mana.setCapacity(tag.getLong("Capacity"));
        }
    }
}
