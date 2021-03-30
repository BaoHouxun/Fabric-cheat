package net.fabricheat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.ClientConnection;
import net.fabricheat.utils.UpdateListener;
import net.fabricheat.utils.UpdateListener.UpdateListeners;


@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    private ServerPlayerEntity trackedPlayer;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void serverPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo info)
    {
        trackedPlayer = player; 
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo info)
    {
        for (UpdateListener updateListener : UpdateListeners.getListeners()) {
            updateListener.onUpdate(trackedPlayer);
        }
    }
}
