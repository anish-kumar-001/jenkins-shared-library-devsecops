def call(Map args = [:]) {
  script {
    def tag = args.tag ?: 'latest'
    def image = args.image
    if (!image) {
      error "updateK8sManifest: 'image' parameter is required"
    }
    // allow both single path and list
    def paths = args.manifest_paths ?: (args.manifest_path ? [args.manifest_path] : [])
    if (!paths || paths.size() == 0) {
      error "updateK8sManifest: no manifest paths supplied"
    }

    echo "updateK8sManifest: updating ${paths} to use ${image}:${tag}"

    // Replace only the image line (works for 'image: repo/name:tag' style)
    for (p in paths) {
      // Keep a backup before replacing (helpful for debugging)
      sh(script: "cp ${p} ${p}.bak || true", returnStatus: true)
      def sedCmd = "sed -i -E 's|^(\\s*image:\\s*).*$|\\1${image}:${tag}|' ${p}"
      def rc = sh(script: sedCmd, returnStatus: true)
      if (rc != 0) {
        echo "updateK8sManifest: warning: sed returned ${rc} for ${p}"
      } else {
        echo "updateK8sManifest: updated ${p}"
      }

      // add & commit changes (do not fail pipeline if commit fails)
      sh(script: "git add ${p} || true; git commit -m 'ci: update image in ${p} to ${image}:${tag}' || true", returnStatus: true)
    }

    // push using provided repo creds; do not fail pipeline if push fails
    if (args.pushRepo == true) {
      withCredentials([usernamePassword(credentialsId: args.repoCredsId ?: 'repo-creds', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
        def repoUrl = args.repoUrl ?: ''
        if (!repoUrl?.trim()) {
          echo "updateK8sManifest: no repoUrl provided, skipping push"
        } else {
          def pushCmd = "git push https://${GIT_USER}:${GIT_PASS}@${repoUrl.replaceFirst('https?://','') } HEAD:main"
          def rc2 = sh(script: pushCmd, returnStatus: true)
          if (rc2 != 0) {
            echo "updateK8sManifest: git push returned ${rc2} (non-fatal)"
            currentBuild.result = (currentBuild.result == null || currentBuild.result == 'SUCCESS') ? 'UNSTABLE' : currentBuild.result
          } else {
            echo "updateK8sManifest: pushed changes to ${repoUrl}"
          }
        }
      }
    } else {
      echo "updateK8sManifest: pushRepo not true; skipping git push"
    }
  }
}
