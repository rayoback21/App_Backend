package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.entity.Admins
import com.example.Aplicativo_web.repository.AdminsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AdminsService {

    @Autowired
    lateinit var adminsRepository: AdminsRepository

    fun list(): List<Admins> {
        return adminsRepository.findAll()
    }

    fun save(admins: Admins):Admins {
        return adminsRepository.save(admins)

    }

    fun update(admins: Admins):Admins {
        try {


        adminsRepository.findById(admins.id) ?: throw Exception("Id Existe")
        return adminsRepository.save(admins)
    }
        catch (ex:Exception){
            throw ResponseStatusException(HttpStatus.NOT_FOUND,ex.message)
        }
    }
    fun updateName(admins: Admins):Admins {
        try {

           val response  = adminsRepository.findById(admins.id) ?: throw Exception("Id Existe")
            response.apply {
                username= admins.username
                password= admins.password
                email = admins.email
            }
            return adminsRepository.save(response)
        }
        catch (ex:Exception){
            throw ResponseStatusException(HttpStatus.NOT_FOUND,ex.message)
        }
    }

}