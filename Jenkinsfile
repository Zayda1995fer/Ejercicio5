// PRACTICA 3 - Pipeline BACKEND: microservicio Ejercicio5MyAPIGestor (API REST, puerto 8081)
// Repositorio: https://github.com/Zayda1995fer/Ejercicio5   Script Path: Jenkinsfile
// Requisitos en Jenkins (Administrar Jenkins > Tools):
//   - JDK   con nombre "jdk21"  -> apuntando a un JDK 21
//   - Maven con nombre "maven3" -> version 3.9.x
//   - (Analisis Sonar)  credencial "Secret text" con ID "sonar-token" y SonarQube en SONAR_HOST_URL
//   - (Escaneo Trivy)   Docker Desktop encendido en el agente
// Las pruebas usan H2 en memoria (src/test/resources): NO se necesita MariaDB encendida.
def run(String cmd) {
    if (isUnix()) { sh cmd } else { bat cmd }
}

// Trivy en Docker sobre la carpeta del proyecto; --skip-dirs target excluye la compilacion (src/ y pom.xml SI se escanean)
def trivy(String opciones) {
    String ws = isUnix() ? '$WORKSPACE' : '%WORKSPACE%'
    // Se monta el repositorio local de Maven para que Trivy resuelva tambien las dependencias transitivas (Tomcat, Jackson...)
    String m2 = isUnix() ? '$HOME/.m2' : '%USERPROFILE%\\.m2'
    run "docker run --rm -v \"${ws}\":/src -v \"${m2}\":/root/.m2 -v trivy-cache:/root/.cache aquasec/trivy:0.58.1 fs --no-progress --skip-dirs target ${opciones} /src"
}

pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
    }

    parameters {
        booleanParam(name: 'ANALISIS_SONAR', defaultValue: true, description: 'Ejecutar analisis de calidad con SonarQube')
        string(name: 'SONAR_HOST_URL', defaultValue: 'http://localhost:9000', description: 'URL de SonarQube')
        booleanParam(name: 'ESCANEO_TRIVY', defaultValue: true, description: 'Escanear vulnerabilidades con Trivy (Docker)')
    }

    stages {
        stage('Verificar versiones') {
            steps {
                run 'java -version'
                run 'mvn -v'
            }
        }

        stage('Compilar') {
            steps {
                run 'mvn -B clean compile'
            }
        }

        stage('Pruebas y cobertura') {
            steps {
                // verify = Surefire (pruebas) + JaCoCo report + JaCoCo check (100% lineas y ramas)
                run 'mvn -B verify'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        stage('Analisis de codigo (SonarQube)') {
            when { expression { params.ANALISIS_SONAR } }
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    // sonar.exclusions: no se analiza target/ (codigo generado); el codigo fuente si se analiza
                    run "mvn -B sonar:sonar -Dsonar.host.url=${params.SONAR_HOST_URL} -Dsonar.projectKey=Ejercicio5MyAPIGestor -Dsonar.exclusions=**/target/** -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml -Dsonar.qualitygate.wait=true"
                }
            }
        }

        stage('Escaneo de vulnerabilidades (Trivy)') {
            when { expression { params.ESCANEO_TRIVY } }
            steps {
                trivy '--scanners vuln,secret,misconfig --format table --output /src/trivy-backend.txt'
                // Falla el pipeline solo si hay vulnerabilidades CRITICAS sin atender
                trivy '--scanners vuln --severity CRITICAL --exit-code 1'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'trivy-backend.txt', allowEmptyArchive: true
                }
            }
        }

        stage('Empaquetar') {
            steps {
                run 'mvn -B package -DskipTests -Djacoco.skip=true'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}
