def call(String image){
  sh "docker run --rm aquasec/trivy:latest image --severity HIGH,CRITICAL ${image} || true"
}