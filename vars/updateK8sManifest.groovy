def call(String image) {

    echo "Updating K8s manifests with image: ${image}"

    def filePath = "k8s/frontend-deployment.yaml"

    echo "Processing: ${filePath}"

    if (!fileExists(filePath)) {
        error "File NOT found: ${filePath}"
    }

    // Update image line safely
    sh """
    sed -i -E 's|(image:).*|\\1 ${image}|g' ${filePath}
    """

    echo "Updated image in ${filePath}"

    // Configure git identity
    sh """
    git config user.email "jenkins@ci.com"
    git config user.name "Jenkins CI"
    """

    // Add & commit changes
    sh """
    git add ${filePath}
    git commit -m "ci: update image to ${image} in ${filePath}"
    """

    // Push directly (NO REBASE)
    withCredentials([usernamePassword(credentialsId: 'repo-creds', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
        sh """
        git push https://${GIT_USER}:${GIT_PASS}@github.com/anish-kumar-001/complete-devsecops-ecosystem.git HEAD:main
        """
    }

    echo "Manifest update + push completed successfully."
}
