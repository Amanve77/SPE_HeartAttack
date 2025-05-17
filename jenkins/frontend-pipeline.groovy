pipeline {
    agent any

    environment {
        IMAGE_NAME = 'amanve7/heartattack-frontend-service'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Amanve77/SPE_HeartAttack.git'
            }
        }

        stage('Build React App') {
            steps {
                dir('microservices/frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir('microservices/frontend') {
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
                sh 'kubectl apply -f k8s/frontend/'
            }
        }
    }
}
