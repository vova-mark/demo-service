// .teamcity/settings.kts

import jetbrains.buildServer.configs.kotlin.v2019_2.*

version = "2024.6"

project {

    // Build & Unit Tests
    buildType(BuildAndUnitTests)

    // Integration Tests
    buildType(IntegrationTests)

    // API Isolated Tests
    buildType(APIIsolatedTests)

    // Aggregate Pipeline
    buildType(AggregatePipeline)
}

object BuildAndUnitTests : BuildType({
    name = "Build & Unit Tests"
    steps {
        step {
            type = "git"
            name = "Checkout"
        }
        script {
            name = "Build & Run Unit Tests"
            scriptContent = "./gradlew clean build"
        }
    }
    artifactRules = "**/build/test-results/test/*.xml => unit-test-results"
    features {
        feature {
            type = "xml-report-plugin"
            param("xmlReportParsing.reportType", "junit")
            param("xmlReportParsing.reportDirs", "**/build/test-results/test/*.xml")
        }
    }
})

object IntegrationTests : BuildType({
    name = "Integration Tests"
    steps {
        step {
            type = "git"
            name = "Checkout"
        }
        script {
            name = "Run Integration Tests"
            scriptContent = "./gradlew integrationTests"
        }
    }
    artifactRules = "**/build/test-results/integrationTests/*.xml => integration-test-results"
    features {
        feature {
            type = "xml-report-plugin"
            param("xmlReportParsing.reportType", "junit")
            param("xmlReportParsing.reportDirs", "**/build/test-results/integrationTests/*.xml")
        }
    }
    dependencies {
        snapshot(BuildAndUnitTests) {}
    }
})

object APIIsolatedTests : BuildType({
    name = "API Isolated Tests"
    steps {
        step {
            type = "git"
            name = "Checkout"
        }
        script {
            name = "Run API Isolated Tests"
            scriptContent = "./gradlew apiIsolatedTests"
        }
    }
    artifactRules = "**/build/test-results/apiIsolatedTests/*.xml => api-test-results"
    features {
        feature {
            type = "xml-report-plugin"
            param("xmlReportParsing.reportType", "junit")
            param("xmlReportParsing.reportDirs", "**/build/test-results/apiIsolatedTests/*.xml")
        }
    }
    dependencies {
        snapshot(IntegrationTests) {}
    }
})

object AggregatePipeline : BuildType({
    name = "Aggregate Pipeline"
    type = BuildTypeSettings.Type.COMPOSITE
    dependsOnSnapshot(BuildAndUnitTests)
    dependsOnSnapshot(IntegrationTests)
    dependsOnSnapshot(APIIsolatedTests)
    steps {
        script {
            name = "Aggregate Step"
            scriptContent = "echo Aggregate Pipeline Complete"
        }
    }
})