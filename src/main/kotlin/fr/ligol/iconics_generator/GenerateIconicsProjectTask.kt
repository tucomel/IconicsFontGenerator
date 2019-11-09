package fr.ligol.iconics_generator

import com.squareup.javapoet.JavaFile
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException


open class GenerateIconicsProjectTask {


    fun createFolderAndFile(configuration: IconicGeneratorPluginExtension, file: JavaFile, ttfByteArray: ByteArray) {
        val fileGenerator = FileGenerator(configuration)
        val rootFolder = File("%s-font/".format(configuration.name.toLowerCase()))
        val mainFolder = File(rootFolder.path + "/src/main/")
        val fontFolder = File(mainFolder.path + "/assets/fonts")
        val resFolder = File(mainFolder.path + "/res/values")
        val codeFolder = File(mainFolder.path + "/java/br/com/entregadoronline/support/widget/iconic/typeface/%s".format(configuration.name.toLowerCase()))
        val codeFile = File(codeFolder.path + "/%s.java".format(configuration.name))
        val ttfFile = File(fontFolder.path + "/%s-%s.ttf".format(configuration.name.toLowerCase(), configuration.versionName))

        rootFolder.mkdirs()
        mainFolder.mkdirs()
        fontFolder.mkdirs()
        resFolder.mkdirs()
        codeFolder.mkdirs()

        fileGenerator.generateFile("template/build.gradle", rootFolder.path + "/build.gradle")
        fileGenerator.generateFile("template/consumer-proguard-rules.pro", rootFolder.path + "/consumer-proguard-rules.pro")
        fileGenerator.generateFile("template/gradle.properties", rootFolder.path + "/gradle.properties")
        fileGenerator.generateFile("template/src/main/AndroidManifest.xml", mainFolder.path + "/AndroidManifest.xml")
        fileGenerator.generateFile("template/src/main/res/values/font_addon.xml", resFolder.path + "/font_addon.xml")

        file.writeTo(System.out)
        codeFile.writeText(file.toString())
        ttfFile.writeBytes(ttfByteArray)
    }

    fun downloadFile(url: String): ResponseBody? {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Failed to download file: $response")
        }
        return response.body()
    }
}