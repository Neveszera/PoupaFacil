package com.example.poupafacil.data

data class Transaction(
    var id: String = "",
    val name: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val category: String = "",
    val type: String = ""
)
