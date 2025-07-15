import com.example.Aplicativo_web.dto.RacingRequestDTO
import com.example.Aplicativo_web.entity.Racing
import com.example.Aplicativo_web.service.RacingService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/racing")
class RacingController(private val racingService: RacingService) {

    // ✅ Solo super_admin puede ver todas las carreras
    @GetMapping
    @PreAuthorize("hasAuthority('super_admin')")
    fun getAll(): List<Racing> = racingService.findAll()

    // ✅ Solo super_admin puede ver una carrera específica
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin')")
    fun getById(@PathVariable id: Long): ResponseEntity<Racing> =
        racingService.findById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    // ✅ Solo super_admin puede crear carreras
    @PostMapping
    @PreAuthorize("hasAuthority('super_admin')")
    fun create(@RequestBody request: RacingRequestDTO): ResponseEntity<Racing> {
        val created = racingService.createRacing(request)
        return ResponseEntity.ok(created)
    }

    // ✅ Solo super_admin puede actualizar carreras
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin')")
    fun update(@PathVariable id: Long, @RequestBody racing: Racing): ResponseEntity<Racing> {
        val existing = racingService.findById(id) ?: return ResponseEntity.notFound().build()
        existing.career = racing.career
        existing.aspirants = racing.aspirants
        existing.professor = racing.professor
        return ResponseEntity.ok(racingService.save(existing))
    }

    // ✅ Solo super_admin puede eliminar carreras
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('super_admin')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val existing = racingService.findById(id) ?: return ResponseEntity.notFound().build()
        racingService.deleteById(existing.id!!)
        return ResponseEntity.noContent().build()
    }
}
