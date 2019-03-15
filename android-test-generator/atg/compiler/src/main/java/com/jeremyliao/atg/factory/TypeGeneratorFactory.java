package com.jeremyliao.atg.factory;

import com.jeremyliao.atg.generator.type.AndroidJUnit4TypeGenerator;
import com.jeremyliao.atg.generator.type.ITypeGenerator;
import com.jeremyliao.atg.generator.type.JUnit4TypeGenerator;
import com.jeremyliao.atg.type.TestType;

/**
 * Created by liaohailiang on 2019/3/13.
 */
public class TypeGeneratorFactory {

    private static final JUnit4TypeGenerator JUNIT4_TYPE_GENERATOR = new JUnit4TypeGenerator();
    private static final AndroidJUnit4TypeGenerator ANDROID_JUNIT4_TYPE_GENERATOR = new AndroidJUnit4TypeGenerator();

    public static ITypeGenerator create(TestType type) {
        switch (type) {
            case JUnit4:
                return JUNIT4_TYPE_GENERATOR;
            case AndroidJUnit4:
                return ANDROID_JUNIT4_TYPE_GENERATOR;
            default:
                return JUNIT4_TYPE_GENERATOR;
        }
    }
}
