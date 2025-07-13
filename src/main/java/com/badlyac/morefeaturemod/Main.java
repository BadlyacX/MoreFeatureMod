package com.badlyac.morefeaturemod;

import com.badlyac.morefeaturemod.handler.PlayerLight.PlayerLight;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "morefeaturemod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Main() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        PlayerLight.register(bus);
    }
}