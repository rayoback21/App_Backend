package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.entity.Admins
import com.example.Aplicativo_web.service.AdminsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("admins")
@CrossOrigin(methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE])

class AdminsController {

    @Autowired
    lateinit var adminsService: AdminsService

    @GetMapping
    fun list(): List<Admins> {
        return adminsService.list()
    }

    @PostMapping
    fun save(@RequestBody admins: Admins): Admins {
        return adminsService.save(admins)
    }
    @PutMapping
    fun update(@RequestBody admins: Admins): ResponseEntity<Admins> {
        return ResponseEntity(adminsService.update(admins), HttpStatus.OK)
    }
    @PatchMapping
    fun updateName(@RequestBody admins: Admins): ResponseEntity<Admins> {
        return ResponseEntity(adminsService.updateName(admins), HttpStatus.OK)
    }
}