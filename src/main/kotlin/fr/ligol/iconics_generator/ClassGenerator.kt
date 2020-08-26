package fr.ligol.iconics_generator

import com.squareup.javapoet.*
import java.util.*
import javax.lang.model.element.Modifier


class ClassGenerator(private val configuration: IconicGeneratorPluginExtension, private val items: Map<String, String>) {
    fun build(): JavaFile {

        val classfile = TypeSpec.classBuilder(configuration.name)
                .addJavadoc("Fonte que possui todos os icones (single color) do app Entregador Online.")
                .addSuperinterface(itypefaceType)
                .addModifiers(Modifier.PUBLIC)
                .addFields(createCompanionObject())
                .addMethod(createGetCharactersFunction())
                .addMethod(createGetIconsFunction())
                .addMethod(createOverrideFunctionReturningDefaultString("getMappingPrefix", configuration.code))
                .addMethod(createOverrideFunctionReturningDefaultString("getFontName", configuration.name))
                .addMethod(createOverrideFunctionReturningDefaultString("getVersion", configuration.versionName))
                .addMethod(createOverrideFunctionReturningDefaultString("getAuthor", configuration.author))
                .addMethod(createOverrideFunctionReturningDefaultString("getUrl", configuration.url))
                .addMethod(createOverrideFunctionReturningDefaultString("getDescription", configuration.description))
                .addMethod(createOverrideFunctionReturningDefaultString("getLicense", configuration.license))
                .addMethod(createOverrideFunctionReturningDefaultString("getLicenseUrl", configuration.licenseUrl))
                .addMethod(createIconCountFunction())
                .addMethod(createIconFunction())
                .addMethod(createGetTypefaceFunction())
                .addType(EnumGenerator(configuration, items).build())
                .build()


        val javaFile = JavaFile.builder(packageName, classfile).build()
        //javaFile.writeTo(System.out)
        return javaFile
    }

    private fun createGetTypefaceFunction(): MethodSpec {
        return MethodSpec.methodBuilder("getTypeface")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(contextType, "context")
                .returns(typefaceType)
                .beginControlFlow("if (typeface == null)")
                .beginControlFlow("try")
                .addStatement("typeface = Typeface.createFromAsset(context.getAssets(), \"fonts/\" + TTF_FILE)")
                .nextControlFlow("catch (Exception ex)")
                .addStatement("return null")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return typeface")
                .build()
    }

    private fun createIconFunction(): MethodSpec {
        return MethodSpec.methodBuilder("getIcon")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .addParameter(String::class.java, "key")
                .returns(iiconType)
                .addStatement("return Icon.valueOf(key)")
                .build()
    }

    private fun createIconCountFunction(): MethodSpec {
        return MethodSpec.methodBuilder("getIconCount")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .returns(Int::class.java)
                .addStatement("return mChars.size()")
                .build()
    }

    private fun createGetIconsFunction(): MethodSpec {
        return MethodSpec.methodBuilder("getIcons")
                .addAnnotation(Override::class.java)
                .returns(stringCollectionType)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("\$T icons = new \$T<>()", stringCollectionType, linkedListType)
                .beginControlFlow("for (Icon value : Icon.values())")
                .addStatement("icons.add(value.name())")
                .endControlFlow()
                .addStatement("return icons")
                .build()
    }

    private fun createGetCharactersFunction(): MethodSpec {
        return MethodSpec.methodBuilder("getCharacters")
                .addAnnotation(Override::class.java)
                .returns(charHashMapType)
                .addModifiers(Modifier.PUBLIC)
                .beginControlFlow("if (mChars == null)")
                .addStatement("\$T aChars = new \$T()", charHashMapType, charHashMapType)
                .beginControlFlow("for (Icon v : Icon.values())")
                .addStatement("aChars.put(v.name(), v.character)")
                .endControlFlow()
                .addStatement("mChars = aChars")
                .endControlFlow()
                .addStatement("return mChars")
                .build()
    }

    private fun createCompanionObject(): List<FieldSpec>? {
        val list = ArrayList<FieldSpec>()
        var field = FieldSpec.builder(String::class.java, "TTF_FILE")
                .initializer("\"%s-%s.ttf\"".format(configuration.name.toLowerCase(), configuration.versionName))
                .addModifiers(Modifier.FINAL, Modifier.STATIC, Modifier.PRIVATE)
                .build()
        list.add(field)
        field = FieldSpec.builder(typefaceType, "typeface")
                .initializer("null")
                .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
                .build()
        list.add(field)
        field = FieldSpec.builder(charHashMapType, "mChars")
                .initializer("null")
                .addModifiers(Modifier.STATIC, Modifier.PRIVATE)
                .build()
        list.add(field)
        return list
    }

    private fun createOverrideFunctionReturningDefaultString(name: String, value: String): MethodSpec {
        return MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .returns(String::class.java)
                .addStatement("return \$S", value)
                .build()
    }

    companion object {
        val packageName = "br.com.entregadoronline.font"
        val charHashMapType = ParameterizedTypeName.get(HashMap::class.java, String::class.java, Character::class.java)
        val stringCollectionType = ParameterizedTypeName.get(Collection::class.java, String::class.java)
        val contextType = ClassName.get("android.content", "Context")
        val typefaceType = ClassName.get("android.graphics", "Typeface")
        val itypefaceType = ClassName.get("br.com.entregadoronline.support.widget.iconic.typeface", "ITypeface")
        val iiconType = ClassName.get("br.com.entregadoronline.support.widget.iconic.typeface", "IIcon")
        val linkedListType = ClassName.get("java.util", "LinkedList")
    }
}