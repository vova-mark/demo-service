// api-tests.Jenkinsfile
pipeline {
    agent {
        // Equivalent to runs-on: ubuntu-latest
        label 'linux'
    }
    
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
        
        stage('Run API Isolated Tests') {
            steps {
                sh './gradlew apiIsolatedTests'
            }
            post {
                always {
                    // Archive the test results (equivalent to actions/upload-artifact)
                    junit '**/build/test-results/apiIsolatedTests/*.xml'
                    archiveArtifacts artifacts: '**/build/test-results/apiIsolatedTests/*.xml', allowEmptyArchive: true
                }
            }
        }
    }
}
