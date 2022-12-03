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
        stage("git commit") {
                withCredentials([usernamePassword(credentialsId: 'github-credentials', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                                        sh 'git config --global user.email "jenkins@gmail.com"'
                                        sh 'git config --global user.name "jenkins"'
                                        sh 'git config --list'
                                        sh "git remote set-url origin https://${USER}:${PASS}@github.com/Hichamrex/spring-back-end.git"
                                        sh 'git add .'
                                        sh 'git commit -m "CI: version bump"'
                                        sh 'git push origin HEAD:main'
                }
        }
    }

}