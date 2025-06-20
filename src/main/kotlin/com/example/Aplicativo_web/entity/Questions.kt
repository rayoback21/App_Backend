package com.example.Aplicativo_web.entity

import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonIgnore

@Entity
@Table(name = "questions")
class Questions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    var id: Long? = null

    @Column(name = "text", nullable = false, length = 500)
    var text: String? = null

    @Column(name = "software_q", nullable = false, length = 450)
    var softwareQ: String? = null

    @Column(name = "design_q", nullable = false, length = 450)
    var designQ: String? = null

    @Column(name = "gastronomy_q", nullable = false, length = 450)
    var gastronomyQ: String? = null

    @Column(name = "marketing_q", nullable = false, length = 450)
    var marketingQ: String? = null

    @Column(name = "tourism_q", nullable = false, length = 450)
    var tourismQ: String? = null

    @Column(name = "talent_q", nullable = false, length = 450)
    var talentQ: String? = null

    @Column(name = "nursing_q", nullable = false, length = 450)
    var nursingQ: String? = null

    @Column(name = "electricity_q", nullable = false, length = 450)
    var electricityQ: String? = null

    @Column(name = "accounting_q", nullable = false, length = 450)
    var accountingQ: String? = null

    @Column(name = "networks_q", nullable = false, length = 450)
    var networksQ: String? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aspirants_id")
    var aspirants: Aspirants? = null

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admins_id")
    var admins: Admins? = null
}