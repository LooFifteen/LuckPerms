package me.lucko.luckperms.minestom.dependencies;

import java.util.Set;
import me.lucko.luckperms.common.dependencies.Dependency;
import me.lucko.luckperms.common.dependencies.DependencyManager;
import me.lucko.luckperms.common.storage.StorageType;
import org.jetbrains.annotations.NotNull;

public final class NoopDependencyManager implements DependencyManager {

    public static final @NotNull NoopDependencyManager INSTANCE = new NoopDependencyManager();

    private NoopDependencyManager() {

    }

    @Override
    public void loadDependencies(Set<Dependency> dependencies) {
        // no-op
    }

    @Override
    public void loadStorageDependencies(Set<StorageType> storageTypes, boolean redis, boolean rabbitmq, boolean nats) {
        // no-op
    }

    @Override
    public ClassLoader obtainClassLoaderWith(Set<Dependency> dependencies) {
        return NoopDependencyManager.class.getClassLoader();
    }

    @Override
    public void close() {
        // no-op
    }

}
