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

// def deployApplication(String imageName) {
//     echo "Deploying the application to EC2..."
//     def shellCmd = "bash ./server-script-back.sh $imageName"
//      withCredentials([usernamePassword(credentialsId: 'azure-vm', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
//         sh "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ./server-script-back.sh $USERNAME@20.166.72.53:/home/$USERNAME"
//         sh "scp -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ./docker-compose-spring.yaml $USERNAME@20.166.72.53:/home/$USERNAME"
//         sh "echo $PASSWORD | sshpass --password-stdin ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $USERNAME@20.166.72.53 ${shellCmd}"
// }

// }

def deployApplication(String imageName) {
    echo "Deploying the appication to EC2..."
    def shellCmd = "bash ./server-script-back.sh $imageName"
    sshagent(['jenkisn-ssh']) {
        sh "scp ./server-script-back.sh azureuser@20.166.72.53:/home/ec2-user"
        sh "scp ./docker-compose-spring.yaml azureuser@20.166.72.53:/home/ec2-user"
        sh "ssh -o StrictHostKeyChecking=no azureuser@20.166.72.53 ${shellCmd}"
    }
}

return this
