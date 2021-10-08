/*
 * Copyright (C) 2015-2021 Emanuel Moecklin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("com.android.library")
    id("maven-publish")
    id("signing")
}

android {
    compileSdk = Build.compileSdkVersion
    buildToolsVersion = Build.buildToolsVersion

    defaultConfig {
        minSdk = Build.minSdkVersion
        targetSdk = Build.targetSdkVersion
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    lint {
        isAbortOnError = true
        disable("UnusedResources")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api("androidx.preference:preference:1.1.1")

    // used for the material design (dialogs and tab layout)
    api("androidx.appcompat:appcompat:1.3.1")
}

tasks {
    val sourceFiles = android.sourceSets.getByName("main").java.srcDirs

    register<Javadoc>("withJavadoc") {
        isFailOnError = false
        dependsOn(android.libraryVariants.toList().last().javaCompileProvider)
        if (! project.plugins.hasPlugin("org.jetbrains.kotlin.android")) {
            setSource(sourceFiles)
        }

        // add Android runtime classpath
        android.bootClasspath.forEach { classpath += project.fileTree(it) }

        // add classpath for all dependencies
        android.libraryVariants.forEach { variant ->
            variant.javaCompileProvider.get().classpath.files.forEach { file ->
                classpath += project.fileTree(file)
            }
        }

        // We don't need javadoc for internals.
        exclude("**/internal/*")

        // Append Java 8 and Android references
        val options = options as StandardJavadocDocletOptions
        options.links("https://developer.android.com/reference")
        options.links("https://docs.oracle.com/javase/8/docs/api/")

        // Workaround for the following error when running on on JDK 9+
        // "The code being documented uses modules but the packages defined in ... are in the unnamed module."
        if (JavaVersion.current() >= JavaVersion.VERSION_1_9) {
            options.addStringOption("-release", "8")
        }
    }

    register<Jar>("withJavadocJar") {
        archiveClassifier.set("javadoc")
        dependsOn(named("withJavadoc"))
        val destination = named<Javadoc>("withJavadoc").get().destinationDir
        from(destination)
    }

    register<Jar>("withSourcesJar") {
        archiveClassifier.set("sources")
        from(sourceFiles)
    }
}

afterEvaluate {
    publishing {
        repositories(project)
        publications {
            val publicationName = project.properties["POM_NAME"]?.toString() ?: "publication"
            create<MavenPublication>(publicationName) {
                val javadocTask = tasks.named<Jar>("withJavadocJar")
                val sourceTask = tasks.named<Jar>("withSourcesJar")
                configure(project, javadocTask, sourceTask)
            }
            signing {
                sign(publishing.publications.getByName(publicationName))
            }
        }
    }
}
