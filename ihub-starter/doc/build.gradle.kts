description = "文档模块组件自动配置"

iHubJava {
    registerFeature("servlet", "web-support", "servlet-support")
    registerFeature("reactor", "web-support", "reactor-support")
}

dependencies {
    implementation(project(":ihub-process"))
    annotationProcessor(project(":ihub-process"))

    implementation("org.springdoc:springdoc-openapi-starter-common")
    "servletImplementation"("org.springdoc:springdoc-openapi-starter-webmvc-api")
    "reactorImplementation"("org.springdoc:springdoc-openapi-starter-webflux-api")

    testImplementation(project(":ihub-boot-test-spring-boot-starter"))
    testImplementation("org.springframework.boot:spring-boot-starter-web")
}
