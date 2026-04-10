# FregeIntelliJPlugin

![Build](https://github.com/mse-p8/FregeIntellIJPlugin/workflows/Build/badge.svg)

<!-- Plugin description -->
This is the IntelliJ plugin for Frege! It provides IDE support for
the [Frege programming language](https://github.com/Frege/frege/) using
the [Frege Language Server](https://github.com/poeik/frege-ls/). 

It depends on the Frege Language Server version `v1.0.2` & Frege version `3.25.84`.

> Note 1: Make sure your Frege files are located in `./src/main/frege` or in the directory stated in the environment
> variable `FREGE_LS_SOURCE_DIR`. The Frege Language Server uses the file system to resolve modules. Therefore, always use
> the file path relative to `FREGE_LS_SOURCE_DIR` as module name.

> Note 2 Syntax highlighting: Open Settings -> Editor -> File Types -> Recognized File Types. Search for Haskell and add
>`*.fr` to `File name patterns:`

### Java interoperability

#### Accessing Java files

If you want to access Java files along with your Frege code make sure to put
the Java code in `./src/main/java` or into the directory stated in the
environment variable `FREGE_LS_JAVA_SOURCE_DIR`.

#### Accessing Jars

If you want to use external Jar files along with your Frege code make sure to put
them under the `./lib` folder or into the directory stated in the
environment variable `FREGE_LS_EXTRA_CLASSPATH`.


<!-- Plugin description end -->

## Installation

We currently only support manual installation. The plugin depends on the IntelliJ Language Server Protocol support.
See [here](https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html#supported-ides) to find out which
IDEs are supported!

  Download the [latest release](https://github.com/poeik/FregeIntellIJPlugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Developing

Tested with gradle 8.14.3, IntelliJ IDEA 2025.1.2 and Kotlin 2.2.0.

### Building the source

Use `./gradlew buildPlugin`. It might take a while as it downloads many Jetbrains dependencies

### Running it locally

Use `./gradlew runIde` to start a new IntelliJ instance with the current version of the plugin installed.

### Upgrading the Frege Language Server

1. Download the latest release tar [here](https://github.com/poeik/frege-ls/releases) and replace the version in
   `src/main/resources/fregels`.
2. Change the version of Frege/FregeLS
   in [Versions.kt](/src/main/kotlin/ch/fhnw/fregeintellijplugin/lspserver/Versions.kt), in this Readme and
   in [gradle.properties](./gradle.properties).
3. Commit & Push
4. Run `./gradlew buildPlugin`
5. Create a new GitHub Release with and upload the contents of `./build/distributions` to it

---

Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
