package com.pz.auth.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
/**
 * @author pz
 * @version 2.3
 * @E-mail 2919274153@qq.com
 * @date 2018-5-17 15:01:54
 */
public @interface MyRole {
    String info() default "";
    String name() default "";
    /**
     *   Authority  values
     * @return
     */
    MyAuth[] value() default {};
}
