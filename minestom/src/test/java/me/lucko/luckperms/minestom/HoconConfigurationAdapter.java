package me.lucko.luckperms.minestom;

import java.nio.file.Path;
import me.lucko.luckperms.common.config.generic.adapter.ConfigurationAdapter;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;

public final class HoconConfigurationAdapter extends ModernConfigurateConfigAdapter implements ConfigurationAdapter {

    public HoconConfigurationAdapter(LuckPermsPlugin plugin) {
        super(plugin, ((LPMinestomPlugin) plugin).resolveConfig("luckperms.conf"));
    }

    @Override
    protected ConfigurationLoader<? extends @NotNull ConfigurationNode> createLoader(Path path) {
        return HoconConfigurationLoader.builder().path(path).build();
    }

}
