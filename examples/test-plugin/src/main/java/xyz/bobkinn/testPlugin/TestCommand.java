package xyz.bobkinn.testPlugin;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.paper.PaperAdventureI18n;

import java.util.Random;

@RequiredArgsConstructor
public class TestCommand implements CommandExecutor {
    private final PaperAdventureI18n i18n;

    public void perform(CommandSender sender) {
        i18n.parseA(sender, "myplugin.test", 123_123.12345);
    }

    public void test1(CommandSender sender) {
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
    }

    public static final Random RANDOM = new Random(12312312);

    private static final String letters = "abcdefghziours";

    private static char randomLetter() {
        var i = RANDOM.nextInt(letters.length());
        return letters.charAt(i);
    }

    private static String randomKey() {
        var l = RANDOM.nextInt(5, 15);
        var sb = new StringBuilder(l);
        for (int i = 0; i < l; i++) {
            var isDot = RANDOM.nextFloat() > .9;
            if (isDot) sb.append(".");
            else {
                sb.append(randomLetter());
            }
        }
        return sb.toString();
    }

    public void test2(CommandSender sender) {
        for (int i = 0; i < 5_000; i++) {
            i18n.parseA(sender, randomKey(), sender);
        }
        var t = System.currentTimeMillis();
        for (int i = 0; i < 50_000; i++) {
            i18n.parseA(sender, randomKey(), sender);
        }
        var d = System.currentTimeMillis() - t;
        sender.sendMessage("Mass d: "+d);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        if (args.length < 1) return false;
        var arg = args[0];
        switch (arg) {
            case "a": test1(sender); break;
            case "b": test2(sender); break;
        }
        return true;
    }
}
