pipeline {
    agent any

    stages {
        stage('Deploy Grafana') {
            steps {
                script {
                    sh '''
                    # Delete old deployment if it exists
                    kubectl delete deployment grafana -n monitoring --ignore-not-found
                    kubectl delete svc grafana-service -n monitoring --ignore-not-found
                    kubectl delete configmap grafana-datasource -n monitoring --ignore-not-found
                    kubectl delete configmap grafana-dashboard -n monitoring --ignore-not-found

                    # Apply updated files
                    kubectl create configmap grafana-datasource --from-file=monitoring/grafana/datasources.yaml -n monitoring --dry-run=client -o yaml | kubectl apply -f -
                    kubectl create configmap grafana-dashboard --from-file=monitoring/grafana/dashboard-config.yaml -n monitoring --dry-run=client -o yaml | kubectl apply -f -
                    kubectl apply -f monitoring/grafana/deployment.yaml -n monitoring
                    kubectl apply -f monitoring/grafana/service.yaml -n monitoring
                    '''
                }
            }
        }
    }
}
