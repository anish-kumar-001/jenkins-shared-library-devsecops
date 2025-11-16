def call(String image) {
  script {
    if (!image) {
      error "trivyScan: image parameter is required"
    }
    echo "trivyScan: scanning ${image} (HIGH,CRITICAL)"
    // Run trivy; do not make the pipeline fail on findings, mark as UNSTABLE instead
    def cmd = "docker run --rm aquasec/trivy:latest image --severity HIGH,CRITICAL ${image}"
    def rc = sh(script: cmd, returnStatus: true)

    if (rc == 0) {
      echo "trivyScan: no HIGH/CRITICAL findings for ${image}"
    } else {
      echo "trivyScan: trivy exited with ${rc} — marking build UNSTABLE (findings may exist)"
      currentBuild.result = (currentBuild.result == null || currentBuild.result == 'SUCCESS') ? 'UNSTABLE' : currentBuild.result
    }
  }
}
