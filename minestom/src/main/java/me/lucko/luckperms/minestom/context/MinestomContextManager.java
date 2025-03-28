package me.lucko.luckperms.minestom.context;

import java.util.UUID;

import me.lucko.luckperms.common.context.manager.DetachedContextManager;
import me.lucko.luckperms.common.context.manager.QueryOptionsSupplier;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import net.minestom.server.entity.Player;

public final class MinestomContextManager extends DetachedContextManager<Player, Player> {

    public MinestomContextManager(LuckPermsPlugin plugin) {
        super(plugin, Player.class, Player.class);
    }

    @Override
    public UUID getUniqueId(Player player) {
        return player.getUuid();
    }

    @Override
    public QueryOptionsSupplier getQueryOptionsSupplier(Player subject) {
        return null;
    }

}
