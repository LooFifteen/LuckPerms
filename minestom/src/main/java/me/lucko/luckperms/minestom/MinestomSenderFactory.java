package me.lucko.luckperms.minestom;

import java.util.UUID;
import java.util.function.BiFunction;

import me.lucko.luckperms.common.locale.TranslationManager;
import me.lucko.luckperms.common.sender.Sender;
import me.lucko.luckperms.common.sender.SenderFactory;
import net.kyori.adventure.text.Component;
import net.luckperms.api.util.Tristate;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.entity.Player;

public final class MinestomSenderFactory extends SenderFactory<LPMinestomPlugin, CommandSender> {

    private final LPMinestomPlugin plugin;
    private final BiFunction<CommandSender, String, Tristate> externalPermissionHandler;

    public MinestomSenderFactory(LPMinestomPlugin plugin, BiFunction<CommandSender, String, Tristate> externalPermissionHandler) {
        super(plugin);
        this.plugin = plugin;
        this.externalPermissionHandler = externalPermissionHandler;
    }

    @Override
    protected UUID getUniqueId(CommandSender sender) {
        return sender instanceof Player player ? player.getUuid() : Sender.CONSOLE_UUID;
    }

    @Override
    protected String getName(CommandSender sender) {
        return sender instanceof Player player ? player.getUsername() : Sender.CONSOLE_NAME;
    }

    @Override
    protected void sendMessage(CommandSender sender, Component message) {
        sender.sendMessage(TranslationManager.render(message));
    }

    @Override
    protected Tristate getPermissionValue(CommandSender sender, String node) {
        if(sender instanceof Player player) {
            Tristate result = this.plugin.getApiProvider()
                    .getPlayerAdapter(Player.class)
                    .getPermissionData(player)
                    .checkPermission(node);
            if(result != Tristate.UNDEFINED) {
                return result;
            }
        }
        return this.externalPermissionHandler.apply(sender, node);
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String node) {
        return this.getPermissionValue(sender, node).asBoolean();
    }

    @Override
    protected void performCommand(CommandSender sender, String command) {
        MinecraftServer.getCommandManager().execute(sender, command);
    }

    @Override
    protected boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleSender;
    }

}
