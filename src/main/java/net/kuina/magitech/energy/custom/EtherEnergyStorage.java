package net.kuina.magitech.energy.custom;

import net.kuina.magitech.energy.IManaStorage;

// エネルギー（Ether＝マナ）の蓄積と管理を行うシンプルなクラス
// IManaStorage を実装し、タンク・アイテム・装置と共通の窓口でやり取りできる
public class EtherEnergyStorage implements IManaStorage {

    private long energy;   // 現在のエネルギー量
    private long capacity; // エネルギーの最大容量

    // コンストラクタ：エネルギーと容量を指定して初期化
    public EtherEnergyStorage(long energy, long capacity) {
        this.energy = energy;
        this.capacity = capacity; // 新しい容量を反映
    }

    public long getEnergy() {
        return energy;
    }

    public long getCapacity() {
        return capacity;
    }

    public void addEnergy(long amount) {
        energy = Math.min(energy + amount, capacity);  // 上限で制限
    }

    public void consume(long amount) {
        if (energy >= amount) {
            energy -= amount;
        }
    }

    public void fill() {
        energy = capacity;
    }

    public void resetEnergy() {
        energy = capacity;
    }
    // 新しく容量を変更するメソッドを追加
    public void setCapacity(long newCapacity) {
        this.capacity = newCapacity;
        // 新しい容量に合わせてエネルギー量も調整（必要に応じて）
        if (this.energy > this.capacity) {
            this.energy = this.capacity;  // 容量が増えてもエネルギーはそのままで、容量を超えている場合は調整
        }
}
    public void setEnergy(long newEnergy) {
        this.energy = Math.min(newEnergy, this.capacity);
    }

    /* ---------- IManaStorage（共通の受け渡し窓口） ---------- */

    @Override
    public long getManaStored() {
        return energy;
    }

    @Override
    public long getMaxMana() {
        return capacity;
    }

    @Override
    public long insertMana(long amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }
        long accepted = Math.min(amount, capacity - energy); // 空き容量まで
        if (accepted <= 0) {
            return 0;
        }
        if (!simulate) {
            energy += accepted;
        }
        return accepted;
    }

    @Override
    public long extractMana(long amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }
        long extracted = Math.min(amount, energy); // 残量まで
        if (extracted <= 0) {
            return 0;
        }
        if (!simulate) {
            energy -= extracted;
        }
        return extracted;
    }
}

