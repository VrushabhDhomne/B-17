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
                sh 'sudo mv /var/lib/jenkins/workspace/ecs-pipeline/target/studentapp-2.2-SNAPSHOT.war /home/ubuntu/studentapp.war'
                //sh 'aws s3 cp /home/ubuntu/studentapp.war s3://dev-artifact-01'
            }
        }
        stage("Dev.Deployment"){
            steps{
                withCredentials([sshUserPrivateKey(credentialsId: 'sam', keyFileVariable: 'sam', usernameVariable: 'apple')]) {
                sh '''
                ssh -i ${sam} -o StrictHostKeyChecking=no ubuntu@34.239.248.122<<EOF
                sudo aws s3 cp s3://dev-artifact-01/studentapp13.war .
                wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.0.8/bin/apache-tomcat-10.0.8.tar.gz
                sudo tar xzvf apache-tomcat-10.0.8.tar.gz  /opt/
                sudo sh /opt/apache-tomcat-8.5.78/bin/shutdown.sh
                sudo cp -rv studentapp13.war studentapp.war
                sudo cp -rv studentapp.war /opt/apache-tomcat-8.5.78/webapps/
                sudo sh /opt/apache-tomcat-8.5.78/bin/startup.sh
                '''
                }

            }
        }
    }

}
