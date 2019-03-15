package com.jeremyliao.atg.generator.type;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import org.junit.runner.RunWith;

import javax.lang.model.element.Modifier;

/**
 * Created by liaohailiang on 2019/3/13.
 */
public class AndroidJUnit4TypeGenerator implements ITypeGenerator {

    @Override
    public TypeSpec.Builder getTypeSpecBuilder(String testClassName) {
        ClassName className = ClassName.get("android.support.test.runner",
                "AndroidJUnit4");
        TypeSpec.Builder builder = TypeSpec.classBuilder(testClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(RunWith.class)
                        .addMember("value", "$T.class", className)
                        .build());
        return builder;
    }
}
