plugins {
    kotlin("plugin.jpa") version "1.9.25"
}

dependencies {
    implementation(project(":artiting-core"))
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.batch:spring-batch-test")

    //  크롤링
    implementation("org.jsoup:jsoup:1.17.2")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

}