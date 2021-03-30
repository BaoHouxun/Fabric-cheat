package net.fabricheat.anticheat.movement;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.UUID;

import com.ibm.icu.text.Transliterator.Position;

import net.fabricheat.utils.UpdateListener;
import net.fabricheat.utils.movement.PositionTracker;
import net.fabricheat.FabricCheat;
import net.fabricheat.commands.AntiSpeedCommands;

public class AntiSpeed implements UpdateListener {
    private UUID trackedPlayer;
    private Vec3d lastPos;
    private int tickSpeeding = 0;
    private PositionTracker tracker;
    public AntiSpeed(ServerPlayerEntity player, PositionTracker inputTracker)
    {
        UpdateListeners.add(this);
        trackedPlayer = player.getUuid();
        lastPos = player.getPos();
        tracker = inputTracker;
    }
    public void remove()
    {
        UpdateListeners.remove(this);
    }
    @Override
    public void onUpdate(ServerPlayerEntity p) {
        if(trackedPlayer.equals(p.getUuid())){
            PlayerEntity player = FabricCheat.getPlayerManager().getPlayer(trackedPlayer);
            if(player != null){
                Vec3d currentPos = player.getPos();
                double maxSpeed = AntiSpeedCommands.getMaxBaseSpeed();
                double upSpeed = 0;
                if(player.isFallFlying())
                {
                    maxSpeed=maxSpeed*999;//28?
                    upSpeed=999;
                }
                else if(player.isInsideWaterOrBubbleColumn())
                {
                    maxSpeed=maxSpeed*12;
                    upSpeed=18;
                }
                else if(player.hasVehicle())
                {
                    if(player.getVehicle() instanceof MinecartEntity)
                    {
                        maxSpeed=2000*maxSpeed;
                        upSpeed=2000;
                    }
                    else{
                    maxSpeed=2*maxSpeed;
                    upSpeed=0.3;
                    }
                }
                else if(player.hasStatusEffect(StatusEffects.SPEED))
                {
                    maxSpeed=(player.getStatusEffect(StatusEffects.SPEED).getAmplifier()*1.2)*maxSpeed;
                }
                if(currentPos.y-lastPos.y > (0.3+upSpeed) ||  Math.abs(new Vec3d(currentPos.x, 0, currentPos.z).distanceTo(new Vec3d(lastPos.x, 0,lastPos.z))) > maxSpeed)//0.35715 normal speed but they need a little bit of room
                {
                    tickSpeeding++;
                    ArrayList<Vec3d> curList = tracker.getStoredPlayerPositions();
                    if(curList.size()>1){
                    curList.remove(curList.size()-1);
                    curList.add(curList.get(0));
                    }
                    tracker.setList(curList);
                    if(tickSpeeding>AntiSpeedCommands.getSpeedTicks() && player.isAlive())
                    {
                        Vec3d tpPos = tracker.getStoredPlayerPositions().get(0);
                        p.teleport(p.getServerWorld(), tpPos.x, tpPos.y, tpPos.z, player.yaw, player.pitch);
                        currentPos = p.getPos();
                        tickSpeeding = AntiSpeedCommands.getSpeedTicks()-5;
                    }
                }
                else
                {
                    tickSpeeding=0;
                }
                lastPos = currentPos;
            }
        }
    }
}
