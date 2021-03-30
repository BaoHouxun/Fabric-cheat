package net.fabricheat.utils;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

import net.minecraft.server.network.ServerPlayerEntity;

public interface UpdateListener {

    public void onUpdate(ServerPlayerEntity player);

    public final class UpdateListeners
    {
        private static CopyOnWriteArrayList<UpdateListener> allListeningClasses = new CopyOnWriteArrayList<>();

        public UpdateListeners()
        {
            System.out.println("Created a Update Listener");
        }
        public static void add(UpdateListener p)
        {
            allListeningClasses.add(p);
        }
        public static void remove(UpdateListener p)
        {
            allListeningClasses.remove(p);
        }
        public static CopyOnWriteArrayList<UpdateListener> getListeners()
        {
            return allListeningClasses;
        }
    }
}
