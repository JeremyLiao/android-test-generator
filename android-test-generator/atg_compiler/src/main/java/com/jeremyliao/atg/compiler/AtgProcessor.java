package com.jeremyliao.atg.compiler;

import com.google.auto.service.AutoService;
import com.jeremyliao.atg.annotation.AtgTest;
import com.jeremyliao.atg.annotation.TestCase;
import com.jeremyliao.atg.type.TestType;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import static com.sun.tools.doclets.formats.html.markup.HtmlStyle.interfaceName;

/**
 * Created by liaohailiang on 2018/8/30.
 */
@AutoService(Processor.class)
public class AtgProcessor extends AbstractProcessor {

    private static final String TAG = "[AtgProcessor]";

    protected Filer filer;
    protected Types types;
    protected Elements elements;
    protected Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) {

        } else {
            processAnnotations(roundEnvironment);
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(AtgTest.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void processAnnotations(RoundEnvironment roundEnvironment) {
        process(roundEnvironment);
    }

    private void process(RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(AtgTest.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                AtgTest atgTest = typeElement.getAnnotation(AtgTest.class);
                TestType type = atgTest.value();
                String typeName = typeElement.getQualifiedName().toString();
                String originalClassName = typeElement.getSimpleName().toString();
                System.out.println(TAG + "typeName: " + typeName);
                generateTestClass(type, typeElement);
            }
        }
    }

    private String generateTestClass(TestType type, TypeElement typeElement) {
        String testDirPath = isAndroidTest(type) ? getAndroidTestPath() : getTestPath();
        System.out.println(TAG + "testDirPath: " + testDirPath);
        File testDir = new File(testDirPath);
        if (!testDir.exists()) {
            testDir.mkdirs();
        }

        String testClassName = getTestClassName(type, typeElement);
        PackageElement packageElement = elements.getPackageOf(typeElement);
        String packageName = packageElement.getQualifiedName().toString();

        TypeSpec.Builder builder = getTestClassTypeSpec(type, testClassName);
        addMethods(builder, typeElement);

        try {
            JavaFile.builder(packageName, builder.build())
                    .build()
                    .writeTo(testDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String className = packageName + "." + testClassName;
        System.out.println(TAG + "generateClass: " + className);
        return className;
    }

    private void addMethods(TypeSpec.Builder builder, TypeElement typeElement) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        for (Element itemElement : enclosedElements) {
            TestCase testCase = itemElement.getAnnotation(TestCase.class);
            ClassName className = ClassName.get(typeElement);
            if (testCase != null &&
                    itemElement.getKind() == ElementKind.METHOD &&
                    itemElement.getModifiers().contains(Modifier.PUBLIC)) {
                ExecutableElement executableElement = (ExecutableElement) itemElement;
                String name = itemElement.getSimpleName().toString();
                MethodSpec.Builder build = MethodSpec.methodBuilder(name)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(void.class)
                        .addAnnotation(Test.class);
                build.addStatement("$T target = new $T()", className, className);
                if (testCase.checkOutout()) {
                    build.addStatement("$T result = target.$L($L)", executableElement.getReturnType(),
                            itemElement.getSimpleName().toString(), testCase.inputs());
                    if (compareTypeMirrorClass(executableElement.getReturnType(), String.class)) {
                        //return String
                        build.addStatement("$T.assertEquals(result,$S)", Assert.class, testCase.expect());
                    } else {
                        //return other
                        build.addStatement("$T.assertEquals(result,$L)", Assert.class, testCase.expect());
                    }
                } else {
                    //TODO check variable
                    build.addStatement("target.$L($L)", itemElement.getSimpleName().toString(), testCase.inputs());
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

                builder.addMethod(build.build());
            }
        }
    }

    private boolean compareTypeMirrorClass(TypeMirror typeMirror, Class type) {
        if (typeMirror == null) {
            return false;
        }
        if (type == null) {
            return false;
        }
        return type.getCanonicalName().equals(typeMirror.toString());
    }

    private VariableElement getField(TypeElement typeElement, String name) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        for (Element element : enclosedElements) {
            if (element.getKind() == ElementKind.FIELD &&
                    element.getSimpleName().toString().equals(name)) {
                return (VariableElement) element;
            }
        }
        return null;
    }

    private ExecutableElement getMethod(TypeElement typeElement, String name) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        for (Element element : enclosedElements) {
            if (element.getKind() == ElementKind.METHOD &&
                    element.getSimpleName().toString().equals(name)) {
                return (ExecutableElement) element;
            }
        }
        return null;
    }

    private String upperCaseFirst(String name) {
        if (name == null || name.length() == 0) {
            return "";
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private TypeSpec.Builder getTestClassTypeSpec(TestType type, String testClassName) {
        TypeSpec.Builder builder = null;
        switch (type) {
            case JUnit4:
                builder = getJUnit4TestClassTypeSpec(testClassName);
                break;
            case AndroidJUnit4:
                builder = getAndroidJUnit4TestClassTypeSpec(testClassName);
                break;
            default:
        }
        return builder;
    }

    private TypeSpec.Builder getJUnit4TestClassTypeSpec(String testClassName) {
        TypeSpec.Builder builder = TypeSpec.classBuilder(testClassName)
                .addModifiers(Modifier.PUBLIC);
        return builder;
    }

    private TypeSpec.Builder getAndroidJUnit4TestClassTypeSpec(String testClassName) {
        ClassName className = ClassName.get("android.support.test.runner",
                "AndroidJUnit4");
        TypeSpec.Builder builder = TypeSpec.classBuilder(testClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(RunWith.class)
                        .addMember("value", "$T.class", className)
                        .build());

        return builder;
    }

    private String getTestClassName(TestType type, TypeElement typeElement) {
        String originalClassName = typeElement.getSimpleName().toString();
        String testClassName = originalClassName + "Test";
        //TODO 文件名已存在的处理
        return testClassName;
    }

    private boolean isAndroidTest(TestType type) {
        boolean isAndroidTest;
        switch (type) {
            case JUnit4:
                isAndroidTest = false;
                break;
            case AndroidJUnit4:
                isAndroidTest = true;
                break;
            default:
                isAndroidTest = false;
        }
        return isAndroidTest;
    }

    private String getModulePath() {
        FileObject resource = null;
        try {
            resource = filer.getResource(StandardLocation.CLASS_OUTPUT,
                    "", "tmp");
            Path path = Paths.get(resource.toUri());
            int end = path.getNameCount() - 1;
            for (int i = end; i >= 0; i--) {
                if ("build".equals(path.getName(i).toString())) {
                    end = i;
                    break;
                }
            }
            Path modulePath = path.subpath(0, end);
            System.out.println(TAG + "whole path: " + path.toString());
            System.out.println(TAG + "module path: " + modulePath.toString());
//            resource.delete();
            return File.separator + modulePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getTestPath() {
        return getModulePath() + "/src/test/java/";
    }

    private String getAndroidTestPath() {
        return getModulePath() + "/src/androidTest/java/";
    }
}
