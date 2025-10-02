plugins {
}

tasks.findByName("bootJar")?.enabled = true
tasks.findByName("jar")?.enabled = false

dependencies {
    implementation(project(":artijjaek-admin"))
    implementation(project(":artijjaek-api"))
    implementation(project(":artijjaek-batch"))
}