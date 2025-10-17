pipeline {
    agent {
        // Custom runner groups in Jenkins require agent labels matching your Jenkins nodes
        label env.RUNNER_GROUP_NAME ?: 'default'
    }
    environment {
        CUSTOM_MESSAGE   = "${params.custom_message ?: 'Default message'}"
        RUN_EXTRA_STEPS  = "${params.run_extra_steps ?: false}"
        ARTIFACT_NAME    = "${params.artifact_name ?: 'workspace-archive'}"
        NODE_VERSION     = "${params.node_version ?: '20'}"
        JAVA_VERSION     = "${params.java_version ?: '21'}"
        PYTHON_VERSION   = "${params.python_version ?: '3.11'}"
        SLACK_WEBHOOK_URL = credentials('SLACK_WEBHOOK_URL')
    }
    options { skipDefaultCheckout() }
    triggers {
        // Configure GitHub hook trigger for GITScm polling in your job settings for similar behavior
    }
    stages {
        stage('Checkout Code') { steps { checkout scm } }
        stage('Show current directory') { steps { sh "pwd" } }
        stage('Print current date') { steps { sh "date" } }
        stage('Print first 10 lines of README') { steps {
            sh '''
            if [ -f README.md ]; then head -10 README.md; else echo "No README.md"; fi
            '''
        } }
        stage('Create a temp file') { steps { sh 'echo "$CUSTOM_MESSAGE" > temp.txt' } }
        stage('Set up Java') { steps { tool name: "jdk-${JAVA_VERSION}", type: 'hudson.model.JDK' } }
        stage('Set up Node.js') { steps {
            script {
                def nodeHome = tool name: "nodejs-${NODE_VERSION}", type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
                env.PATH = "${nodeHome}/bin:${env.PATH}"
            }
        } }
        stage('Upload temp file') { steps { archiveArtifacts artifacts: 'temp.txt', fingerprint: true } }
        stage('Download temp file') { steps { echo "Artifacts: use current build or Copy Artifact plugin for cross-build." } }
        stage('Cache Gradle dependencies') { steps { echo "Cache: use external/shared cache directories or plugins if needed." } }
        stage('Set up Python') { steps { sh "echo 'Configure Python ${PYTHON_VERSION} as per your Jenkins setup'" } }
        stage('Archive workspace') { steps { sh 'tar czf workspace.tar.gz .' } }
        stage('Upload workspace archive') { steps { archiveArtifacts artifacts: 'workspace.tar.gz', fingerprint: true } }
        stage('Extra step (conditional)') {
            when { expression { env.RUN_EXTRA_STEPS == 'true' } }
            steps { echo "Running extra steps as requested!" }
        }
    }
    post {
        failure {
            echo 'Workflow failed. Please investigate.'
            slackSend channel: '#ci', color: 'danger', message: "Job failed: ${env.JOB_NAME} [${env.BUILD_NUMBER}]."
        }
        always {
            slackSend channel: '#ci', color: currentBuild.result == 'SUCCESS' ? 'good' : 'danger', message: "Build ${env.BUILD_NUMBER}: ${currentBuild.result}"
        }
    }
}
