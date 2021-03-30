//This is my first commant just to say that this is for anti-lightning hacks
package net.fabricheat.mixins;

import java.util.concurrent.Executor;

import com.ibm.icu.util.ULocale.Category;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Spawner;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.fabricheat.FabricCheat;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    private SoundEvent currentSound;
    private SoundCategory currentCategory;
    @Inject(method = "playSound", at = @At(value = "INVOKE"))
    private void getValues(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, CallbackInfo info)
    {
        currentSound = sound;
        currentCategory = category;
    }
    @ModifyArgs(method = "playSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAround(Lnet/minecraft/entity/player/PlayerEntity;DDDDLnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/network/Packet;)V"))
    public void stopLightningSound(Args args)
    {
        if(currentSound.equals(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER)){
            List<ServerPlayerEntity> players = new ArrayList<>();
            if(args.get(0) == null){
                players = FabricCheat.getPlayerManager().getPlayerList();
            }
            else
            {
                players.add(args.get(0));
            }
            for(ServerPlayerEntity player : players)
            {
                args.set(1, player.getX());
                args.set(2, player.getY());
                args.set(3, player.getZ());
                args.set(6, new PlaySoundS2CPacket(currentSound, currentCategory, player.getX(), player.getY(), player.getZ(), 0.5f, 1f));
            }
        }
    }
}
