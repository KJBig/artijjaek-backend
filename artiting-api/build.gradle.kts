plugins {
}

tasks.findByName("bootJar")?.enabled = true
tasks.findByName("jar")?.enabled = false

dependencies {
    implementation(project(":artiting-core"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}