package net.fabricheat.commands;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

import static net.minecraft.server.command.CommandManager.literal;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.text.TranslatableText;


public final class PacketKickerCommands {
    private static int maxPackets = 50;
    private static int millis = 100;

    public PacketKickerCommands()
    {
        registerSetPackets();
        registerSetMillis();
    }
    public static int getMaxPackets()
    {
        return maxPackets;
    }
    public static int getMillis()
    {
        return millis;
    }
    private static void registerSetPackets(){
        String integer = "integer";
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
        dispatcher.register(literal("setmaxpackets").requires(source -> source.hasPermissionLevel(4)).then(CommandManager.argument(integer, IntegerArgumentType.integer(0)).executes(context -> {
                maxPackets = IntegerArgumentType.getInteger(context, integer);
                context.getSource().sendFeedback(new TranslatableText("§6[Fabricheat]§7: Set max packets to §6" + maxPackets + "§7 per " + millis +"ms."), true); 
                return 1;
            })));
        });
    }
    private static void registerSetMillis(){
        String integer = "integer";
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
        dispatcher.register(literal("setpacketmillis").requires(source -> source.hasPermissionLevel(4)).then(CommandManager.argument(integer, IntegerArgumentType.integer(0)).executes(context -> {
                millis = IntegerArgumentType.getInteger(context, integer);
                context.getSource().sendFeedback(new TranslatableText("§6[Fabricheat]§7: Set max packets to " + maxPackets + " per §6" + millis +"ms§7."), true); 
                return 1;
            })));
        });
    }
}