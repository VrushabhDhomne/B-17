pipeline{
    agent{
        label "ecs-agent-worker"
    }
    stages{
        stage("git.pull"){
            steps{
                echo "git pull from branch"
                git branch: 'main', credentialsId: 'sshkey-worker', url: 'git@github.com:VrushabhDhomne/Devops.git'
                sh 'ls'
            }
        }
        stage("App.build"){
            steps{
                echo "App build from maven"
            }
        }
        stage("App.start"){
            steps{
                echo "git pull from branch"
            }
        }
    }

}
