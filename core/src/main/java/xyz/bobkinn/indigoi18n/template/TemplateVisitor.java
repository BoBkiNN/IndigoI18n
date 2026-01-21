package xyz.bobkinn.indigoi18n.template;

import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;

/**
 * Utility class used to traverse {@link xyz.bobkinn.indigoi18n.data.ParsedEntry}
 */
public interface TemplateVisitor {
    void visitPlain(String text);

    void visitArgument(TemplateArgument arg);

    void visitInline(InlineTranslation inline);
}
