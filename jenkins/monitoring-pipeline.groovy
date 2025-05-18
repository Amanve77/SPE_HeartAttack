pipeline {
    agent any
    stages {
        stage('Deploy Prometheus') {
            steps {
                dir('monitoring/prometheus') {
                    sh 'kubectl apply -f prometheus-deployment.yaml -n demo-basic'
                    sh 'kubectl apply -f prometheus-service.yaml -n demo-basic'
                }
            }
        }
    }
}
