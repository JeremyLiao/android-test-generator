package com.jeremyliao.atg.generator.type;

import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * Created by liaohailiang on 2019/3/13.
 */
public class JUnit4TypeGenerator implements ITypeGenerator {

    @Override
    public TypeSpec.Builder getTypeSpecBuilder(String testClassName) {
        TypeSpec.Builder builder = TypeSpec.classBuilder(testClassName)
                .addModifiers(Modifier.PUBLIC);
        return builder;
    }
}
