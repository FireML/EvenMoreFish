plugins {
    id("com.oheers.evenmorefish.java-conventions")
    `java-library`
}

group = "com.oheers.evenmorefish"
version = properties["project-version"] as String

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.annotations)
    compileOnly(libs.universalscheduler)
    compileOnlyApi(libs.boostedyaml)
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

publishing {
    repositories { // Copied directly from CodeMC's docs
        maven {
            url = uri("https://repo.codemc.io/repository/EvenMoreFish/")

            val mavenUsername = System.getenv("JENKINS_USERNAME")
            val mavenPassword = System.getenv("JENKINS_PASSWORD")

            if (mavenUsername != null && mavenPassword != null) {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
    publications {
        create<MavenPublication>("api") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                implementation(libs.junit.jupiter.api)
                runtimeOnly(libs.junit.jupiter.engine)
            }

            targets {
                all {
                    testTask.configure {
                        useJUnitPlatform()
                    }
                }
            }
        }
    }
}