package net.kuina.magitech.energy;

/**
 * EtherEnergyStorage を持つ TileEntity などが実装するインターフェース
 */
public interface IEtherEnergyReceiver {
    EtherEnergyStorage getEtherStorage();
}