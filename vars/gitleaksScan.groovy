def call(Map args = [:]) {
  script {
    def repoPath = args.path ?: env.WORKSPACE ?: '.'
    def configPath = args.config ?: "${repoPath}/.gitleaks.toml"
    echo "gitleaksScan: scanning ${repoPath} using config ${configPath}"

    def cmd = "docker run --rm -v ${repoPath}:/repo zricethezav/gitleaks:latest detect --source=/repo --no-banner --config=${configPath}"
    def rc = sh(script: cmd, returnStatus: true)
    if (rc == 0) {
      echo "gitleaksScan: no leaks found"
    } else {
      echo "gitleaksScan: gitleaks exited with ${rc} — marking build UNSTABLE (possible secrets)"
      currentBuild.result = (currentBuild.result == null || currentBuild.result == 'SUCCESS') ? 'UNSTABLE' : currentBuild.result
    }
  }
}
