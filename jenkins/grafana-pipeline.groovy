pipeline {
    agent any

    stages {
        stage('Deploy Grafana') {
            steps {
                script {
                    sh 'kubectl apply -f monitoring/grafana/deployment.yaml -n monitoring'
                    sh 'kubectl apply -f monitoring/grafana/service.yaml -n monitoring'
                    sh 'kubectl create configmap grafana-datasource --from-file=monitoring/grafana/datasources.yaml -n monitoring --dry-run=client -o yaml | kubectl apply -f -'
                    sh 'kubectl create configmap grafana-dashboard --from-file=monitoring/grafana/dashboard-config.yaml -n monitoring --dry-run=client -o yaml | kubectl apply -f -'
                }
            }
        }
    }
}
