def call(){
  sh 'docker run --rm -v $(pwd):/src returntocorp/semgrep semgrep --config=auto /src || true'
}