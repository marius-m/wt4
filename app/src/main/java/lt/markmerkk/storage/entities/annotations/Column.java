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
    public FieldType value();
    public boolean canBeNull() default false;
    public boolean isPrimary() default false;
    public String defaultValue() default "";
}
