package fr.ligol.iconics_generator

object GenerateFont {
    @JvmStatic
    fun main(args: Array<String>) {
        /*val value = "\"\\e018\"".replace("\\e", "\\ue")
        println(value)
        println(Integer.parseInt(value.substring(1, value.length - 1).substring(2), 16).toChar())*/

        val fl = javaClass.classLoader.getResource("style.css").readText()
        val parser = CssParser(fl)
        val itemMap = parser.getFontItems()

        val file = ClassGenerator(IconicGeneratorPluginExtension(), itemMap).build()
        parser.getTTFUrl()?.let {
            val ttfFile = javaClass.classLoader.getResource("eonline.ttf").readBytes()
            GenerateIconicsProjectTask().createFolderAndFile(IconicGeneratorPluginExtension(), file, ttfFile)
        }
        return

    }
}
