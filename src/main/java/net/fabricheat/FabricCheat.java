package net.fabricheat;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sound.midi.Track;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents.Register;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.server.PlayerManager;

import net.fabricheat.anticheat.misc.PacketKicker;
import net.fabricheat.anticheat.movement.AntiSpeed;
import net.fabricheat.commands.PacketKickerCommands;
import net.fabricheat.utils.PacketListener.PacketListeners;
import net.fabricheat.utils.UpdateListener.UpdateListeners;
import net.fabricheat.utils.movement.PositionTracker;
import net.fabricheat.anticheat.movement.AntiSpeed;
import net.fabricheat.anticheat.movement.AntiFly;
import net.fabricheat.commands.AntiSpeedCommands;

public final class FabricCheat implements ModInitializer {
    private static CopyOnWriteArrayList<TrackedPlayer> trackedPlayers = new CopyOnWriteArrayList<>();
    private static PlayerManager playerManager;
    private static World world;
    //creates all refrences that are used across multipule files
    PacketListeners packetListeners = new PacketListeners();
    UpdateListeners updateListeners = new UpdateListeners();
    @Override
    public void onInitialize() {
        System.out.println("Fabricheat: Started Fabric-cheat");
        new PacketKickerCommands();
        new AntiSpeedCommands();
    }
    public static void setWorld(World w)
    {
        world = w;
    }
    public static World getWorld()
    {
        return world;
    }
    public static void setPlayerManager(PlayerManager manager)
    {
        playerManager=manager;
    }
    public static PlayerManager getPlayerManager()
    {
        return playerManager;
    }
    public static void onPlayerJoin(ServerPlayerEntity player)
    {
        //if they are not an op of any level
        if(!player.hasPermissionLevel(1)&&!player.hasPermissionLevel(2)&&!player.hasPermissionLevel(3)&&!player.hasPermissionLevel(4))
        {
            System.out.println("added player");
            trackedPlayers.add(new TrackedPlayer(player));
        }
    }
    public static void onPlayerLeave(ServerPlayerEntity player)
    {
        for (int i = 0; i<trackedPlayers.size(); i++) 
        {
            TrackedPlayer currentPlayer = trackedPlayers.get(i);
            if(currentPlayer.getPlayer().equals(player.getUuid()))
            {
                System.out.println("deleted player "+playerManager.getPlayer(currentPlayer.getPlayer()).getName().asString());
                currentPlayer.deletePlayerFromMemory();
                trackedPlayers.remove(i);
                return;
            }
        }
    }
    public static class TrackedPlayer
    {
        private UUID trackedPlayer;
        private PacketKicker packetKicker;
        private PositionTracker positionTracker;
        private AntiSpeed antiSpeed;
        private AntiFly antiFly;

        public TrackedPlayer(ServerPlayerEntity player)
        {
            trackedPlayer = player.getUuid();

            packetKicker = new PacketKicker(player);

            positionTracker = new PositionTracker(player);

            antiSpeed = new AntiSpeed(player, positionTracker);
            antiFly = new AntiFly(player, positionTracker);
        }
        public UUID getPlayer()
        {
            return trackedPlayer;
        }
        public void deletePlayerFromMemory()
        {
            packetKicker.remove();
            positionTracker.remove();
            antiSpeed.remove();
            antiFly.remove();
            packetKicker=null;
            positionTracker=null;
            antiSpeed=null;
            antiFly=null;
        }
    }
}

