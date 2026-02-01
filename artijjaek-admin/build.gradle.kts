plugins {
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework.security:spring-security-crypto")

    implementation(project(":artijjaek-core"))
}
