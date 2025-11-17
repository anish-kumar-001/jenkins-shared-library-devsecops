def call(Map config) {

    withCredentials([usernamePassword(
        credentialsId: 'repo-creds',
        usernameVariable: 'GIT_USER',
        passwordVariable: 'GIT_PASS'
    )]) {

        config.manifest_paths.each { filePath ->
            if (fileExists(filePath)) {
                echo "Updating image in ${filePath}"
                sh """
                    sed -i -E 's|(image:).*|\\1 ${config.image}:${config.tag}|' ${filePath}
                """
                sh "git add ${filePath}"
            }
        }

        sh '''
            git config user.email "anish@example.com"
            git config user.name "Anish CI"
        '''

        // commit only if there is a change
        sh "git commit -m 'ci: update image -> ${config.image}:${config.tag}' || true"

        // safe pull
        sh '''
            git pull --rebase origin main || true
        '''

        // SAFE push (no Groovy interpolation issues)
        sh '''
            git push https://${GIT_USER}:${GIT_PASS}@github.com/anish-kumar-001/complete-devsecops-ecosystem.git main
        '''
    }
}

