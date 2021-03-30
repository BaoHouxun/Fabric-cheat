package net.fabricheat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;

import net.fabricheat.FabricCheat;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "setPlayerManager", at = @At("HEAD"))
    public void setPlayerManager(PlayerManager playerManager, CallbackInfo info)
    {
        FabricCheat.setPlayerManager(playerManager);
    }
}
