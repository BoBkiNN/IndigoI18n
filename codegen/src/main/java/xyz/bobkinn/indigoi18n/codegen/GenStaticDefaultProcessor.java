package xyz.bobkinn.indigoi18n.codegen;

import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("xyz.bobkinn.indigoi18n.codegen.GenStaticDefault")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class GenStaticDefaultProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(GenStaticDefault.class)) {
            if (element.getKind() != ElementKind.CLASS) continue;
            processClass((TypeElement) element);
        }
        return true;
    }

    // =========================
    // Core processing
    // =========================

    private void processClass(TypeElement classElement) {
        GenStaticDefault ann = classElement.getAnnotation(GenStaticDefault.class);

        String packageName = getPackageName(classElement);
        String generatedClassName = ann.name();

        ClassName originalClass = ClassName.get(classElement);
        FieldSpec instanceField = createInstanceField(originalClass, ann.creator());

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addField(instanceField);

        classBuilder.addJavadoc("Generated static defaults for {@link $T}", classElement);

        addInterfaceMethods(classBuilder, classElement);

        writeClass(packageName, classBuilder.build());
    }

    // =========================
    // Helpers
    // =========================

    private String getPackageName(TypeElement type) {
        return processingEnv.getElementUtils()
                .getPackageOf(type)
                .getQualifiedName()
                .toString();
    }

    private FieldSpec createInstanceField(ClassName originalClass, String creator) {
        return FieldSpec.builder(originalClass, "INSTANCE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$T.$L()", originalClass, creator)
                .build();
    }

    private void addInterfaceMethods(TypeSpec.Builder classBuilder, TypeElement classElement) {
        for (TypeMirror iface : classElement.getInterfaces()) {
            TypeElement ifaceElement =
                    (TypeElement) processingEnv.getTypeUtils().asElement(iface);

            for (Element member : ifaceElement.getEnclosedElements()) {
                if (member.getKind() != ElementKind.METHOD) continue;
                if (member.getModifiers().contains(Modifier.PRIVATE)) continue;
                classBuilder.addMethod(createDelegatingMethod(ifaceElement, (ExecutableElement) member));
            }

            // add super interfaces methods
            addInterfaceMethods(classBuilder, ifaceElement);
        }
    }

    private MethodSpec createDelegatingMethod(TypeElement owner, ExecutableElement method) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(
                        method.getSimpleName().toString()
                )
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.get(method.getReturnType()));

        // parameters
        for (VariableElement param : method.getParameters()) {
            builder.addParameter(
                    TypeName.get(param.asType()),
                    param.getSimpleName().toString()
            );
        }

        // call
        CodeBlock call = buildCall(method);

        if (method.getReturnType().getKind() == TypeKind.VOID) {
            builder.addStatement("$L", call);
        } else {
            builder.addStatement("return $L", call);
        }
        // varargs
        if (method.isVarArgs()) builder.varargs(true);
        // type params
        for (var tp : method.getTypeParameters()) {
            builder.addTypeVariable(TypeVariableName.get(tp));
        }
        builder.addJavadoc(buildMethodJd(owner, method));
        return builder.build();
    }

    private CodeBlock buildMethodJd(TypeElement owner, ExecutableElement method) {
        var b = CodeBlock.builder()
                .add("@see $T#$L(", owner, method.getSimpleName());
        boolean first = true;
        for (VariableElement param : method.getParameters()) {
            if (!first) b.add(", ");
            b.add("$T", param.asType());
            first = false;
        }
        b.add(")");
        return b.build();
    }

    private CodeBlock buildCall(ExecutableElement method) {
        CodeBlock.Builder call = CodeBlock.builder()
                .add("INSTANCE.$L(", method.getSimpleName());

        boolean first = true;
        for (VariableElement param : method.getParameters()) {
            if (!first) call.add(", ");
            call.add("$L", param.getSimpleName());
            first = false;
        }

        call.add(")");
        return call.build();
    }

    private void writeClass(String packageName, TypeSpec type) {
        try {
            JavaFile.builder(packageName, type)
                    .build()
                    .writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, e.toString());
        }
    }
}
