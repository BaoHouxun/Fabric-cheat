package net.fabricheat.utils.movement;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.fabricheat.FabricCheat;
import net.fabricheat.utils.UpdateListener;


public class PositionTracker implements UpdateListener {
    
    private UUID trackedPlayer;
    private ServerWorld playerWorld;
    private ArrayList<Vec3d> storedPlayerPositions = new ArrayList<>();

    public PositionTracker(ServerPlayerEntity player) 
    {
        UpdateListeners.add(this);
        trackedPlayer = player.getUuid();
        playerWorld = player.getServerWorld();
    }
    public void remove()
    {
        UpdateListeners.remove(this);
    }
    public ArrayList<Vec3d> getStoredPlayerPositions()
    {
        return storedPlayerPositions;
    }
    public void setList(ArrayList<Vec3d> posList)
    {
        storedPlayerPositions = posList;
    }
    @Override
    public void onUpdate(ServerPlayerEntity p) {
        if(trackedPlayer.equals(p.getUuid())){
            ServerPlayerEntity player = FabricCheat.getPlayerManager().getPlayer(trackedPlayer);
            if(player!=null && player.isAlive()){
                if(!player.getServerWorld().equals(playerWorld))
                {
                    playerWorld=player.getServerWorld();
                    storedPlayerPositions.clear();
                }
                if(storedPlayerPositions.size() > 60)
                {
                    storedPlayerPositions.remove(0);
                }
                storedPlayerPositions.add(player.getPos());
            }
            else
            {
                storedPlayerPositions.clear();
            }
        }
    }
}