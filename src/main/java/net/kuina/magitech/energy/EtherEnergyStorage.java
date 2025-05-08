package net.kuina.magitech.energy;

// エネルギー（Ether）の蓄積と管理を行うシンプルなクラス
public class EtherEnergyStorage {

    private int energy;    // 現在のエネルギー量
    private int capacity;  // エネルギーの最大容量

    // コンストラクタ：最大容量を指定して初期化し、エネルギーを満タンに設定
    public EtherEnergyStorage(int capacity) {
        this.capacity = capacity;
        this.energy = capacity;  // 初期状態では最大容量までチャージ
    }

    // 現在のエネルギー量を取得
    public int getEnergy() {
        return energy;
    }

    // エネルギーの最大容量を取得
    public int getCapacity() {
        return capacity;
    }

    // 指定量のエネルギーを持っているか確認する
    public boolean hasEnergy(int amount) {
        return energy >= amount;
    }

    // 指定量のエネルギーを追加（最大容量を超えない）
    public void addEnergy(int amount) {
        energy = Math.min(energy + amount, capacity);  // 上限で制限
    }

    // 指定量のエネルギーを消費（足りている場合のみ）
    public void removeEnergy(int amount) {
        if (hasEnergy(amount)) {
            energy -= amount;
        }
    }

    // エネルギーを最大値まで回復する
    public void resetEnergy() {
        energy = capacity;
    }
}
