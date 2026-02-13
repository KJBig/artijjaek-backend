plugins {
}

tasks.findByName("bootJar")?.enabled = true
tasks.findByName("jar")?.enabled = false

tasks.named<Test>("test") {
    dependsOn(
        ":artijjaek-core:test",
        ":artijjaek-admin:test",
        ":artijjaek-api:test",
        ":artijjaek-batch:test"
    )
}

dependencies {
    implementation(project(":artijjaek-admin"))
    implementation(project(":artijjaek-api"))
    implementation(project(":artijjaek-batch"))
}
