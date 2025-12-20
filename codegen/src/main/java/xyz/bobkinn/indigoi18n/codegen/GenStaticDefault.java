package xyz.bobkinn.indigoi18n.codegen;

import java.lang.annotation.*;

/**
 * Generates class named {@link #name()} in same package.<br>
 * It contains public static final field INSTANCE with type of annotated class.
 * This field is initialised using {@link #creator()} method.
 * Annotation processor takes all methods from interfaces annotated class implements
 * and generates static methods executing same method on INSTANCE.
 */
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
