package net.kuina.magitech.energy.custom;

// エネルギー（Ether）の蓄積と管理を行うシンプルなクラス
public class EtherEnergyStorage{

    private long energy;    // 現在のエネルギー量
    private long capacity;  // エネルギーの最大容量

    // コンストラクタ：最大容量を指定して初期化
    public EtherEnergyStorage(long energy, long capacity) {
        this.energy = energy; // 初期エネルギー（ログイン時のエネルギー）
        this.capacity = capacity; // 最大容量（変更なし）
    }

    // 現在のエネルギー量を取得
    public long getEnergy() {
        return energy;
    }

    // エネルギーの最大容量を取得
    public long getCapacity() {
        return capacity;
    }

    // 指定量のエネルギーを持っているか確認する
    public boolean hasEnergy(long amount) {
        return energy >= amount;
    }

    // 指定量のエネルギーを追加（最大容量を超えない）
    public void addEnergy(long amount) {
        energy = Math.min(energy + amount, capacity);  // 上限で制限
    }

    // 指定量のエネルギーを消費（足りている場合のみ）
    public void consume(long amount) {
        if (hasEnergy(amount)) {
            energy -= amount;
        }
    }

    // エネルギーを最大値まで回復する
    public void fill() {
        energy = capacity;
    }

    // エネルギーを最大容量までリセットする
    public void resetEnergy() {
        energy = capacity;
    }
}