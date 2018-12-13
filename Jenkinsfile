pipeline {
    agent any
    tools {
        maven 'Apache Maven 3.5.2'
    }
    stages{
        stage('Checkout') {
            steps {
                git 'https://github.com/vyjorg/LPDM-Order'
            }
        }
        stage('Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml'
                }
                failure {
                    error 'The tests failed'
                }
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package -Dmaven.test.skip=true'
            }
        }
        stage('Deploy'){
            steps {
                sh 'docker stop LPDM-OrderMS || true && docker rm LPDM-OrderMS || true'
                sh 'docker-compose -f /var/lib/jenkins/workspace/Vyjorg_LPDM-Order_master/docker/dc-lpdm-order-ms.yml build --no-cache'
                step([$class: 'DockerComposeBuilder', dockerComposeFile: 'docker/dc-lpdm-order-ms.yml', option: [$class: 'StartAllServices'], useCustomDockerComposeFile: true])
            }
        }
    }
}