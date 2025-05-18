pipeline {
    agent any

    environment {
        IMAGE_NAME = 'amanve7/heartattack-backend-service'
        KUBECONFIG = '/var/lib/jenkins/.kube/config'   
        DOCKER_IMAGE = 'amanve7/heartattack-backend-service'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Amanve77/SPE_HeartAttack.git'
            }
        }

        stage('Test') {
            steps {
                dir('microservices/backend') {
                    sh 'mvn clean test'
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

        stage('Docker Build & Push') {
            steps {
                dir('microservices/backend') {
                    script {
                        docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                            def customImage = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                            customImage.push()
                            customImage.push('latest')
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                    echo "Using kubeconfig at: $KUBECONFIG"

                    # Create namespace
                    kubectl apply -f k8s/namespace.yaml

                    kubectl apply -f k8s/mysql/mysql-secret.yaml
                    
                    kubectl apply -f k8s/mysql/mysql-pvc.yaml

                    # Deploy MySQL first
                    kubectl apply -f k8s/mysql/
                    
                    echo "Waiting for MySQL pod to be ready..."
                    kubectl wait --for=condition=ready pod -l app=mysql -n demo-basic --timeout=120s
                    
                    # Deploy backend after MySQL is ready
                    kubectl apply -f k8s/backend/
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
    }
}
