def call(Map config) {

    def manifestPaths = config.manifest_paths
    def imageName = config.image
    def imageTag = config.tag

    echo "Updating K8s manifests with image: ${imageName}:${imageTag}"

    manifestPaths.each { file ->
        echo "Processing: ${file}"
        if (fileExists(file)) {

            // This sed works on Ubuntu & Alpine both
            sh """
                sed -i -E 's|(image:).*|\\1 ${imageName}:${imageTag}|' ${file}
            """

            echo "Updated image in ${file}"
        } else {
            error "Manifest file not found: ${file}"
        }
    }

    // Push updated YAMLs back to GitHub
    withCredentials([string(credentialsId: 'github-token', variable: 'GIT_PASS')]) {

        sh """
            echo "Cleaning workspace to avoid conflicts..."
            git reset --hard

            echo "Pulling latest from main..."
            git pull origin main || true

            echo "Adding updated files..."
            git add k8s/

            echo "Committing changes..."
            git commit -m "ci: update image to ${imageName}:${imageTag}" || echo "No changes to commit"

            echo "Pushing changes to GitHub..."
            git push https://anish-kumar-001:${GIT_PASS}@github.com/anish-kumar-001/complete-devsecops-ecosystem.git main
        """
    }

    echo "Update complete."
}
