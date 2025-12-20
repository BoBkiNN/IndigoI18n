package xyz.bobkinn.indigoi18n.codegen;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenStaticDefault {
    /**
     * Name of generated class
     */
    String name();

    /**
     * Static method name in this class that is used to create INSTANCE
     */
    String creator();
}
