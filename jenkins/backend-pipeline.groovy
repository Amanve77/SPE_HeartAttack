pipeline {
    agent any

    environment {
        IMAGE_NAME = 'amanve7/heartattack-backend-service'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Amanve77/SPE_HeartAttack.git'
            }
        }

        stage('Check for Changes in backend/') {
            when {
                expression {
                    // Get list of changed files
                    def changes = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
                    echo "Changed files:\n${changes}"
                    return changes.split("\n").any { it.startsWith("backend/") }
                }
            }
            steps {
                echo "Changes found in backend/, continuing with backend pipeline..."
                // Add build, test, deploy steps here
            }
        }

        stage('Skip Pipeline') {
            when {
                expression {
                    def changes = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
                    return !changes.split("\n").any { it.startsWith("backend/") }
                }
            }
            steps {
                echo "No changes in backend/. Skipping pipeline."
                script {
                    currentBuild.result = 'SUCCESS'
                    exit 0
                }
            }
        }


        stage('Build Spring Boot') {
            steps {
                dir('microservices/backend') {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('microservices/backend') {
                    sh 'docker build -t $IMAGE_NAME:latest .'
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh 'docker push $IMAGE_NAME:latest'
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh 'kubectl apply -f k8s/backend/'
            }
        }
    }
}
