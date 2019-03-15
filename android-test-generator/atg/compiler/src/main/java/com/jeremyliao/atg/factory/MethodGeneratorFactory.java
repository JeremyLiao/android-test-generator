package com.jeremyliao.atg.factory;

import com.jeremyliao.atg.generator.method.IMethodGenerator;
import com.jeremyliao.atg.generator.method.JUnit4MethodGenerator;
import com.jeremyliao.atg.generator.method.JUnit4StaticMethodGenerator;
import com.jeremyliao.atg.type.TestType;

/**
 * Created by liaohailiang on 2019/3/13.
 */
public class MethodGeneratorFactory {

    private static final JUnit4MethodGenerator JUNIT4_METHOD_GENERATOR = new JUnit4MethodGenerator();
    private static final JUnit4StaticMethodGenerator JUNIT4_STATIC_METHOD_GENERATOR = new JUnit4StaticMethodGenerator();

    public static IMethodGenerator create(TestType type, boolean isStatic) {
        switch (type) {
            case JUnit4:
                if (isStatic) {
                    return JUNIT4_STATIC_METHOD_GENERATOR;
                } else {
                    return JUNIT4_METHOD_GENERATOR;
                }
            case AndroidJUnit4:
            default:
                return JUNIT4_METHOD_GENERATOR;
        }
    }
}
