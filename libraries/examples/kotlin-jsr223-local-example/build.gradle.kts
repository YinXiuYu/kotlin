
description = "Sample Kotlin JSR 223 scripting jar with local (in-process) compilation and evaluation"

plugins {
    kotlin("jvm")
}

dependencies {
    compile(project(":kotlin-stdlib"))
    compile(project(":kotlin-script-runtime"))
    compile(projectRuntimeJar(":kotlin-compiler-embeddable"))
    compile(project(":kotlin-script-util"))
    testCompile(project(":kotlin-test:kotlin-test-junit"))
    testCompile(commonDep("junit:junit"))
    testRuntime(project(":kotlin-reflect"))
    compileOnly(project(":compiler:cli-common")) //  TODO(JPS): fix import
    testCompile(project(":core:util.runtime")) //  TODO(JPS): fix import
    testCompile(project(":compiler:daemon-common")) //  TODO(JPS): fix import
}

projectTest()
