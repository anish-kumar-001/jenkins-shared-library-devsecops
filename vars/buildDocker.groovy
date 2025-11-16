def call(Map args){
  def ctx = args.context ?: '.'
  def image = args.image
  def tag = args.tag ?: 'latest'
  sh "docker build -t ${image}:${tag} ${ctx}"
}