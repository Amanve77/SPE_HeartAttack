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
