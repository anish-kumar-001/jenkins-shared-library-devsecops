def call(Map args) {

    def image = args.image
    def tag = args.tag ?: "latest"
    def files = args.manifest_paths ?: []

    echo "Updating K8s manifests: ${files} -> ${image}:${tag}"

    files.each { file ->

        echo "Updating ${file}"

        sh """
            if [ ! -f "${file}" ]; then
              echo "ERROR: File ${file} not found!"
              exit 1
            fi
        """

        sh """
            sed -i -E "s#(image:).*#\\1 ${image}:${tag}#" ${file}
        """
    }

    sh """
        git config user.email "jenkins@local"
        git config user.name "Jenkins CI"

        git add ${files.join(' ')} || true
        git commit -m "ci: update image -> ${image}:${tag}" || true
    """

    withCredentials([usernamePassword(
        credentialsId: 'repo-creds',
        usernameVariable: 'GIT_USER',
        passwordVariable: 'GIT_PASS'
    )]) {
        sh """
            git pull --no-rebase || true
            git push https://${GIT_USER}:${GIT_PASS}@github.com/anish-kumar-001/complete-devsecops-ecosystem.git HEAD:main
        """
    }
}

