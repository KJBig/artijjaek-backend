plugins {
}

tasks.findByName("bootJar")?.enabled = false
tasks.findByName("jar")?.enabled = true

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("com.h2database:h2")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}