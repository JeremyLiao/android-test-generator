package com.jeremyliao.atg.generator.method;

import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * Created by liaohailiang on 2019/3/13.
 */
public interface IMethodGenerator {

    MethodSpec.Builder getTypeSpecBuilder(TypeElement typeElement, ExecutableElement executableElement);
}
