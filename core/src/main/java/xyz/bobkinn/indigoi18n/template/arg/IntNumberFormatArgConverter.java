package xyz.bobkinn.indigoi18n.template.arg;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class IntNumberFormatArgConverter<T, O> implements ArgumentConverter<T, O> {
    private final Map<Locale, NumberFormat> intCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unused")
    public void resetCache() {
        intCache.clear();
    }

    public NumberFormat nfIntInstance(Locale locale) {
        return intCache.computeIfAbsent(locale, l -> {
            var nf = NumberFormat.getIntegerInstance(l);
            nf.setGroupingUsed(true);
            return nf;
        });
    }
}
