def call(Map args = [:]) {
  // Build a docker image from given context, image, tag
  script {
    def ctx = args.context ?: '.'
    def image = args.image
    def tag = args.tag ?: 'latest'

    if (!image) {
      error "buildDocker: required parameter 'image' is missing"
    }

    echo "buildDocker: building ${image}:${tag} from context ${ctx}"
    def status = sh(script: "docker build -t ${image}:${tag} ${ctx}", returnStatus: true)
    if (status != 0) {
      error "buildDocker: docker build failed with exit code ${status}"
    }
    echo "buildDocker: successfully built ${image}:${tag}"
  }
}
