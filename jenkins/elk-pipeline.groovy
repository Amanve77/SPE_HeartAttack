pipeline {
    agent any

    environment {
        KUBECONFIG = '/var/lib/jenkins/.kube/config'
    }

    stages {
        stage('Create ConfigMaps') {
            steps {
                script {
                    sh '''
                        kubectl create configmap elasticsearch-config --from-file=logging/elasticsearch/elasticsearch.yml -n demo-basic || true
                        kubectl create configmap filebeat-config --from-file=logging/filebeat/filebeat.yml -n demo-basic || true
                    '''
                }
            }
        }

        stage('Deploy Elasticsearch') {
            steps {
                script {
                    sh 'kubectl apply -f logging/elasticsearch/elasticsearch-deployment.yaml'
                }
            }
        }

        stage('Deploy Kibana') {
            steps {
                script {
                    sh 'kubectl apply -f logging/kibana/kibana-deployment.yaml'
                }
            }
        }

        stage('Deploy Filebeat') {
            steps {
                script {
                    sh 'kubectl apply -f logging/filebeat/filebeat-daemonset.yaml'
                }
            }
        }

        stage('Wait for Deployments') {
            steps {
                script {
                    sh '''
                        kubectl rollout status deployment/elasticsearch -n demo-basic
                        kubectl rollout status deployment/kibana -n demo-basic
                        kubectl rollout status daemonset/filebeat -n demo-basic
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'ELK Stack deployed successfully!'
        }
        failure {
            echo 'Failed to deploy ELK Stack'
        }
    }
}
