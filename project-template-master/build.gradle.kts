plugins {
    java
    application
}

group = "me.template"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("fr.inria.gforge.spoon:spoon-core:11.2.1-beta-11")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("org.junit.jupiter:junit-jupiter:5.11.4")
}

application {
    mainClass.set("me.template.Main")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks {
    test {
        useJUnitPlatform()
    }

    jar {
        manifest {
            attributes(
                "Main-Class" to "me.template.Main",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        }
        from(configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        })

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}