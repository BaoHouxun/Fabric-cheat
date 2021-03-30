package net.fabricheat.anticheat.misc;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.spongepowered.asm.mixin.Shadow;

import net.fabricheat.commands.PacketKickerCommands;
import net.fabricheat.utils.PacketListener;
import net.fabricheat.utils.UpdateListener;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class PacketKicker implements PacketListener, UpdateListener {

	private UUID trackedPlayer;
	private int legalPacketCount = 50;
	private int millis =100;
	private Text kickReason = new TranslatableText("Sending too many packets!");
	private CopyOnWriteArrayList<Integer> packetAmount = new CopyOnWriteArrayList<>();

	public PacketKicker(ServerPlayerEntity player) {
		PacketListeners.add(this);
		UpdateListeners.add(this);
		trackedPlayer = player.getUuid();
	}

	public void remove()
    {
        UpdateListeners.remove(this);
        PacketListeners.remove(this);
    }
	
	@Override
	public void onUpdate(ServerPlayerEntity player) 
	{
		if(trackedPlayer.equals(player.getUuid()))
		{
			legalPacketCount = PacketKickerCommands.getMaxPackets();
			millis = PacketKickerCommands.getMillis();
			int currentTime = (int)System.currentTimeMillis();
			for (int i=0; i<packetAmount.size(); i++) {
				int packetTime = packetAmount.get(i);
				if(packetTime+millis < currentTime)
				{
					packetAmount.remove(i);
				}
			}
		}
	}
	@Override
	public void onPacketRecived(Packet<?> packet, ServerPlayerEntity player)
	{
		if(player.equals(trackedPlayer)){
			packetAmount.add((int)System.currentTimeMillis());
			if(packetAmount.size() > legalPacketCount)
			{
				System.out.println("Fabricheat: packet kicker detected a player sending too many packets!");
         		player.networkHandler.disconnect(kickReason);
			}
		}
	}
}
