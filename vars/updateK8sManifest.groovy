def call(Map args){
  def tag = args.tag ?: 'latest'
  def image = args.image
  def paths = args.manifest_paths ?: (args.manifest_path ? [args.manifest_path] : [])
  for (p in paths){
    sh "sed -i 's|image:.*|image: ${image}:${tag}|' ${p} || true"
    sh "git add ${p} || true; git commit -m 'ci: update image in ${p} to ${image}:${tag}' || true"
  }
  withCredentials([usernamePassword(credentialsId: 'repo-creds', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]){
    sh "git push https://${GIT_USER}:${GIT_PASS}@github.com/anish-kumar-001/complete-devsecops-ecosystem.git HEAD:main || true"
  }
}