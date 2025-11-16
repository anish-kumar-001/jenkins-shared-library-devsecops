def call(Map args = [:]) {
  script {
    // Allow optional path override, default to workspace root
    def path = args.path ?: env.WORKSPACE ?: '.'
    echo "semgrepScan: scanning ${path}"
    // use env.WORKSPACE instead of $(pwd) for reliability across agents
    def cmd = "docker run --rm -v ${path}:/src returntocorp/semgrep semgrep --config=auto /src"
    def rc = sh(script: cmd, returnStatus: true)
    if (rc == 0) {
      echo "semgrepScan: no issues found"
    } else {
      echo "semgrepScan: semgrep exited with ${rc} — marking build UNSTABLE (findings may exist)"
      currentBuild.result = (currentBuild.result == null || currentBuild.result == 'SUCCESS') ? 'UNSTABLE' : currentBuild.result
    }
  }
}
