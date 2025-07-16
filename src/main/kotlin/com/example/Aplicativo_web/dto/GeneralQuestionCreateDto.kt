package com.example.Aplicativo_web.dto

import com.example.Aplicativo_web.entity.Questions
// No necesitas importar UsersEntity o Racing aquí si solo mapeas a Questions
// import com.example.Aplicativo_web.entity.UsersEntity
// import com.example.Aplicativo_web.entity.Racing


// DTO para crear preguntas generales
data class GeneralQuestionCreateDto(
    val text: String, // No nullable, ya que tu frontend lo pide.
    val optionA: String, // No nullable.
    val optionB: String, // No nullable.
    val optionC: String?, // Puede ser nulo para 'true-false'
    val optionD: String?, // Puede ser nulo para 'true-false'
    val correctOption: String, // No nullable, como string 'A', 'B', 'C', 'D'
    val points: Int, // No nullable.
    val questionType: String // "multiple-choice" o "true-false"
    // NO INCLUIMOS professor_id ni racing_id aquí, ya que no son parte de la creación de una pregunta general
) {
    // Función de mapeo de DTO a Entidad Questions
    fun toEntity(): Questions {
        return Questions().apply {
            this.text = this@GeneralQuestionCreateDto.text
            this.optionA = this@GeneralQuestionCreateDto.optionA
            this.optionB = this@GeneralQuestionCreateDto.optionB
            this.optionC = this@GeneralQuestionCreateDto.optionC
            this.optionD = this@GeneralQuestionCreateDto.optionD
            // ¡CAMBIO AQUÍ! Ya no necesitamos .singleOrNull() porque correctOption
            // en la entidad Questions ahora es String? y el DTO lo recibe como String.
            this.correctOption = this@GeneralQuestionCreateDto.correctOption
            this.points = this@GeneralQuestionCreateDto.points
            this.questionType = this@GeneralQuestionCreateDto.questionType
            // Professor y Racing se dejan nulos aquí para preguntas generales.
            // La entidad Questions ahora los permite como nullable = true en la DB.
            // Para preguntas específicas, se asignan en el controlador.
        }
    }
}