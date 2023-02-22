import org.jetbrains.compose.ComposeBuildConfig.composeVersion

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("maven-publish")
    id("signing")
}

group = "com.whoop.composereorderable"
version = "0.109.7" // we add 100 to the minor version to avoid conflicts with upstream version

kotlin {
    jvm()
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.animation)
                implementation("org.jetbrains.compose.ui:ui-util:${composeVersion}")
            }
        }
    }
}

val javadocJar = tasks.register("javadocJar", Jar::class.java) {
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        repositories {
            maven {
                name="internal"
                val releasesRepoUrl = extra.properties.getOrDefault("internalUrl", "") as String
                val snapshotsRepoUrl = extra.properties.getOrDefault("internalSnapshotUrl", "") as String
                url = if (version.toString().endsWith("SNAPSHOT")) uri(snapshotsRepoUrl) else uri(releasesRepoUrl)
                credentials {
                    username = extra.properties.getOrDefault("internalUsername", "") as String
                    password = extra.properties.getOrDefault("internalPassword", "") as String
                }
            }
        }
    }
    publications {
        withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name.set("ComposeReorderable")
                description.set("Reorderable Compose LazyList")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                url.set("https://github.com/WhoopInc/ComposeReorderable")
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/WhoopInc/ComposeReorderable/issues")
                }
                scm {
                    connection.set("https://github.com/WhoopInc/ComposeReorderable.git")
                    url.set("https://github.com/WhoopInc/ComposeReorderable")
                }
                developers {
                    developer {
                        name.set("Andre Claßen")
                        email.set("andreclassen1337@gmail.com")
                    }
                }
            }
        }
    }
}

signing {
    isRequired = false
    sign(publishing.publications)
}
