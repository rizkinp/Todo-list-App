package com.example.project_uas_todo2

data class Tugas(
    val id: String,
    val userId: String,
    var topic: String,
    var description: String,
    var mataKuliahId: String,
    var dueDate: String
)
