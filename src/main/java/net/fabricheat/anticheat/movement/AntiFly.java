package net.fabricheat.anticheat.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.fabricheat.utils.UpdateListener;
import net.fabricheat.FabricCheat;
import net.fabricheat.utils.PacketListener;
import net.fabricheat.utils.movement.PositionTracker;

public class AntiFly implements UpdateListener, PacketListener
{

    private UUID trackedPlayer;
    private PositionTracker tracker;
    private double calcPlayerSpeed=0.08;
    private double lastGroundPositionY;
    private double calcBlockPos;
    private int ticksInAir = 0;
    private int timesTeleported = 0;
   // private Boolean hasSentPacket = false;
    private Boolean wasAllInAir = false;

    public AntiFly(ServerPlayerEntity player, PositionTracker inputTracker)
    {
        UpdateListeners.add(this);
        PacketListeners.add(this);
        trackedPlayer = player.getUuid();
        tracker = inputTracker;
        lastGroundPositionY = player.getY();
    }
    public void remove()
    {
        UpdateListeners.remove(this);
        PacketListeners.remove(this);
    }
    @Override
    public void onUpdate(ServerPlayerEntity p) 
    {
        if(trackedPlayer.equals(p.getUuid())){
            PlayerEntity player = FabricCheat.getPlayerManager().getPlayer(trackedPlayer);
            if(player != null){
                Vec3d currentPos = player.getPos();
                double maxJumpHeight = 2;
                double maxTicks = 12;
                if(player.hasVehicle())
                {
                    Entity vehical = player.getVehicle();
                    if(vehical instanceof HorseEntity)
                    {
                        maxJumpHeight=5;
                        maxTicks=24;
                    }
                    else if(vehical instanceof MinecartEntity)
                    {
                        maxJumpHeight=999;
                        maxTicks=999999;
                    }
                }
         
                if(wasAllInAir)
                {
                    ticksInAir++;
                    if(currentPos.y - lastGroundPositionY<=0)
                    {
                        if(player.hasStatusEffect(StatusEffects.SLOW_FALLING))
                        {
                            
                        }
                        else if(calcPlayerSpeed < 3.92){
                            calcPlayerSpeed=(calcPlayerSpeed+0.08)*0.98;
                        }
                        else
                        {
                            calcPlayerSpeed=3.92;
                        }
                        calcBlockPos-=calcPlayerSpeed;
                        //System.out.println("real: "+ (currentPos.y - lastGroundPositionY) +", calculated: "+ (calcBlockPos - lastGroundPositionY));
                    }
                    if(currentPos.y - lastGroundPositionY > maxJumpHeight || ( ticksInAir > maxTicks && currentPos.y - lastGroundPositionY > calcBlockPos - lastGroundPositionY))
                    {
                        if(timesTeleported<60 && player.isAlive()){
                        timesTeleported++;
                        Vec3d tpPos = tracker.getStoredPlayerPositions().get(0);
                        p.teleport(p.getServerWorld(), tpPos.x, tpPos.y, tpPos.z, player.yaw, player.pitch);
                        ticksInAir-=10;
                        }
                        else if(timesTeleported<80)
                        {
                            timesTeleported++;
                        }
                        else
                        {
                            timesTeleported++;
                        }
                    }
                }
                else
                {
                    ticksInAir=0;
                    timesTeleported=0;
                    calcPlayerSpeed=0.08;
                }
                wasAllInAir=false;
            }
        }
    }
    @Override
    public void onPacketRecived(Packet<?> packetType, ServerPlayerEntity p) {
        if(trackedPlayer.equals(p.getUuid()) && packetType instanceof PlayerMoveC2SPacket)
        {
            PlayerEntity player = FabricCheat.getPlayerManager().getPlayer(trackedPlayer);
            if(player != null){
                Vec3d currentPos = player.getPos();
                double amountBellow=0.05;
                if(player.hasVehicle())
                {
                    if(player.getVehicle() instanceof HorseEntity)
                    {
                        amountBellow=0.9;
                    }
                }
                //Box box = p.getBoundingBox();
                /*
                //Box box = p.getBoundingBox();
                //System.out.println((p.getServerWorld().isAir(new BlockPos(new Vec3d((int)box.minX, currentPos.y-amountBellow, (int)box.minZ)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d((int)box.minX, currentPos.y-amountBellow, (int)box.maxZ)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d((int)box.maxX, currentPos.y-amountBellow, (int)box.minZ)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d((int)box.maxX, currentPos.y-amountBellow, (int)box.maxZ)))));
                */
                if(!player.isFallFlying() && (p.getServerWorld().isAir(new BlockPos(new Vec3d(currentPos.x, currentPos.y-amountBellow, currentPos.z)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(currentPos.x-0.9, currentPos.y-amountBellow, currentPos.z)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(currentPos.x+0.9, currentPos.y-amountBellow, currentPos.z)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(currentPos.x-0.9, currentPos.y-amountBellow, currentPos.z)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(currentPos.x, currentPos.y-amountBellow, currentPos.z+0.9)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(currentPos.x, currentPos.y-amountBellow, currentPos.z-0.9)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(currentPos.x+0.9, currentPos.y-amountBellow, currentPos.z+0.9)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(currentPos.x+0.9, currentPos.y-amountBellow, currentPos.z-0.9)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(currentPos.x-0.9, currentPos.y-amountBellow, currentPos.z+0.9)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(currentPos.x-0.9, currentPos.y-amountBellow, currentPos.z-0.9)))))//(p.getServerWorld().isAir(new BlockPos(new Vec3d(box.minX, currentPos.y-amountBellow, box.minZ)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(box.maxX, currentPos.y-amountBellow, box.minZ)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(box.minX, currentPos.y-amountBellow, box.maxZ)))&&p.getServerWorld().isAir(new BlockPos(new Vec3d(box.maxX, currentPos.y-amountBellow, box.maxZ)))))
                {
                    wasAllInAir=true;
                }
                else
                {
                    calcBlockPos = currentPos.y;
                    lastGroundPositionY = currentPos.y;
                }
                if(player.isFallFlying())
                {
                    wasAllInAir=false;
                }
            }
        }
    }
}
