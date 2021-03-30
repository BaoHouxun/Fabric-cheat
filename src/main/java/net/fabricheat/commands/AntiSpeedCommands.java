package net.fabricheat.commands;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;

import static net.minecraft.server.command.CommandManager.literal;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.text.TranslatableText;

public final class AntiSpeedCommands {
    private static int speedTicks = 4;
    private static double maxBaseSpeed = 0.65;
    public AntiSpeedCommands()
    {
        registerSetTicks();
        registerSetMaxSpeed();
    }
    public static int getSpeedTicks()
    {
        return speedTicks;
    }
    public static double getMaxBaseSpeed()
    {
        return maxBaseSpeed;
    }
    private static void registerSetTicks(){
        String integer = "integer";
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
        dispatcher.register(literal("setspeedticks").requires(source -> source.hasPermissionLevel(4)).then(CommandManager.argument(integer, IntegerArgumentType.integer(0)).executes(context -> {
                speedTicks = IntegerArgumentType.getInteger(context, integer);
                context.getSource().sendFeedback(new TranslatableText("§6[Fabricheat]§7: Set the base max speed to " + maxBaseSpeed + " meters per tick, with a §6" + speedTicks +"§7 tick check."), true);  
                return 1;
            })));
        });
    }
    private static void registerSetMaxSpeed(){
        String doubl = "double";
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
        dispatcher.register(literal("setmaxspeed").requires(source -> source.hasPermissionLevel(4)).then(CommandManager.argument(doubl, DoubleArgumentType.doubleArg(0)).executes(context -> {
                maxBaseSpeed = DoubleArgumentType.getDouble(context, doubl);
                context.getSource().sendFeedback(new TranslatableText("§6[Fabricheat]§7: Set the base max speed to §6" + maxBaseSpeed + "§7 meters per tick, with a " + speedTicks +" tick check."), true);  
                return 1;
            })));
        });
    }
}
