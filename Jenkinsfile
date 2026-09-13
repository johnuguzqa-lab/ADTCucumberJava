// ==========================================================================
// Jenkins Pipeline (declarative) sample for the Cucumber BDD framework.
//
// Prerequisites on the Jenkins agent:
//   * JDK 21 LTS (configured as a global tool named 'jdk21'; any JDK 21+ also works)
//   * Maven 3.8+ (configured as a global tool named 'maven-3')
//   * Chrome and/or Firefox installed (Selenium Manager resolves the drivers)
//
// Run headless so it works on build agents without a display.
// ==========================================================================
pipeline {
    agent any

    tools {
        maven 'maven-3'
        jdk 'jdk21'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run BDD tests') {
            steps {
                // -Dheadless=true keeps the suite CI-friendly (no display needed).
                sh 'mvn clean test -Dheadless=true'
            }
        }
    }

    post {
        always {
            // Publish JUnit-style results to the build.
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true

            // Archive the Cucumber HTML/JSON reports for the build page.
            archiveArtifacts artifacts: 'target/cucumber.json, target/cucumber-report.html, target/cucumber-timeline-report/**', allowEmptyArchive: true

            // Attach automation logs as a build artifact for diagnosis.
            archiveArtifacts artifacts: 'target/test-logs/**', allowEmptyArchive: true
        }
    }
}
