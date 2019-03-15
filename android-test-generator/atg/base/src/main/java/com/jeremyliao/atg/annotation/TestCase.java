package com.jeremyliao.atg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by liaohailiang on 2018/8/24.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface TestCase {

    String inputs() default "";

    boolean checkReturn() default false;

    boolean isStatic() default false;

    String checkVariable() default "";

    String expect();
}
