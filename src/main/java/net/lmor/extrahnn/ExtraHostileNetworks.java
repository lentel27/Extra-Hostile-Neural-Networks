package net.lmor.extrahnn;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(ExtraHostileNetworks.MOD_ID)
public class ExtraHostileNetworks
{
    public static final String MOD_ID = "modid";
    public ExtraHostileNetworks()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
