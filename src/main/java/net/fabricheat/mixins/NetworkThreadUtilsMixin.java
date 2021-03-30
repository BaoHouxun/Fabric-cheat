package net.fabricheat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.thread.ThreadExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.Packet;
import net.minecraft.network.OffThreadException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import net.fabricheat.utils.PacketListener.PacketListeners;

@Mixin(net.minecraft.network.NetworkThreadUtils.class)
public class NetworkThreadUtilsMixin {
    @Inject(method = "forceMainThread", at = @At("HEAD"))
    private static <T extends PacketListener> void forceMainThread(Packet<T> packet, T listener, ServerWorld world, CallbackInfo info) throws OffThreadException 
    {
        if(listener instanceof ServerPlayNetworkHandler){
            ServerPlayerEntity player = ((ServerPlayNetworkHandler)listener).player;
            for (net.fabricheat.utils.PacketListener packetListener : PacketListeners.getListeners()) {
                packetListener.onPacketRecived(packet, player);
            }
        }
    }

}
