// Pipeline del microservicio Ejercicio5MyAPIGestor (API REST, puerto 8081)
// Requisitos en Jenkins (Administrar Jenkins > Tools):
//   - JDK   con nombre "jdk21"  -> apuntando a un JDK 21
//   - Maven con nombre "maven3" -> version 3.9.x
// En el job: Pipeline script from SCM, Script Path = Ejercicio5MyAPIGestor/Jenkinsfile
pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
    }

    stages {
        stage('Verificar versiones') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'java -version'
                        sh 'mvn -v'
                    } else {
                        bat 'java -version'
                        bat 'mvn -v'
                    }
                }
            }
        }

        stage('Compilar y empaquetar') {
            steps {
                dir('Ejercicio5MyAPIGestor') {
                    script {
                        // -DskipTests: la unica prueba (contextLoads) necesita MariaDB con la base dbgestor encendida
                        if (isUnix()) {
                            sh 'mvn -B clean package -DskipTests'
                        } else {
                            bat 'mvn -B clean package -DskipTests'
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'Ejercicio5MyAPIGestor/target/*.jar', fingerprint: true
        }
    }
}
