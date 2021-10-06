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

val props = project.properties

fun isReleaseBuild() = props["POM_VERSION_NAME"]?.toString()?.contains("SNAPSHOT") == false

fun getRepositoryUrl() = if (isReleaseBuild()) getReleaseRepositoryUrl() else getSnapshotRepositoryUrl()

fun getReleaseRepositoryUrl() = props["RELEASE_REPOSITORY_URL"]?.toString()
    ?: "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

fun getSnapshotRepositoryUrl() = props["SNAPSHOT_REPOSITORY_URL"]?.toString()
    ?: "https://oss.sonatype.org/content/repositories/snapshots/"

afterEvaluate {
    publishing {
        repositories {
            maven {
                url = uri(getRepositoryUrl())
                // credentials are stored in ~/.gradle/gradle.properties with ~ being the path of the home directory
                credentials {
                    username = props["NEXUS_USERNAME"]?.toString()
                        ?: throw IllegalStateException("user/NEXUS_USERNAME name not found")
                    password = props["NEXUS_PASSWORD"]?.toString()
                        ?: throw IllegalStateException("password/NEXUS_PASSWORD not found")
                }
            }
        }

        val publicationName = props["PUBLICATION_NAME"]?.toString() ?: "MavenLibrary"

        publications {
            create<MavenPublication>(publicationName) {
                groupId = props["POM_GROUP_ID"].toString()
                artifactId = props["POM_ARTIFACT_ID"].toString()
                version = props["POM_VERSION_NAME"].toString()

                from(components["release"])

                pom {
                    name.set(props["POM_NAME"].toString())
                    description.set(props["POM_DESCRIPTION"].toString())
                    url.set(props["POM_URL"].toString())
                    packaging = props["POM_PACKAGING"].toString()

                    scm {
                        url.set(props["POM_SCM_URL"].toString())
                        connection.set(props["POM_SCM_CONNECTION"].toString())
                        developerConnection.set(props["POM_SCM_DEV_CONNECTION"].toString())
                    }

                    organization {
                        name.set(props["POM_COMPANY_NAME"].toString())
                        url.set(props["POM_COMPANY_URL"].toString())
                    }

                    developers {
                        developer {
                            id.set(props["POM_DEVELOPER_ID"].toString())
                            name.set(props["POM_DEVELOPER_NAME"].toString())
                            email.set(props["POM_DEVELOPER_EMAIL"].toString())
                        }
                    }

                    licenses {
                        license {
                            name.set(props["POM_LICENCE_NAME"].toString())
                            url.set(props["POM_LICENCE_URL"].toString())
                            distribution.set(props["POM_LICENCE_DIST"].toString())
                        }
                    }
                }
            }

            signing {
                sign(publishing.publications.getByName(publicationName))
            }
        }
    }
}