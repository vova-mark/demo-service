pipeline {
    agent any

    // Pipeline parameters, similar to workflow inputs in GitHub Actions
    parameters {
        string(name: 'CUSTOM_MESSAGE', defaultValue: 'Default message', description: 'Custom message for temp file')
        booleanParam(name: 'RUN_EXTRA_STEPS', defaultValue: false, description: 'Run extra steps')
        string(name: 'ARTIFACT_NAME', defaultValue: 'workspace-archive', description: 'Workspace archive artifact name')
        string(name: 'NODE_VERSION', defaultValue: '20', description: 'Node.js version')
        string(name: 'JAVA_VERSION', defaultValue: '21', description: 'Java version')
        string(name: 'PYTHON_VERSION', defaultValue: '3.11', description: 'Python version')
    }

    triggers {
        // Simulate 'on push' and 'on pull_request' for 'main' branch
        // Jenkins requires webhooks/configuration for equivalent triggers
        // pollSCM('H/5 * * * *')
    }

    environment {
        SLACK_WEBHOOK_URL = credentials('SLACK_WEBHOOK_URL') // Needs to be defined in Jenkins credentials
    }

    stages {
        stage('Checkout code') {
            steps {
                // Jenkins checks out source code by default
                checkout scm
            }
        }
        stage('Show current directory') {
            steps {
                sh 'pwd'
            }
        }
        stage('Print current date') {
            steps {
                sh 'date'
            }
        }
        stage('Print first 10 lines of README') {
            steps {
                sh '''
                    if [ -f README.md ]; then head -10 README.md; else echo "No README.md"; fi
                '''
            }
        }
        stage('Create temp file') {
            steps {
                sh 'echo "${CUSTOM_MESSAGE}" > temp.txt'
            }
        }
        stage('Set up Java') {
            steps {
                // Use tool step or custom installation, e.g. with JDK Tool Plugin
                script {
                    def javaHome = tool name: "JDK${JAVA_VERSION}", type: 'hudson.model.JDK'
                    env.JAVA_HOME = javaHome
                }
            }
        }
        stage('Set up Node.js') {
            steps {
                // Use NodeJS Plugin for Jenkins
                script {
                    def nodeHome = tool name: "NodeJS${NODE_VERSION}", type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
                    env.PATH = "${nodeHome}/bin:${env.PATH}"
                }
            }
        }
        stage('Set up Python') {
            steps {
                // Use Python Plugin for Jenkins or ensure correct Python version is installed
                script {
                    def pythonHome = tool name: "Python${PYTHON_VERSION}", type: 'hudson.plugins.python.PythonInstallation'
                    env.PATH = "${pythonHome}/bin:${env.PATH}"
                }
            }
        }
        stage('Cache Gradle dependencies') {
            steps {
                // Jenkins cachs are typically handled with external plugins (not a built-in step)
                echo 'Gradle caching not natively supported like GitHub Actions. Consider using custom solutions or plugins.'
            }
        }
        stage('Archive workspace') {
            steps {
                sh 'tar czf workspace.tar.gz .'
            }
        }
        stage('Archive temp file') {
            steps {
                archiveArtifacts artifacts: 'temp.txt', onlyIfSuccessful: true
            }
        }
        stage('Archive workspace archive') {
            steps {
                archiveArtifacts artifacts: 'workspace.tar.gz', onlyIfSuccessful: true
            }
        }
        stage('Download temp file') {
            steps {
                // Artifacts can be accessed from previous builds via 'Copy Artifact' plugin, not natively like GitHub Actions
                echo 'To download a file from a previous job, use the Copy Artifact plugin.'
            }
        }
        stage('Extra step (conditional)') {
            when {
                expression { params.RUN_EXTRA_STEPS == true }
            }
            steps {
                echo "Running extra steps as requested!"
            }
        }
    }

    post {
        failure {
            script {
                // Create GitHub issue on failure: not natively supported; would require custom scripting or plugin
                echo 'Workflow failed. Please investigate. (Would create a GitHub issue here via API or plugin)'
            }
            // Slack notification (basic implementation)
            sh '''
                curl -X POST -H 'Content-type: application/json' --data '{"text":"Jenkins job failed!"}' "$SLACK_WEBHOOK_URL"
            '''
        }
    }
}