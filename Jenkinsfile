pipeline {
    agent any

    stages {
        stage('Detect Changed Folders') {
            steps {
                script {
                    def commits = sh(script: "git rev-list HEAD --count", returnStdout: true).trim().toInteger()

                    if (commits < 2) {
                        echo "First commit detected. Triggering all jobs."
                        build job: 'heartattack-backend'
                        build job: 'heartattack-frontend'
                        build job: 'heartattack-ml'
                        return
                    }

                    def changeLog = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim().split("\n")
                    def backendChanged = changeLog.any { it.startsWith("microservices/backend/") || it.startsWith("K8s/backend/") || it.startsWith("jenkins/backend-pipeline.groovy")}
                    def frontendChanged = changeLog.any { it.startsWith("microservices/frontend/") || it.startsWith("K8s/frontend/") || it.startsWith("jenkins/frontend-pipeline.groovy")}
                    def mlChanged = changeLog.any { it.startsWith("microservices/ml-service/") || it.startsWith("K8s/ml/") || it.startsWith("jenkins/ml-pipeline.groovy")}
                    
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
        stage('Deploy with Ansible') {
            steps {
                dir('ansible') {
                    sh '''
                        sudo apt-get update
                        sudo apt-get install -y ansible
                        ansible-playbook -i inventory deploy.yml
                    '''
                }
            }
        }
    }
}
