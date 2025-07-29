package me.lucko.luckperms.minestom.dependencies;

import java.nio.file.Path;
import me.lucko.luckperms.common.plugin.classpath.ClassPathAppender;
import org.jetbrains.annotations.NotNull;

public final class NoopClassPathAppender implements ClassPathAppender {

    public static final @NotNull NoopClassPathAppender INSTANCE = new NoopClassPathAppender();

    private NoopClassPathAppender() {

    }

    @Override
    public void addJarToClasspath(Path file) {
        // no-op
    }

}
