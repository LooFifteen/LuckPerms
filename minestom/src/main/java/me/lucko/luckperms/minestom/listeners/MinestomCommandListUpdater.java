package me.lucko.luckperms.minestom.listeners;

import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.lucko.luckperms.common.cache.BufferedRequest;
import me.lucko.luckperms.common.event.LuckPermsEventListener;
import me.lucko.luckperms.common.util.CaffeineFactory;
import me.lucko.luckperms.minestom.LPMinestomBootstrap;
import me.lucko.luckperms.minestom.LPMinestomPlugin;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.context.ContextUpdateEvent;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MinestomCommandListUpdater implements LuckPermsEventListener {

    private final @NotNull LoadingCache<UUID, SendBuffer> sendingBuffers = CaffeineFactory.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build(SendBuffer::new);

    private final @NotNull LPMinestomPlugin plugin;

    public MinestomCommandListUpdater(@NotNull LPMinestomPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void bind(@NotNull EventBus bus) {
        bus.subscribe(UserDataRecalculateEvent.class, this::onUserDataRecalculate);
        bus.subscribe(ContextUpdateEvent.class, this::onContextUpdate);
    }

    private void onUserDataRecalculate(@NotNull UserDataRecalculateEvent event) {
        this.requestUpdate(event.getUser().getUniqueId());
    }

    private void onContextUpdate(@NotNull ContextUpdateEvent event) {
        event.getSubject(Player.class).map(Player::getUuid).ifPresent(this::requestUpdate);
    }

    private void requestUpdate(@NotNull UUID uniqueId) {
        if (!this.plugin.getBootstrap().isPlayerOnline(uniqueId)) return;

        SendBuffer sendBuffer = this.sendingBuffers.get(uniqueId);
        if (sendBuffer == null) throw new IllegalStateException("send buffer is null");
        sendBuffer.request();
    }

    private void sendUpdate(@NotNull UUID uniqueId) {
        LPMinestomBootstrap bootstrap = this.plugin.getBootstrap();
        bootstrap.getScheduler().sync().execute(() -> bootstrap.getPlayer(uniqueId).ifPresent(Player::refreshCommands));
    }

    private final class SendBuffer extends BufferedRequest<Void> {

        private final @NotNull UUID uniqueId;

        SendBuffer(@NotNull UUID uniqueId) {
            super(500, TimeUnit.MILLISECONDS, MinestomCommandListUpdater.this.plugin.getBootstrap().getScheduler());
            this.uniqueId = uniqueId;
        }

        @Override
        protected @Nullable Void perform() {
            MinestomCommandListUpdater.this.sendUpdate(this.uniqueId);
            return null;
        }

    }

}
