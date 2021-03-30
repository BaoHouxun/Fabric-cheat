package net.fabricheat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.ClientConnection;

import net.fabricheat.FabricCheat;

@Mixin(net.minecraft.server.PlayerManager.class)
public class PlayerManagerMixin {
    
    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info)
    {
        FabricCheat.onPlayerJoin(player);
    }
    @Inject(method = "remove", at = @At("HEAD"))
    public void remove(ServerPlayerEntity player, CallbackInfo info)
    {
        FabricCheat.onPlayerLeave(player);
    }
}
