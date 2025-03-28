package me.lucko.luckperms.minestom.context;

import java.util.Objects;
import java.util.UUID;
import me.lucko.luckperms.common.context.manager.DetachedContextManager;
import me.lucko.luckperms.common.context.manager.QueryOptionsSupplier;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class MinestomContextManager extends DetachedContextManager<Player, Player> {

    private static final Tag<QueryOptionsSupplier> QUERY_OPTIONS_SUPPLIER_TAG = Tag.Transient("luckperms_query_options_supplier");

    public MinestomContextManager(LuckPermsPlugin plugin) {
        super(plugin, Player.class, Player.class);
    }

    @Override
    public UUID getUniqueId(Player player) {
        return player.getUuid();
    }

    @Override
    public @Nullable QueryOptionsSupplier getQueryOptionsSupplier(Player subject) {
        Objects.requireNonNull(subject);
        QueryOptionsSupplier supplier = subject.getTag(QUERY_OPTIONS_SUPPLIER_TAG);
        if (supplier == null) {
            supplier = this.createQueryOptionsSupplier(subject);
            subject.setTag(QUERY_OPTIONS_SUPPLIER_TAG, supplier);
        }
        return supplier;
    }

}
