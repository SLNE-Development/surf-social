package dev.slne.surf.friends.velocity.command.subcommand.friend;

import com.velocitypowered.api.proxy.Player;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;

import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.core.util.PluginColor;
import dev.slne.surf.friends.velocity.VelocityInstance;

import java.util.Optional;
import java.util.UUID;

import net.kyori.adventure.text.Component;

public class FriendRemoveCommand extends CommandAPICommand {
    public FriendRemoveCommand(String name) {
        super(name);
        withArguments(new StringArgument("player"));

        executesPlayer((player, info)-> {
            Optional<Player> optionalPlayer = VelocityInstance.getInstance().getProxy().getPlayer((String) info.getUnchecked("player"));

            if(!optionalPlayer.isPresent()){
                player.sendMessage(FriendCore.prefix().append(
                    Component.text("Der Spieler wurde nicht gefunden.").color(PluginColor.RED)));
                return;
            }

            UUID target = optionalPlayer.get().getUniqueId();

            VelocityInstance.getInstance().getApi().removeFriend(player.getUniqueId(), target);
            VelocityInstance.getInstance().getApi().removeFriend(target, player.getUniqueId());
        });
    }
}
