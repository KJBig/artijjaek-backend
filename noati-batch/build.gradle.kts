plugins {
}

dependencies {
    implementation(project(":noati-core"))
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.jsoup:jsoup:1.17.2")
    testImplementation("org.springframework.batch:spring-batch-test")
}