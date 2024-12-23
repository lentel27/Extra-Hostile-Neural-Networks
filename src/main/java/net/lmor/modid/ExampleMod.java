package net.lmor.modid;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(ExampleMod.MOD_ID)
public class ExampleMod
{
    public static final String MOD_ID = "modid";
    public ExampleMod()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
