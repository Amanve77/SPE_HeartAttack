pipeline {
    agent any

    stages {
        stage('Detect Changed Folders') {
            steps {
                script {
                    def commits = sh(script: "git rev-list HEAD --count", returnStdout: true).trim().toInteger()

                    if (commits < 2) {
                        echo "First commit detected. Triggering all jobs."
                        build job: 'backend-pipeline'
                        build job: 'frontend-pipeline'
                        build job: 'ml-pipeline'
                        return
                    }

                    def changeLog = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim().split("\n")
                    def backendChanged = changeLog.any { it.startsWith("backend/") }
                    def frontendChanged = changeLog.any { it.startsWith("frontend/") }
                    def mlChanged = changeLog.any { it.startsWith("ml_service/") }

                    echo "Changed files:\n${changeLog.join('\n')}"
                    echo "Backend changed: ${backendChanged}"
                    echo "Frontend changed: ${frontendChanged}"
                    echo "ML Service changed: ${mlChanged}"

                    if (backendChanged) {
                        build job: 'heartattack-backend'
                    }
                    if (frontendChanged) {
                        build job: 'heartattack-frontend'
                    }
                    if (mlChanged) {
                        build job: 'heartattack-ml'
                    }

                    if (!backendChanged && !frontendChanged && !mlChanged) {
                        echo "No relevant changes found. Skipping downstream builds."
                    }
                }
            }
        }
    }
}
