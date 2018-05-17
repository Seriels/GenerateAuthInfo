package com.pz.auth.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
//@Repeatable(Control.class)
public @interface MyRole {
    String info() default "";
    String name() default "";
    /**
     *   Authority  values
     * @return
     */
    MyAuth[] value() default {};
}
