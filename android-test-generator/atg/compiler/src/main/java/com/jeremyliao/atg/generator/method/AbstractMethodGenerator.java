package com.jeremyliao.atg.generator.method;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by liaohailiang on 2019/3/13.
 */
public abstract class AbstractMethodGenerator implements IMethodGenerator {

    protected boolean compareTypeMirrorClass(TypeMirror typeMirror, Class type) {
        if (typeMirror == null) {
            return false;
        }
        if (type == null) {
            return false;
        }
        return type.getCanonicalName().equals(typeMirror.toString());
    }

    protected VariableElement getField(TypeElement typeElement, String name) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        for (Element element : enclosedElements) {
            if (element.getKind() == ElementKind.FIELD &&
                    element.getSimpleName().toString().equals(name)) {
                return (VariableElement) element;
            }
        }
        return null;
    }

    protected ExecutableElement getMethod(TypeElement typeElement, String name) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        for (Element element : enclosedElements) {
            if (element.getKind() == ElementKind.METHOD &&
                    element.getSimpleName().toString().equals(name)) {
                return (ExecutableElement) element;
            }
        }
        return null;
    }

    protected String upperCaseFirst(String name) {
        if (name == null || name.length() == 0) {
            return "";
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    protected String formatInput(ExecutableElement executableElement, String input) {
        if (input == null || input.length() == 0) {
            return input;
        }
        List<? extends VariableElement> parameters = executableElement.getParameters();
        if (parameters == null || parameters.size() == 0) {
            return input;
        }
        String[] params = input.split(",");
        if (params.length != parameters.size()) {
            return input;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            TypeMirror typeMirror = parameters.get(i).asType();
            if (compareTypeMirrorClass(typeMirror, String.class)) {
                sb.append("\"").append(params[i]).append("\"");
            } else {
                sb.append(params[i]);
            }
            if (i < params.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
