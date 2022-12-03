#!/usr/bin/env groovy

String incrementVersion() {
    echo "Incrementing the version..."
    sh 'mvn build-helper:parse-version versions:set \
        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
        versions:commit'
     def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
     def version = matcher[1][1]
     return "$version-$BUILD_NUMBER"
}

def buildJar() {
    echo "Building the application..."
    sh "mvn clean package -DskipTests"
}

def buildDockerImage(String imageName) {
    echo "Building Docker Iamge..."
    sh "docker build -t $imageName ."
}

def dockerLogin() {
    echo "Login to the docker..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        sh "echo $PASSWORD | docker login -u $USERNAME --password-stdin"
    }
}

def dockerPush(String imageName) {
    echo "Pushing the docker image to docker repository..."
    sh "docker push $imageName"
}

def deployApplication(String imageName) {
    echo "Deploying the appication to EC2..."
    def shellCmd = "bash ./server-script-back.sh $imageName"
    sshagent(['ec2-user-ssh']) {
        sh "scp ./server-script-back.sh ec2-user@15.188.59.125:/home/ec2-user"
        sh "scp ./docker-compose-spring.yaml ec2-user@15.188.59.125:/home/ec2-user"
        sh "ssh -o StrictHostKeyChecking=no ec2-user@15.188.59.125 ${shellCmd}"
    }
}

return this