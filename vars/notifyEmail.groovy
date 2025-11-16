def call(String status){
  emailext(subject: "CI Pipeline: ${status}", body: "Pipeline ${status}", to: "choudharyanish078@gmail.com")
}