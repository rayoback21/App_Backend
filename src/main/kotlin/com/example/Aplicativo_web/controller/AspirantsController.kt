package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.entity.Aspirants
import com.example.Aplicativo_web.service.ApirantsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/aspirants")
@CrossOrigin(methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE])
class AspirantsController {

    @Autowired
    lateinit var aspirantsService: ApirantsService


    // Este endpoint obtiene la lista de estudiantes
    @GetMapping
    fun list(): List<Aspirants> {
        return aspirantsService.list()
    }

    // Este endpoint obtiene un estudiante por su ID
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<out Any> {
        val aspirants = aspirantsService.findById(id)
        return if (aspirants != null) {
            ResponseEntity.ok(aspirants)
        } else {
            ResponseEntity("Estudiante no encontrado", HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/nui/{nui}")
    fun getByNui(@PathVariable nui: String): ResponseEntity<out Any> {
        val student = aspirantsService.findByNui(nui)
        return if (student != null) {
            ResponseEntity.ok(student)
        } else {
            ResponseEntity("Estudiante no encontrado", HttpStatus.NOT_FOUND)
        }
    }

    // Endpoint para guardar un nuevo estudiante
    @PostMapping
    fun save(@RequestBody aspirants: Aspirants): Aspirants {
        return aspirantsService.save(aspirants)
    }

    // Endpoint para actualizar un estudiante por ID
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long?, @RequestBody aspirants: Aspirants?): ResponseEntity<Aspirants> {
        val updatedStudents = aspirantsService.update(aspirants!!)
        return ResponseEntity.ok(updatedStudents)
    }

    // Endpoint para actualizar parcialmente un estudiante
    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long?, @RequestBody aspirants: Aspirants?): ResponseEntity<Aspirants> {
        val updatedAspirants = aspirantsService.updateName(aspirants!!)
        return ResponseEntity.ok(updatedAspirants)
    }

    // Endpoint para eliminar un estudiante por ID
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<String> {
        aspirantsService.delete(id)
        return ResponseEntity("Estudiante Eliminado", HttpStatus.OK)
    }


}





