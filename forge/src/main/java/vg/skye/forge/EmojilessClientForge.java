package vg.skye.forge;

import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vg.skye.EmojilessClient;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "emojiless", bus = Mod.EventBusSubscriber.Bus.MOD)
public class EmojilessClientForge {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(EmojilessClient::init);
    }

    @SubscribeEvent
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) EmojilessClient::reloadCallback);
    }
}
