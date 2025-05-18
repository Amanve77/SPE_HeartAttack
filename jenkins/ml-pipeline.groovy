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
                sh '''
                    echo "Using kubeconfig at: $KUBECONFIG"
                    kubectl apply -f k8s/namespace.yaml
                    kubectl apply -f k8s/ml/
                '''
            }
        }
    }

    post {
        failure {
            emailext (
                subject: "Pipeline Failed: ${currentBuild.fullDisplayName}",
                body: "Something is wrong with ${env.BUILD_URL}",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
        always {
            dir('microservices/ml-service') {
                sh 'rm -rf venv'
            }
        }
    }
}
