pipeline{
    agent any
    
    stages{
        stage("git.pull"){
            steps{
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install git -y '
                git credentialsId: 'new-key', url: 'git@github.com:VrushabhDhomne/student-ui.git'
                sh 'ls'
            }
        }
        stage("Maven.build"){
            steps{
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install maven -y'
                sh 'mvn clean package'
            }
        }
        stage("Push.Artifact"){
            steps{
                sh 'curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"'
                sh 'unzip awscliv2.zip'
                sh 'sudo ./aws/install'
                sh 'aws s3 cp test.war s3://dev-artifact/test2.war'
            }
        }
        stage("Dev.Deployment"){
            steps{
                sh 'ssh -i jenkins.pem ec2-user@0.0.0.0'
                sh 'curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"'
                sh 'unzip awscliv2.zip'
                sh 'sudo ./aws/install'
                sh 'aws s3 cp s3://dev-artifact/test2.war /opt/tomcat/webapps'
                sh './opt/tomcat/bin/startup.sh'
            }
        }
    }

}
