def call(String status) {
    emailext(
        to: "choudharyanish078@gmail.com",
        subject: "CI Pipeline: ${status}",
        body: "Pipeline ${status}"
    )
}
