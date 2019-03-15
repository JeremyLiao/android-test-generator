package com.jeremyliao.atg.compiler;

import com.google.auto.service.AutoService;
import com.jeremyliao.atg.annotation.AtgTest;
import com.jeremyliao.atg.annotation.TestCase;
import com.jeremyliao.atg.factory.MethodGeneratorFactory;
import com.jeremyliao.atg.factory.TypeGeneratorFactory;
import com.jeremyliao.atg.generator.method.IMethodGenerator;
import com.jeremyliao.atg.generator.type.ITypeGenerator;
import com.jeremyliao.atg.type.TestType;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

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
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;


/**
 * Created by liaohailiang on 2018/8/30.
 */
@AutoService(Processor.class)
public class AtgProcessor extends AbstractProcessor {

    private static final String TAG = "[AtgProcessor]";
    private static final String CREATE_TEST_CASE = "createTestCase";

    protected Filer filer;
    protected Types types;
    protected Elements elements;
    protected Messager messager;

    private boolean shouldCreateTestCase = true;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Map<String, String> options = processingEnvironment.getOptions();
        System.out.println(TAG + "options: " + options);
        if (options.containsKey(CREATE_TEST_CASE)) {
            shouldCreateTestCase = Boolean.getBoolean(options.get(CREATE_TEST_CASE));
        }
        System.out.println(TAG + "shouldCreateTestCase: " + shouldCreateTestCase);
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
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(AtgTest.class.getCanonicalName());
        return annotations;
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
        addMethods(builder, type, typeElement);

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

    private void addMethods(TypeSpec.Builder builder, TestType type, TypeElement typeElement) {
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        for (Element itemElement : enclosedElements) {
            if (itemElement.getKind() == ElementKind.METHOD) {
                TestCase testCase = itemElement.getAnnotation(TestCase.class);
                if (testCase == null) {
                    continue;
                }
                ExecutableElement executableElement = (ExecutableElement) itemElement;
                IMethodGenerator methodGenerator = MethodGeneratorFactory.create(type, testCase.isStatic());
                MethodSpec.Builder methodBuilder = methodGenerator.getTypeSpecBuilder(typeElement, executableElement);
                if (methodBuilder != null) {
                    builder.addMethod(methodBuilder.build());
                }
            }
        }
    }

    private TypeSpec.Builder getTestClassTypeSpec(TestType type, String testClassName) {
        ITypeGenerator typeGenerator = TypeGeneratorFactory.create(type);
        return typeGenerator.getTypeSpecBuilder(testClassName);
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
            resource.delete();
            String modulePathStr = File.separator + modulePath.toString();
            System.out.println(TAG + "module path: " + modulePathStr);
            return modulePathStr;
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
