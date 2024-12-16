package com.example.studyhelpermdapp

data class StudyGroup(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val members: List<String> = emptyList()
)
