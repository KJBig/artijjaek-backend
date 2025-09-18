plugins {
}

tasks.findByName("bootJar")?.enabled = true
tasks.findByName("jar")?.enabled = false

dependencies {
    implementation(project(":artiting-admin"))
    implementation(project(":artiting-api"))
    implementation(project(":artiting-batch"))
}