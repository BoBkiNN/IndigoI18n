package xyz.bobkinn.indigoi18n.format.adventure;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.format.I18nFormat;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class ComponentI18nFormat extends I18nFormat<Component> {

    private final ComponentTemplateFormatter templateFormatter;

    public abstract Component deserialize(String text);

    public ComponentI18nFormat(TemplateCache cache, ComponentTemplateFormatter templateFormatter) {
        super(cache);
        this.templateFormatter = templateFormatter;
    }

    @Override
    public Component produce(String text) {
        return deserialize(text);
    }

    private Component processText(Context ctx, TranslationInfo info, TextComponent comp, List<Object> args){
        var full = comp.content();
        var parsed = cache.getOrCreate(full, info);
        if (parsed == null) {
            // TODO think about customizable error formatting instead falling everywhere to just key
            return Component.text(info.key());
        }
        // format parts into component
        var res = templateFormatter.format(ctx, parsed, args);
        List<Component> extra = new ArrayList<>(comp.children().size());
        for (var c : comp.children()) {
            if (c instanceof TextComponent text){
                extra.add(processText(ctx, info, text, args));
            } else extra.add(c);
        }
        // apply root style and children
        return res.mergeStyle(comp).append(extra);
    }

    @Override
    public Component replaceArguments(Context ctx, Component input, List<Object> args) {
        var info = ctx.collectInfo();
        if (info == null) {
            throw new IllegalStateException("No translation info were collected in context "+ctx);
        }
        if (input instanceof TextComponent text) {
            return processText(ctx, info, text, args);
        } else {
            List<Component> extra = new ArrayList<>();
            for (var c : input.children()) {
                if (c instanceof TextComponent text){
                    extra.add(processText(ctx, info, text, args));
                } else extra.add(c);
            }
            return input.children(extra);
        }
    }
}
