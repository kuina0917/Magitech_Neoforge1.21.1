package net.kuina.magitech.energy;

import net.kuina.magitech.energy.custom.EtherEnergyStorage;

/**
 * EtherEnergyStorage を持つ TileEntity などが実装するインターフェース
 */
public interface IEtherEnergyReceiver {
    EtherEnergyStorage getEtherStorage();
}