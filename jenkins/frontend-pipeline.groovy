pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'amanve7/heartattack-frontend-service'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Install Dependencies') {
            steps {
                dir('microservices/frontend') {
                    sh 'npm ci'  // Using ci instead of install for clean installs
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

        stage('Build') {
            steps {
                dir('microservices/frontend') {
                    sh 'npm run build'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                dir('microservices/frontend') {
                    script {
                        docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                            def customImage = docker.build("${DOCKER_IMAGE}")
                            customImage.push("${DOCKER_TAG}")
                            customImage.push("latest")
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    sh """
                        kubectl set image deployment/frontend-deployment frontend=${DOCKER_IMAGE}:${DOCKER_TAG} -n demo-basic
                    """
                }
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
