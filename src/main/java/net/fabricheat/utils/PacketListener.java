package net.fabricheat.utils;

import java.util.EventListener;

import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.Packet;

public interface PacketListener extends EventListener 
{
    public void onPacketRecived(Packet<?> packetType, ServerPlayerEntity player);

    public final class PacketListeners
    {
        private static CopyOnWriteArrayList<PacketListener> allListeningClasses = new CopyOnWriteArrayList<>();

        public PacketListeners()
        {
            System.out.println("Created a Packet Listener");
        }
        public static void add(PacketListener p)
        {
            allListeningClasses.add(p);
        }
        public static void remove(PacketListener p)
        {
            allListeningClasses.remove(p);
        }
        public static CopyOnWriteArrayList<PacketListener> getListeners()
        {
            return allListeningClasses;
        }
    }
}

