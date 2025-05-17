pipeline {
    agent any

    environment {
        IMAGE_NAME = 'amanve7/heartattack-ml-service'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Amanve77/SPE_HeartAttack.git'
            }
        }

        stage('Check if ML changed') {
            when {
                expression {
                    def changes = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim()
                    return changes.split('\n').any { it.startsWith('ml/') }
                }
            }
            steps {
                echo "ML folder changed. Proceeding with build..."
            }
        }

        stage('Install Dependencies') {
            steps {
                dir('microservices/ml-service') {
                    sh 'pip install -r requirements.txt'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('microservices/ml-service') {
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
                sh 'kubectl apply -f k8s/ml/'
            }
        }
    }
}
