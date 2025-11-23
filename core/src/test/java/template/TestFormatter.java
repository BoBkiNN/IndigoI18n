package template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.template.FormatSpec;
import xyz.bobkinn.indigoi18n.template.TemplateFormatter;

public class TestFormatter {
    /**
     * {@code _=+7}
     */
    @Test
    public void testAlignNumber() {
        var alg = new FormatSpec.Alignment(FormatSpec.AlignType.SIGN, '_');
        var v = TemplateFormatter.alignNumber(alg, 7, '+', "23");
        Assertions.assertEquals("+____23", v);
    }
}
