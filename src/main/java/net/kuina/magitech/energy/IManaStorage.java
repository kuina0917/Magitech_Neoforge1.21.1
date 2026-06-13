package net.kuina.magitech.energy;

/**
 * マナを「入れる／取り出す」ための共通の窓口。
 *
 * <p>貯蔵タンク・携帯アイテム・各種装置など、マナを保持するものはこれを実装する。
 * 転送は {@link ManaTransfer} を介して行い、利用側は相手がブロックかアイテムかを
 * 意識せずに同じ方法でマナをやり取りできる。</p>
 *
 * <p>{@code simulate} を {@code true} にすると実際には増減させず、
 * 「もし実行したら何単位入る／出るか」だけを返す（残量計算などに使う）。</p>
 */
public interface IManaStorage {

    /** 現在の貯蔵量。 */
    long getManaStored();

    /** 最大容量。 */
    long getMaxMana();

    /**
     * マナを注入する。
     *
     * @param amount   入れたい量
     * @param simulate true なら実際には入れず、入れられる量だけ返す
     * @return 実際に（または simulate 時は入れられたであろう）注入された量
     */
    long insertMana(long amount, boolean simulate);

    /**
     * マナを取り出す。
     *
     * @param amount   取り出したい量
     * @param simulate true なら実際には減らさず、取り出せる量だけ返す
     * @return 実際に（または simulate 時は取り出せたであろう）取り出された量
     */
    long extractMana(long amount, boolean simulate);

    /** 外部からの注入を受け付けるか。 */
    default boolean canReceive() {
        return true;
    }

    /** 外部への取り出しを許可するか。 */
    default boolean canExtract() {
        return true;
    }
}
