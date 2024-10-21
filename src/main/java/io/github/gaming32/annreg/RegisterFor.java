package io.github.gaming32.annreg;

import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterFor {
    @Pattern("[a-z0-9_]{2,}")
    String modid();

    @Pattern("[a-z0-9_.-]*")
    String namespace() default "";

    @Pattern("([a-z0-9_.-]*:)?[a-z0-9/._-]*")
    String registry();
}
