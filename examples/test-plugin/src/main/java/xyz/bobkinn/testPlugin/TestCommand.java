package xyz.bobkinn.testPlugin;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.paper.PaperAdventureI18n;

@RequiredArgsConstructor
public class TestCommand implements CommandExecutor {
    private final PaperAdventureI18n i18n;

    public void perform(CommandSender sender) {
        i18n.parseA(sender, "myplugin.test", 123_123.12345);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        for (int i = 0; i < 5_000; i++) {
            perform(sender);
        }
        var t = System.currentTimeMillis();
        for (int i = 0; i < 50_000; i++) {
            perform(sender);
        }
        var d = System.currentTimeMillis() - t;
        sender.sendMessage("Many Delta ms: "+d);
        var t2 = System.nanoTime();
        perform(sender);
        var d2 = System.nanoTime() - t2;
        sender.sendMessage("Single call ns: "+d2);
        return true;
    }
}
