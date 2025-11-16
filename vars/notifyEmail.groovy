def call(String status, Map args = [:]) {
  script {
    def toAddr = args.to ?: 'choudharyanish078@gmail.com'
    def subj = args.subject ?: "CI Pipeline: ${status}"
    def body = args.body ?: "Pipeline ${status}"
    echo "notifyEmail: sending ${status} notification to ${toAddr}"
    emailext(subject: subj, body: body, to: toAddr)
  }
}
