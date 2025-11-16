def call(Map args) {

    def tag = args.tag ?: 'latest'
    def image = args.image
    def paths = args.manifest_paths ?: []

    echo "Updating K8s manifests with image: ${image}:${tag}"

    for (file in paths) {

        echo "Processing: ${file}"

        sh """
            if [ -f ${file} ]; then
              echo "Updating image in ${file}"
              sed -i -E "s|^(\\s*image:\\s*).*\$|\\1${image}:${tag}|" ${file}
            else
              echo "Manifest not found: ${file}"
              exit 1
            fi
        """

        sh """
            git add ${file}
            git commit -m "ci: update image to ${image}:${tag} in ${file}" || true
        """
    }

    withCredentials([usernamePassword(
        credentialsId: 'repo-creds',
        usernameVariable: 'GIT_USER',
        passwordVariable: 'GIT_PASS'
    )]) {
        sh """
            git pull --rebase
            git push https://${GIT_USER}:${GIT_PASS}@github.com/anish-kumar-001/complete-devsecops-ecosystem.git HEAD:main
        """
    }
}
