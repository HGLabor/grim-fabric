val includeImplementation: Configuration by configurations.creating {
  configurations.implementation.configure { extendsFrom(this@creating) }
}

dependencies {
  include(api(project(":grim-api", configuration = "namedElements"))!!)

  modApi(libs.bundles.fabric)
  modApi(libs.bundles.silk)
  modApi(libs.bundles.performance)

  includeImplementation(libs.configuralize)
  includeImplementation(libs.viaversion.api)
  includeImplementation(libs.luckperms.api)
  includeImplementation(libs.discord)
  includeImplementation(libs.spark.api)
  libs.packetevents.also {
    modImplementation(it)
    include(it)
  }
  libs.kyori.also {
    modImplementation(it)
    include(it)
  }

  handleIncludes(includeImplementation)
}

loom {
  accessWidenerPath.set(file("src/main/resources/grim.accesswidener"))
}

/* Thanks to https://github.com/jakobkmar for this script */
fun DependencyHandlerScope.includeTransitive(
  dependencies: Set<ResolvedDependency>,
  fabricLanguageKotlinDependency: ResolvedDependency?,
  checkedDependencies: MutableSet<ResolvedDependency> = HashSet()
) {
  val minecraftDependencies = listOf(
    "slf4j-api",
    "commons-logging",
    "oshi-core",
    "jna",
    "jna-platform",
    "gson",
    "commons-lang3",
    "jackson-annotations",
    "jackson-core",
    "jackson-databind",
  )

  dependencies.forEach {
    if (checkedDependencies.contains(it) /*|| it.moduleGroup == "org.jetbrains.kotlin" || it.moduleGroup == "org.jetbrains.kotlinx"*/) return@forEach

    if (it.name.startsWith("net.fabric")) {
      println("Skipping -> ${it.name}")
      checkedDependencies += it
      return@forEach
    }

    if (it.name.startsWith("net.kyori")) {
      println("Skipping -> ${it.name}")
      checkedDependencies += it
      return@forEach
    }

    if (it.name.startsWith("net.silkmc")) {
      println("Skipping -> ${it.name}")
      checkedDependencies += it
      return@forEach
    }

    if (fabricLanguageKotlinDependency?.children?.any { kotlinDep -> kotlinDep.name == it.name } == true) {
      println("Skipping -> ${it.name} (already in fabric-language-kotlin)")
    } else if (minecraftDependencies.any { dep -> dep == it.moduleName }) {
      println("Skipping -> ${it.name} (already in minecraft)")
    } else {
      include(it.name)
      println("Including -> ${it.name}")
    }
    checkedDependencies += it

    includeTransitive(it.children, fabricLanguageKotlinDependency, checkedDependencies)
  }
}

fun DependencyHandlerScope.implementAndInclude(dep: Any) {
  modImplementation(dep)
  include(dep)
}

fun DependencyHandlerScope.handleIncludes(configuration: Configuration) {
  includeTransitive(
    configuration.resolvedConfiguration.firstLevelModuleDependencies,
    configurations.modImplementation.get().resolvedConfiguration.firstLevelModuleDependencies
      .firstOrNull() { it.moduleGroup == "net.fabricmc" && it.moduleName == "fabric-language-kotlin" },
  )
}
