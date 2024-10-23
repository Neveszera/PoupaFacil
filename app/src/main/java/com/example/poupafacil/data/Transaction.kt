package com.example.poupafacil.data

data class Transaction(
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val date: String,
    val category: String,
    val type: String
)