package ch.fhnw.fregeintellijplugin.lspserver

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.platform.lsp.api.customization.LspDiagnosticsSupport
import java.io.File
import java.lang.IllegalStateException
import java.nio.charset.StandardCharsets

internal class FregeLspServerSupportProvider : LspServerSupportProvider {
    val fregeLanguageServerExecutablePath: String

    init {
        thisLogger().info("Starting Frege LSP Server")
        val manager = FregeLsExecutableManager(Versions.FREGE_LS, Versions.FREGE)
        val path = manager.getOrExtractLspServer()
        fregeLanguageServerExecutablePath  = OsUtils.osSpecificFregeLanguageServerExecutablePath(path)
    }

    override fun fileOpened(
        project: Project,
        file: VirtualFile,
        serverStarter: LspServerSupportProvider.LspServerStarter
    ) {
        thisLogger().info("Opened ${file.path}")
        if ("fr" == file.extension) {
            thisLogger().info("attaching Frege server to it.")
            serverStarter.ensureServerStarted(FregeLspServerDescriptor(fregeLanguageServerExecutablePath, project))
        }
    }
}

private class FregeLspServerDescriptor(private val fregeServerPath: String, project: Project) :
    ProjectWideLspServerDescriptor(project, "Frege") {
    override val lspDiagnosticsSupport: LspDiagnosticsSupport?
        get() = super.lspDiagnosticsSupport

    override fun isSupportedFile(file: VirtualFile): Boolean = file.extension == "fr"

    override fun createCommandLine(): GeneralCommandLine {
        val cmdLine =
            GeneralCommandLine(fregeServerPath)
        cmdLine.charset = StandardCharsets.UTF_8
        val basePath = project.basePath ?: throw IllegalStateException("No project base path set, exiting")

        cmdLine.workDirectory = File(basePath)
        thisLogger().warn("working dir ${project.basePath}")

        return cmdLine
    }
}