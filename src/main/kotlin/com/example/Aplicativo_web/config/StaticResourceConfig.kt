/*
package com.example.Aplicativo_web.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Path
import java.nio.file.Paths

@Configuration
class StaticResourceConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val uploadDir: Path = Paths.get("uploads/photos")
        val uploadPath = uploadDir.toFile().absolutePath

        registry.addResourceHandler("/uploads/photos/**")
            .addResourceLocations("file:$uploadPath/")
    }
}
*/