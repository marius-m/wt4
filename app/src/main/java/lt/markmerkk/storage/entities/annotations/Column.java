package lt.markmerkk.storage.entities.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mariusm on 27/08/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    FieldType value();
    boolean canBeNull() default false; // Deprecated, will be removed!!
    boolean isPrimary() default false;
    String defaultValue() default "";
}
