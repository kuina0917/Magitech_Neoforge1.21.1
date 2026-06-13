package net.kuina.magitech.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.kuina.magitech.energy.PlayerEtherEnergy;

public class SetManaCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("magitech")
                .then(Commands.literal("setmana")
                        .then(Commands.argument("amount", LongArgumentType.longArg(0))
                                .executes(context -> {
                                    long amount = LongArgumentType.getLong(context, "amount");
                                    Player player = context.getSource().getPlayer();
                                    PlayerEtherEnergy.setCapacity(player, amount);
                                    return 1;
                                })
                        )
                )
        );
    }
}