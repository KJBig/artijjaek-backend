plugins {
}

dependencies {
    implementation(project(":noati-core"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
}