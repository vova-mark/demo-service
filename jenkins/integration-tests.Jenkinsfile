// integration-tests.Jenkinsfile
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
        
        stage('Run Integration Tests') {
            steps {
                sh './gradlew integrationTests'
            }
            post {
                always {
                    // Archive the test results (equivalent to actions/upload-artifact)
                    junit '**/build/test-results/integrationTests/*.xml'
                    archiveArtifacts artifacts: '**/build/test-results/integrationTests/*.xml', allowEmptyArchive: true
                }
            }
        }
    }
}
