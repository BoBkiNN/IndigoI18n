package xyz.bobkinn.testPlugin;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.bobkinn.indigoi18n.paper.PaperAdventureI18n;

@RequiredArgsConstructor
public class TestCommand implements CommandExecutor {
    private final PaperAdventureI18n i18n;

    public void send(CommandSender sender) {
        i18n.parseA(sender, "myplugin.test", 123_123.12345);
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        for (int i = 0; i < 500_000; i++) {
            send(sender);
        }
        return true;
    }
}
