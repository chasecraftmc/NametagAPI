  
pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean install'
      }
    }
    stage('archive') {
      steps {
        archiveArtifacts(artifacts: 'target/NametagAPI.jar', allowEmptyArchive: true, onlyIfSuccessful: true, fingerprint: true)
      }
    }
  }

  tools {
    maven 'M3'
    jdk 'JDK 14'
  }

  triggers {
    pollSCM('H/1 * * * *')
  }
}