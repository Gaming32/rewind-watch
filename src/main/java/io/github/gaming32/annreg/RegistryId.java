package io.github.gaming32.annreg;

import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryId {
    @Pattern("[a-z0-9/._-]*")
    String value();
}
