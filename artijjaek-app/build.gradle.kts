plugins {
}

tasks.findByName("bootJar")?.enabled = true
tasks.findByName("jar")?.enabled = false

tasks.named<Test>("test") {
    dependsOn(
        ":artijjaek-core:test",
        ":artijjaek-mq:test",
        ":artijjaek-mail-worker:test",
        ":artijjaek-admin:test",
        ":artijjaek-api:test",
        ":artijjaek-batch:test"
    )
}

tasks.named("clean") {
    dependsOn(
        ":artijjaek-core:clean",
        ":artijjaek-mq:clean",
        ":artijjaek-mail-worker:clean",
        ":artijjaek-admin:clean",
        ":artijjaek-api:clean",
        ":artijjaek-batch:clean"
    )
}

dependencies {
    implementation(project(":artijjaek-admin"))
    implementation(project(":artijjaek-api"))
    implementation(project(":artijjaek-batch"))
    implementation(project(":artijjaek-mail-worker"))
}
