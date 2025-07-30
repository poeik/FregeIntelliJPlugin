package ch.fhnw.fregeintellijplugin.lspserver


object OsUtils {
    private val OS: String by lazy { System.getProperty("os.name") }

    private fun getOsName(): String = OS

    private fun isWindows(): Boolean = getOsName().startsWith("Windows")

    fun osSpecificFregeLanguageServerExecutablePath(paths: FregeLanguageServerExecutablePath): String =
        if (isWindows()) paths.windowsPath
        else paths.unixPath
}