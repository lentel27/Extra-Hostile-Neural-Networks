package net.lmor.extrahnn.api;

import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

public interface IRegTile {
    IEnergyStorage getEnergy();

    IItemHandler getInventory();
}
