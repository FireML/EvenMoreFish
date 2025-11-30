plugins {
    id("java")
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

//todo, should be in its own plugin
tasks {
    if (project.name.contains("addons")) {
        jar {
            val addonName = defaultAddonName(project.name)
            archiveFileName.set(addonName)
        }
    }
}

fun defaultAddonName(project: String): String {
    val jvmVersion = project.split("-")[4].uppercase()
    return "EMF-Addons-${jvmVersion}.addon"
}

