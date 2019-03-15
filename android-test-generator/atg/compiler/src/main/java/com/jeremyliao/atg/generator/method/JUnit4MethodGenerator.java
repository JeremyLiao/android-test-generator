package com.jeremyliao.atg.generator.method;

import com.jeremyliao.atg.annotation.TestCase;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by liaohailiang on 2019/3/13.
 */
public class JUnit4MethodGenerator extends AbstractMethodGenerator {

    @Override
    public MethodSpec.Builder getTypeSpecBuilder(TypeElement typeElement, ExecutableElement executableElement) {
        String name = executableElement.getSimpleName().toString();
        ClassName className = ClassName.get(typeElement);
        TestCase testCase = executableElement.getAnnotation(TestCase.class);
        if (testCase != null && executableElement.getModifiers().contains(Modifier.PUBLIC)) {
            MethodSpec.Builder build = MethodSpec.methodBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addException(Exception.class)
                    .addAnnotation(Test.class);
            String formatInput = formatInput(executableElement, testCase.inputs());
            build.addStatement("$T target = new $T()", className, className);
            if (testCase.checkReturn()) {
                build.addStatement("$T result = target.$L($L)", executableElement.getReturnType(),
                        executableElement.getSimpleName().toString(), formatInput);
                if (compareTypeMirrorClass(executableElement.getReturnType(), String.class)) {
                    //return String
                    build.addStatement("$T.assertEquals(result,$S)", Assert.class, testCase.expect());
                } else {
                    //return other
                    build.addStatement("$T.assertEquals(result,$L)", Assert.class, testCase.expect());
                }
            } else {
                build.addStatement("target.$L($L)", executableElement.getSimpleName().toString(), formatInput);
                String variable = testCase.checkVariable();
                if (variable.length() > 0) {
                    //find the field
                    VariableElement field = getField(typeElement, variable);
                    if (field != null) {
                        if (field.getModifiers().contains(Modifier.PUBLIC)) {
                            if (compareTypeMirrorClass(field.asType(), String.class)) {
                                //return String
                                build.addStatement("$T.assertEquals(target.$L,$S)", Assert.class, variable, testCase.expect());
                            } else {
                                //return other
                                build.addStatement("$T.assertEquals(target.$L,$L)", Assert.class, variable, testCase.expect());
                            }
                        } else {
                            //find getter
                            String getterName = "get" + upperCaseFirst(variable);
                            ExecutableElement method = getMethod(typeElement, getterName);
                            if (method != null && method.getModifiers().contains(Modifier.PUBLIC)) {
                                if (compareTypeMirrorClass(field.asType(), String.class)) {
                                    //return String
                                    build.addStatement("$T.assertEquals(target.$L(),$S)", Assert.class, getterName, testCase.expect());
                                } else {
                                    //return other
                                    build.addStatement("$T.assertEquals(target.$L(),$L)", Assert.class, getterName, testCase.expect());
                                }
                            } else {
                                //TODO 用反射的方式去调用
                            }
                        }
                    }
                }
            }
            return build;
        } else {
            return null;
        }
    }
}
