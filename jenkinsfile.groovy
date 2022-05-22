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
                sh 'sudo mv /var/lib/jenkins/workspace/studentapp/target/studentapp-2.2-SNAPSHOT.war /home/ubuntu/studentapp-${BUILD_ID}.war'
                sh 'aws s3 cp /home/ubuntu/studentapp-${BUILD_ID}.war s3://dev-artifact-01'
            }
        }
        stage("Dev.Deployment"){
            steps{
                withCredentials([sshUserPrivateKey(credentialsId:'tomcat', keyFileVariable:'tomcat')]){
                sh 'ssh -i ${tomcat} -o StrictHostKeyChecking=no ec2-user@54.242.62.193'
                sh 'curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"'
                sh 'unzip awscliv2.zip'
                sh 'sudo ./aws/install'
                sh 'aws s3 cp s3://dev-artifact/test2.war /opt/tomcat/webapps'
                sh './opt/tomcat/bin/startup.sh'
                }

            }
        }
    }

}
