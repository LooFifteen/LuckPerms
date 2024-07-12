package me.lucko.luckperms.minestom;

import java.util.List;
import me.lucko.luckperms.common.command.CommandManager;
import me.lucko.luckperms.common.command.utils.ArgumentTokenizer;
import me.lucko.luckperms.common.sender.Sender;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

public final class MinestomCommandExecutor extends CommandManager {
    private final LuckPermsCommand command;
    private final LPMinestomPlugin plugin;

    public MinestomCommandExecutor(LPMinestomPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.command = new LuckPermsCommand(this);
    }

    public void register() {
        MinecraftServer.getCommandManager().register(this.command);
    }

    public void unregister() {
        MinecraftServer.getCommandManager().unregister(this.command);
    }

    public class LuckPermsCommand extends Command {
        private final MinestomCommandExecutor commandExecutor;

        public LuckPermsCommand(MinestomCommandExecutor commandExecutor) {
            super("luckperms", "lp", "perm", "perms", "permission", "permissions");
            this.commandExecutor = commandExecutor;

            final var params = ArgumentType.StringArray("params");

            params.setSuggestionCallback((sender, context, suggestion) -> {
                Sender wrapped = this.commandExecutor.plugin.getSenderFactory().wrap(sender);
                String input = context.getInput();
                String[] split = input.split(" ", 2);
                String args = split.length > 1 ? split[1] : "";
                List<String> arguments = ArgumentTokenizer.TAB_COMPLETE.tokenizeInput(args);
                tabCompleteCommand(wrapped, arguments).stream().map(SuggestionEntry::new).forEach(suggestion::addEntry);
            });

            setDefaultExecutor((sender, context) -> process(sender, context.getCommandName(), new String[0]));

            addSyntax((sender, context) -> process(sender, context.getCommandName(), context.get(params)), params);
        }

        public void process(CommandSender sender, String command, String[] args) {
            List<String> arguments = ArgumentTokenizer.EXECUTE.tokenizeInput(args);

            this.commandExecutor.executeCommand(this.commandExecutor.plugin.getSenderFactory().wrap(sender), command, arguments);
        }
    }
}
