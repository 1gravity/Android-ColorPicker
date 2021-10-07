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

val sourceFiles = android.sourceSets.getByName("main").java.getSourceFiles()

tasks.register<Javadoc>("withJavadoc") {
    isFailOnError = false
    dependsOn(tasks.named("compileDebugSources"), tasks.named("compileReleaseSources"))

    // add Android runtime classpath
    android.bootClasspath.forEach { classpath += project.fileTree(it) }

    // add classpath for all dependencies
    android.libraryVariants.forEach { variant ->
        variant.javaCompileProvider.get().classpath.files.forEach { file ->
            classpath += project.fileTree(file)
        }
    }

    source = sourceFiles
}

val withJavadocJar = tasks.register<Jar>("withJavadocJar") {
    archiveClassifier.set("javadoc")
    dependsOn(tasks.named("withJavadoc"))
    val destination = tasks.named<Javadoc>("withJavadoc").get().destinationDir
    from(destination)
}

val withSourcesJar = tasks.register<Jar>("withSourcesJar") {
    archiveClassifier.set("sources")
    from(sourceFiles)
}

afterEvaluate {
    publishing {
        repositories(project)
        publications {
            val publicationName = project.properties["POM_NAME"]?.toString() ?: "publication"
            create<MavenPublication>(publicationName) {
                configure(project, withJavadocJar, withSourcesJar)
            }
            signing {
                sign(publishing.publications.getByName(publicationName))
            }
        }
    }
}
