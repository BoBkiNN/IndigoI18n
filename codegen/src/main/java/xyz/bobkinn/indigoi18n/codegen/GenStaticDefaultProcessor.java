package xyz.bobkinn.indigoi18n.codegen;

import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("xyz.bobkinn.indigoi18n.codegen.GenStaticDefault")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class GenStaticDefaultProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element elem : roundEnv.getElementsAnnotatedWith(GenStaticDefault.class)) {
            if (elem.getKind() != ElementKind.CLASS) continue;

            TypeElement classElement = (TypeElement) elem;
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processor triggered for: " + classElement.getSimpleName());
            GenStaticDefault ann = classElement.getAnnotation(GenStaticDefault.class);

            String packageName = processingEnv.getElementUtils().getPackageOf(classElement).getQualifiedName().toString();
            String generatedClassName = ann.name();
            String creator = ann.creator();
            String originalClassName = classElement.getSimpleName().toString();

            ClassName originalClass = ClassName.get(packageName, originalClassName);

            // Create INSTANCE field
            FieldSpec instanceField = FieldSpec.builder(originalClass, "INSTANCE")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$T.$L()", originalClass, creator)
                    .build();

            TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addField(instanceField);

            // Iterate over interfaces
            List<? extends TypeMirror> interfaces = classElement.getInterfaces();

            for (TypeMirror iFace : interfaces) {
                TypeElement iFaceElement = (TypeElement) processingEnv.getTypeUtils().asElement(iFace);

                for (Element iFaceMember : iFaceElement.getEnclosedElements()) {
                    if (iFaceMember.getKind() != ElementKind.METHOD) continue;

                    ExecutableElement method = (ExecutableElement) iFaceMember;
                    String methodName = method.getSimpleName().toString();

                    // Return type
                    TypeName returnType = TypeName.get(method.getReturnType());

                    // Parameters
                    MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .returns(returnType);

                    for (VariableElement param : method.getParameters()) {
                        TypeName paramType = TypeName.get(param.asType());
                        methodBuilder.addParameter(paramType, param.getSimpleName().toString());
                    }

                    // Build method body
                    StringBuilder call = new StringBuilder("INSTANCE." + methodName + "(");
                    boolean first = true;
                    for (VariableElement param : method.getParameters()) {
                        if (!first) call.append(", ");
                        call.append(param.getSimpleName().toString());
                        first = false;
                    }
                    call.append(")");

                    if (returnType.equals(TypeName.VOID)) {
                        methodBuilder.addStatement("$L", call.toString());
                    } else {
                        methodBuilder.addStatement("return $L", call.toString());
                    }

                    classBuilder.addMethod(methodBuilder.build());
                }
            }

            // Write class
            TypeSpec generatedClass = classBuilder.build();
            JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
            }
        }

        return true;
    }
}
