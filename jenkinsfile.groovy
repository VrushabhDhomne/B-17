pipeline{
    agent any
    
    stages{
        stage("git.pull"){
            steps{
                sh 'sudo apt-get update -y'
                sh 'sudo apt-get install git -y '
                git credentialsId: 'Git', url: 'git@github.com:VrushabhDhomne/student-ui.git'
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
                sh 'sudo mv /var/lib/jenkins/workspace/ecs-pipeline/studentapp/target/studentapp-2.2-SNAPSHOT.war /home/ubuntu/studentapp-${BUILD_ID}.war'
                sh 'aws s3 cp /home/ubuntu/studentapp-${BUILD_ID}.war s3://dev-artifact-01'
            }
        }
        stage("Dev.Deployment"){
            steps{
                withCredentials([sshUserPrivateKey(credentialsId:'tomcat', keyFileVariable:'tomcat')]){
                sh '''
                ssh -i ${tomcat} -o StrictHostKeyChecking=no ec2-user@54.242.62.193<<EOF
                sudo aws s3 cp s3://dev-artifact-01/studentapp-${BUILD_ID}.war .
                curl -O https://dlcdn.apache.org/tomcat/tomcat-8/v8.5.78/bin/apache-tomcat-8.5.78.tar.gz
                sudo tar -xvf apache-tomcat-8.5.78.tar.gz -C  /opt/
                sudo sh /opt/apache-tomcat-8.5.78/bin/shutdown.sh
                sudo cp -rv studentapp-${BUILD_ID}.war studentapp.war
                sudo cp -rv studentapp.war /opt/apache-tomcat-8.5.78/webapps/
                sudo sh /opt/apache-tomcat-8.5.78/bin/startup.sh
                '''
                }

            }
        }
    }

}
