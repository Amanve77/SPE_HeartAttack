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

        stage('Model Training & Evaluation') {
            steps {
                dir('microservices/ml-service') {
                    sh 'dvc repo'
                }
            }
        }

        stage('DVC Push Artifacts (if updated)') {
            steps {
                dir('microservices/ml-service') {
                    sh '''
                        git config --global user.email "jenkins@example.com"
                        git config --global user.name "Jenkins"
                        dvc push
                        git add models/*.pkl models/*.dvc
                        git commit -m "CI: Updated model artifacts [skip ci]" || echo "No changes to commit"
                        git push origin HEAD:master
                    '''
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
                subject: "ML Pipeline Failed: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                body: "Check the build logs: ${env.BUILD_URL}",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
        success {
            echo '✅ ML pipeline completed successfully!'
        }
    }
}
