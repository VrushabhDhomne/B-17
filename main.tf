provider "aws" {
  region     = "us-east-1"
  access_key = "AKIAUQV4W2YK2GNAT3NE"
  secret_key = "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"
}

#-----Ec2 Instance------

#-----Ec2 Instance------

data "aws_ami" "jenkins" {
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical
}

resource "aws_instance" "web" {
  ami           = data.aws_ami.jenkins.id
  instance_type = "t2.micro"
  key_name = "jenkins"

  user_data = <<EOF
#!/bin/bash
sudo apt update
sudo apt install openjdk-8-jdk -y
sudo apt install maven git wget unzip -y
wget -q -O - https://pkg.jenkins.io/debian-stable/jenkins.io.key | sudo apt-key add -
sudo sh -c 'echo deb https://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
sudo apt-get update
sudo apt-get install jenkins -y
sudo systemctl start jenkins
sudo systemctl enable jenkins
EOF
  tags = {
    Name = "jenkins"
  }
}