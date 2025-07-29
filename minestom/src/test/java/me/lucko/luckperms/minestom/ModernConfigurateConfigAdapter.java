package me.lucko.luckperms.minestom;

import com.google.common.base.Splitter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.lucko.luckperms.common.config.generic.adapter.ConfigurationAdapter;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

public abstract class ModernConfigurateConfigAdapter implements ConfigurationAdapter {

    private final LuckPermsPlugin plugin;
    private final Path path;
    private ConfigurationNode root;

    public ModernConfigurateConfigAdapter(LuckPermsPlugin plugin, Path path) {
        this.plugin = plugin;
        this.path = path;
        this.reload();
    }

    protected abstract ConfigurationLoader<? extends @NotNull ConfigurationNode> createLoader(Path path);

    @Override
    public void reload() {
        ConfigurationLoader<? extends @NotNull ConfigurationNode> loader = this.createLoader(this.path);
        try {
            this.root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ConfigurationNode resolvePath(String path) {
        if (this.root == null) {
            throw new RuntimeException("Config is not loaded.");
        }

        return this.root.node(Splitter.on('.').splitToList(path).toArray());
    }

    @Override
    public String getString(String path, String def) {
        String string = this.resolvePath(path).getString();
        if (string == null) return def;
        return string;
    }

    @Override
    public int getInteger(String path, int def) {
        return this.resolvePath(path).getInt(def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return this.resolvePath(path).getBoolean(def);
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        ConfigurationNode node = this.resolvePath(path);
        if (node.virtual() || !node.isList()) {
            return def;
        }

        try {
            return node.getList(String.class, def);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getStringMap(String path, Map<String, String> def) {
        ConfigurationNode node = this.resolvePath(path);
        if (node.virtual()) {
            return def;
        }

        return node.childrenMap().entrySet().stream().collect(Collectors.toMap(
                k -> k.getKey().toString(),
                v -> v.getValue().toString()
        ));
    }

    @Override
    public LuckPermsPlugin getPlugin() {
        return this.plugin;
    }

}
