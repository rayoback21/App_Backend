package com.example.Aplicativo_web.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths // Asegúrate de importar Paths

@Configuration // Esta es la única clase @Configuration que debe manejar addResourceHandlers
class WebConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Esta es la ruta correcta y más robusta para servir archivos desde el sistema de archivos.
        // "file:./uploads/photos/" significa que Spring buscará la carpeta 'uploads/photos'
        // en el directorio donde se está ejecutando tu aplicación Spring Boot.
        registry.addResourceHandler("/uploads/photos/**")
            .addResourceLocations("file:./uploads/photos/")
        // Si en un futuro necesitas una ruta absoluta (ej. en producción), podrías usar:
        // .addResourceLocations("file:/ruta/absoluta/a/tus/fotos/")
    }
}
    