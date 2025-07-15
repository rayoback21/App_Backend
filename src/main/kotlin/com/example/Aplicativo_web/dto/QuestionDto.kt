package com.example.Aplicativo_web.dto

class QuestionDto {
    var id: Long? = null
    var text: String? = null
    var softwareQ: String? = null
    var designQ: String? = null
    var gastronomyQ: String? = null
    var marketingQ: String? = null
    var tourismQ: String? = null
    var talentQ: String? = null
    var nursingQ: String? = null
    var electricityQ: String? = null
    var accountingQ: String? = null
    var networksQ: String? = null

    // Opciones de respuesta
    var optionA: String? = null
    var optionB: String? = null
    var optionC: String? = null
    var optionD: String? = null

    var correctOption: Char? = null
    var questionType: String? = null
    var points: Int? = null
    var professorId: Long? = null
    var racingId: Long? = null


}
