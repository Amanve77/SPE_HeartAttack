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

        stage('Install Dependencies') {
            steps {
                dir('microservices/frontend') {
                    sh 'npm ci' 
                }
            }
        }

        stage('Build') {
            steps {
                dir('microservices/frontend') {
                    sh 'npm run build'
                }
            }
        }

        stage('Lint') {
            steps {
                dir('microservices/frontend') {
                    sh '''
                        npm run lint || echo "Lint warnings found, continuing..."
                    '''  
                }
            }
        }

        stage('Test') {
            steps {
                dir('microservices/frontend') {
                    sh '''
                        npm test -- --watchAll=false --ci --coverage
                    '''
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
                sh '''
                    echo "Using kubeconfig at: $KUBECONFIG"
                    kubectl apply -f k8s/namespace.yaml
                    kubectl apply -f k8s/frontend/
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
        success {
            echo 'Frontend pipeline completed successfully!'
        }
        always {
            cleanWs()
        }
    }
}
