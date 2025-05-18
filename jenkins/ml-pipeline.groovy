pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'amanve7/heartattack-ml-service'
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
                dir('microservices/ml-service') {
                    sh '''
                        python3 -m venv venv
                        . venv/bin/activate
                        pip install -r requirements.txt
                    '''
                }
            }
        }
        '''
        stage('Test') {
            steps {
                dir('microservices/ml-service') {
                    sh ''''''
                        . venv/bin/activate
                        pytest --junitxml=test-results/junit.xml --cov=. --cov-report=html
                    ''''''
                }
            }
            post {
                always {
                    junit 'microservices/ml-service/test-results/junit.xml'
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'microservices/ml-service/htmlcov',
                        reportFiles: 'index.html',
                        reportName: 'ML Service Coverage Report'
                    ])
                }
            }
        }
        '''
        stage('Docker Build & Push') {
            steps {
                dir('microservices/ml-service') {
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
                script {
                    sh """
                        kubectl set image deployment/ml-deployment ml=${DOCKER_IMAGE}:${DOCKER_TAG} -n demo-basic
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
        always {
            dir('microservices/ml-service') {
                sh 'rm -rf venv'
            }
        }
    }
}
