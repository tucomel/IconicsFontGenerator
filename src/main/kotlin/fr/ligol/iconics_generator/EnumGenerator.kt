package fr.ligol.iconics_generator

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.gradle.internal.impldep.org.eclipse.jgit.annotations.NonNull
import javax.annotation.Nullable
import javax.lang.model.element.Modifier

class EnumGenerator(private val configuration: IconicGeneratorPluginExtension, private val items: Map<String, String>) {

    fun build(): TypeSpec {
        val builder = TypeSpec.enumBuilder("Icon")
                .addSuperinterface(iiconType)
                .addModifiers(Modifier.PUBLIC)
                .addField(createTypeFaceField())
                .addField(createCharacterField())
                .addMethod(createGetCharacterFunction())
                .addMethod(createGetFormattedNameFunction())
                .addMethod(createGetNameFunction())
                .addMethod(createGetTypefaceFunction())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(Char::class.java, "character")
                        .addStatement("this.character = character")
                        .build())

        createEnumValue(builder)
        return builder.build()
    }

    private fun createTypeFaceField(): FieldSpec? {
        return FieldSpec.builder(ClassGenerator.itypefaceType, "typeface")
                .initializer("null")
                .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
                .build()
    }

    private fun createCharacterField(): FieldSpec? {
        return FieldSpec.builder(Char::class.java, "character")
                .build()
    }

    private fun createEnumValue(builder: TypeSpec.Builder) {
        for (item in items.entries) {
            val name = item.key.replace(".eof", configuration.code).replace("-", "_")
            val value = item.value.replace("\\e", "\\ue");
            val code = Integer.parseInt(value.substring(1, value.length - 1).substring(2), 16)
            builder.addEnumConstant(name, TypeSpec.anonymousClassBuilder("'\\u\$L\'", Integer.toHexString(code)).build())
        }
    }

    private fun createGetFormattedNameFunction(): MethodSpec {
        return MethodSpec.methodBuilder("getFormattedName")
                .addAnnotation(Override::class.java)
                .addAnnotation(NonNull::class.java)
                .returns(String::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return \"{\" + name() + \"}\"")
                .build()
    }

    private fun createGetCharacterFunction(): MethodSpec {
        return MethodSpec.methodBuilder("getCharacter")
                .addAnnotation(Override::class.java)
                .returns(Char::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return character")
                .build()
    }

    private fun createGetNameFunction(): MethodSpec {
        return MethodSpec.methodBuilder("getName")
                .addAnnotation(Override::class.java)
                .addAnnotation(NonNull::class.java)
                .returns(String::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return name()")
                .build()
    }

    private fun createGetTypefaceFunction(): MethodSpec {
        return MethodSpec.methodBuilder("getTypeface")
                .addAnnotation(Override::class.java)
                .addAnnotation(Nullable::class.java)
                .returns(itypefaceType)
                .addModifiers(Modifier.PUBLIC)
                .beginControlFlow("if (typeface == null)")
                .addStatement("typeface = new %s()".format(configuration.name))
                .endControlFlow()
                .addStatement("return typeface")
                .build()
    }

    companion object {
        val iiconType = ClassName.get("br.com.entregadoronline.support.widget.iconic.typeface", "IIcon")
        val itypefaceType = ClassName.get("br.com.entregadoronline.support.widget.iconic.typeface", "ITypeface")
    }

}