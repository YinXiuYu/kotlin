
plugins {
    kotlin("jvm")
    id("jps-compatible")
}

dependencies {
    testRuntime(intellijDep())

    compile(project(":compiler:util"))
    compile(project(":jps-plugin"))
    compile(project(":plugins:android-extensions-compiler"))
    compileOnly(intellijDep()) { includeJars("openapi", "jps-builders", "jps-model", "jdom") }
    Platform[181].orHigher {
        compileOnly(intellijDep()) { includeJars("platform-api") }
    }
    compile(intellijPluginDep("android")) { includeJars("jps/android-jps-plugin") }

    testCompile(projectTests(":jps-plugin"))
    testCompile(commonDep("junit:junit"))
    testCompile(project(":kotlin-test:kotlin-test-jvm"))
    testCompile(projectTests(":kotlin-build-common"))
    testCompileOnly(intellijDep()) { includeJars("openapi", "jps-builders") }
    testCompileOnly(intellijDep("jps-build-test")) { includeJars("jps-build-test") }
    testCompileOnly(intellijDep()) { includeJars("jps-model") }

    if (System.getProperty("idea.active") != null) {
        // TODO(JPS): Fix import. Looks like library with same name are clashed.
        //            See compile(intellijPluginDep("android")) { includeJars("jps/android-jps-plugin") }.
        testCompile(intellijPluginDep("android"))
    } else {
        testRuntime(intellijPluginDep("android"))
    }
    (Platform[181].orHigher.or(Ide.AS31)) {
        testRuntime(intellijPluginDep("smali"))
    }
    testRuntime(intellijDep("jps-build-test"))
    testRuntime(intellijDep("jps-standalone"))
}

sourceSets {
    Ide.IJ {
        "main" { projectDefault() }
        "test" { projectDefault() }
    }

    Ide.AS {
        "main" {}
        "test" {}
    }
}

projectTest {
    workingDir = rootDir
    useAndroidSdk()
}

testsJar {}