package ch.fhnw.fregeintellijplugin.lspserver

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.io.FileUtil
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream

data class FregeLanguageServerExecutablePath(val windowsPath: String, val unixPath: String)

class FregeLsExecutableManager(serverVersion: String, fregeVersion: String) {
    private val logger = Logger.getInstance(FregeLsExecutableManager::class.java)
    private val serverTarResourcesPath = "/fregels/frege-ls-$serverVersion.tar"

    // internal tar structure
    private val tarRootDir = "frege-ls-$serverVersion"
    private val windowsServerPath = "$tarRootDir/bin/frege-ls.bat"
    private val unixServerPath = "$tarRootDir/bin/frege-ls"

    // if one of these files is missing, we reextract the tar
    private val requiredFilesPath =
        listOf(
            unixServerPath,
            windowsServerPath,
            "$tarRootDir/lib/frege$fregeVersion.jar",
            "$tarRootDir/lib/frege-ls-$serverVersion.jar"
        )

    fun getOrExtractLspServer(): FregeLanguageServerExecutablePath {
        val pluginRuntimeDir = getPluginDirectory() ?: throw IllegalStateException("No plugin directory")
        val unixServerAbsolutePath = "${pluginRuntimeDir.absolutePath}/${unixServerPath}"
        val windowsServerAbsolutePath = "${pluginRuntimeDir.absolutePath}/${windowsServerPath}"
        val paths = FregeLanguageServerExecutablePath(windowsServerAbsolutePath, unixServerAbsolutePath)

        if (!languageServerAlreadyExtracted(pluginRuntimeDir)) {
            thisLogger().info("Extracting language server tar")
            extractServer(pluginRuntimeDir)
            makeScriptsExecutable(unixServerAbsolutePath)
            makeScriptsExecutable(windowsServerAbsolutePath)
        }

        thisLogger().debug("Unix executable: ${paths.unixPath}")
        thisLogger().debug("Windows executable: ${paths.windowsPath}")

        return paths
    }

    private fun languageServerAlreadyExtracted(pluginRuntimeDir: File): Boolean =
        pluginRuntimeDir.exists() && extractionIsValid(pluginRuntimeDir)

    private fun extractServer(pluginRuntimeDir: File) =
        try {
            extractServerFromResources(pluginRuntimeDir)
        } catch (e: Exception) {
            logger.error("Failed to extract LSP server", e)
        }

    private fun getPluginDirectory(): File? {
        // Get the plugin's data directory
        val tempDir = FileUtil.getTempDirectory()
        val pluginTempDir = File(tempDir, "intellij-frege-plugin")
        if (!pluginTempDir.exists()) {
            pluginTempDir.mkdirs()
        }
        return pluginTempDir
    }

    private fun extractServerFromResources(pluginRuntimeDir: File) {
        cleanUpDir(pluginRuntimeDir)

        val serverTarStream = loadServerTarFromResources()

        serverTarStream.use { input ->
            extractLanguageServer(input, pluginRuntimeDir)
        }
    }

    private fun cleanUpDir(targetDir: File) {
        targetDir.deleteRecursively()
        targetDir.mkdirs()
    }

    private fun loadServerTarFromResources(): InputStream =
        (this::class.java.getResourceAsStream(serverTarResourcesPath)
            ?: throw IllegalStateException("LSP server tar not found in resources"))

    private fun extractLanguageServer(serverTarStream: InputStream, pluginRuntimeDir: File) {
        TarArchiveInputStream(serverTarStream).use { tarInput ->
            var entry = tarInput.nextEntry
            while (entry != null) {
                val file = File(pluginRuntimeDir, entry.name)

                if (entry.isDirectory) {
                    file.mkdirs()
                } else {
                    file.parentFile.mkdirs()
                    Files.copy(tarInput, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }

                entry = tarInput.nextEntry
            }
        }
    }

    private fun makeScriptsExecutable(scriptPath: String) {
        val scriptFile = File(scriptPath)
        scriptFile.setExecutable(true)
    }

    private fun extractionIsValid(pluginRuntimeDir: File): Boolean {
        // Check if key files exist to verify extraction is complete
        return requiredFilesPath.all { File(pluginRuntimeDir, it).exists() }
    }
}