pipeline {
    agent any

    environment {
        KUBECONFIG = "~/.kube/config"
    }

    stages {
        stage('Deploy ELK Stack') {
            steps {
                echo "Applying Elasticsearch..."
                sh 'kubectl apply -f elk/elasticsearch/elasticsearch-deployment.yaml -n demo-basic'

                echo "Applying Kibana..."
                sh 'kubectl apply -f elk/kibana/kibana-deployment.yaml -n demo-basic'

                echo "Applying Filebeat..."
                sh 'kubectl apply -f elk/filebeat/filebeat-k8s.yaml -n demo-basic'
            }
        }
    }
}
