package xyz.bobkinn.indigoi18n.spigot;

import lombok.Data;
import org.bukkit.command.CommandSender;
import xyz.bobkinn.indigoi18n.context.ContextEntry;

/**
 * Context entry storing viewer of translation.
 * Added when {@link CommandSenderLanguageMixin} is used
 */
@Data
public class ViewerContext implements ContextEntry {
    /**
     * Sender that is meant to view resulting output
     */
    private final CommandSender viewer;
}
