// unit-tests.Jenkinsfile
pipeline {
    agent any
    
    options {
        // Preserve test results
        preserveStashes(buildCount: 5)
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build & Unit Tests') {
            steps {
                sh './gradlew clean build'
            }
            post {
                always {
                    // Archive the test results (equivalent to actions/upload-artifact)
                    junit '**/build/test-results/test/*.xml'
                    archiveArtifacts artifacts: '**/build/test-results/test/*.xml', allowEmptyArchive: true
                }
            }
        }
    }
}
