package me.lucko.luckperms.minestom;

import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.platform.PlayerAdapter;
import net.luckperms.api.util.Tristate;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * An example implementation of permission handling in a Player using LuckPerms.
 * This class is a simple example and is not intended for production use.
 * Every situation is different, and you should consider your own requirements when implementing permission handling.
 */
public final class ExamplePlayer extends Player {

    private static final @NotNull MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final @NotNull LuckPerms luckPerms;
    private final @NonNull PlayerAdapter<Player> playerAdapter;

    public ExamplePlayer(@NotNull LuckPerms luckPerms, @NotNull GameProfile profile, @NotNull PlayerConnection connection) {
        super(connection, profile);
        this.luckPerms = luckPerms;
        this.playerAdapter = this.luckPerms.getPlayerAdapter(Player.class);
    }

    private @NotNull User getLuckPermsUser() {
        return this.playerAdapter.getUser(this);
    }

    private @NotNull CachedMetaData getLuckPermsMetaData() {
        return this.getLuckPermsUser().getCachedData().getMetaData();
    }

    /**
     * Adds a permission to the player. You may choose not to implement
     * this method on a production server, and leave permission management
     * to the LuckPerms web interface or in-game commands.
     *
     * @param permission the permission to add
     * @return the result of the operation
     */
    public @NotNull CompletableFuture<DataMutateResult> addPermission(@NotNull String permission) {
        User user = this.getLuckPermsUser();
        DataMutateResult result = user.data().add(Node.builder(permission).build());
        return this.luckPerms.getUserManager().saveUser(user).thenApply(ignored -> result);
    }

    /**
     * Sets a permission for the player. This method uses a {@link Node} rather
     * than a permission name, this allows for permissions that rely on context.
     * You may choose not to implement this method on a production server, and
     * leave permission management to the LuckPerms web interface or in-game
     * commands.
     *
     * @param permission the permission to set
     * @param value the value of the permission
     * @return the result of the operation
     */
    public @NotNull CompletableFuture<DataMutateResult> setPermission(@NotNull Node permission, boolean value) {
        User user = this.getLuckPermsUser();
        DataMutateResult result = value
                ? user.data().add(permission)
                : user.data().remove(permission);
        return this.luckPerms.getUserManager().saveUser(user).thenApply(ignored -> result);
    }

    /**
     * Removes a permission from the player. You may choose not to implement
     * this method on a production server, and leave permission management
     * to the LuckPerms web interface or in-game commands.
     *
     * @param permissionName the name of the permission to remove
     */
    public @NotNull CompletableFuture<DataMutateResult> removePermission(@NotNull String permissionName) {
        User user = this.getLuckPermsUser();
        DataMutateResult result = user.data().remove(Node.builder(permissionName).build());
        return this.luckPerms.getUserManager().saveUser(user).thenApply(ignored -> result);
    }

    /**
     * Checks if the player has a permission.
     *
     * @param permissionName the name of the permission to check
     * @return true if the player has the permission
     */
    public boolean hasPermission(@NotNull String permissionName) {
        return this.getPermission(permissionName).asBoolean();
    }

    /**
     * Gets the value of a permission. This passes a {@link Tristate} value
     * straight from LuckPerms, which may be a better option than using
     * boolean values in some cases.
     *
     * @param permissionName the name of the permission to check
     * @return the value of the permission
     */
    public @NotNull Tristate getPermission(@NotNull String permissionName) {
        User user = this.getLuckPermsUser();
        return user.getCachedData().getPermissionData().checkPermission(permissionName);
    }

    /**
     * Gets the prefix of the player. This method uses the MiniMessage library
     * to parse the prefix, which is a more advanced option than using legacy
     * chat formatting.
     *
     * @return the prefix of the player
     */
    public @NotNull Component getPrefix() {
        String prefix = this.getLuckPermsMetaData().getPrefix();
        if (prefix == null) return Component.empty();
        return MINI_MESSAGE.deserialize(prefix);
    }

    /**
     * Gets the suffix of the player. This method uses the MiniMessage library
     * to parse the suffix, which is a more advanced option than using legacy
     * chat formatting.
     *
     * @return the suffix of the player
     */
    public @NotNull Component getSuffix() {
        String suffix = this.getLuckPermsMetaData().getSuffix();
        if (suffix == null) return Component.empty();
        return MINI_MESSAGE.deserialize(suffix);
    }

}
