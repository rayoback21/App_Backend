package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.Roles
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional // Importar Optional

interface RolesRepository : JpaRepository<Roles, Long> {
        fun findByRoles(roleName: String): List<Roles>
}