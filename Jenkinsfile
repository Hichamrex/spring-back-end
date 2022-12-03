def grScript
def imageName = 'hichamrex/apps-repo:spring-'
pipeline {
    agent any
    tools {
        maven 'maven-3.8'
    }
    stages {
        stage("initialisation") {
            steps {
                script {
                    echo "Loading the script..."
                    grScript = load 'script.groovy'
                }
            }
        }
        stage("increment version") {
            steps {
                script {
                    imageName += grScript.incrementVersion()
                }
            }
        }
        stage("build Jar") {
            steps {
                script {
                    grScript.buildJar()
                }
            }
        }
        stage("build docker image") {
            steps {
                script {
                    grScript.buildDockerImage(imageName)
                    grScript.dockerLogin()
                    grScript.dockerPush(imageName)
                }
            }
        }
        stage("Deploying the application...") {
            steps {
                script {
                    grScript.deployApplication(imageName)
                }
            }
        }

    }

}