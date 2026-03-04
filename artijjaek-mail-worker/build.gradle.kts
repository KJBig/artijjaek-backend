plugins {
}

tasks.findByName("bootJar")?.enabled = false
tasks.findByName("jar")?.enabled = true

dependencies {
    implementation(project(":artijjaek-core"))
    implementation(project(":artijjaek-mq"))
    implementation("org.springframework:spring-tx")
    implementation("org.springframework.boot:spring-boot-starter-mail")
}
