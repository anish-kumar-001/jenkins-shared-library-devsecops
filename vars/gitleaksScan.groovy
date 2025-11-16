def call(){
  sh 'docker run --rm -v $(pwd):/repo zricethezav/gitleaks:latest detect --source=/repo --no-banner --config=/repo/.gitleaks.toml || true'
}