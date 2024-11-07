package com.example.poupafacil.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class FirestoreHelper {

    private val db = FirebaseFirestore.getInstance()
    private val transactionsCollection = db.collection("transactions")

    fun insertTransaction(transaction: Transaction, onSuccess: (Transaction) -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val transactionRef = db.collection("transactions").document()

        val transactionWithId = transaction.copy(id = transactionRef.id)

        transactionRef.set(transactionWithId)
            .addOnSuccessListener {
                onSuccess(transactionWithId)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateTransaction(transaction: Transaction, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        transaction.id?.let { id ->
            transactionsCollection.document(id).set(transaction)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
        }
    }

    fun deleteTransaction(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        transactionsCollection.document(id).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun getAllTransactions(onSuccess: (List<Transaction>) -> Unit, onFailure: (Exception) -> Unit) {
        transactionsCollection.get()
            .addOnSuccessListener { result ->
                val transactionsList = result.documents.mapNotNull { it.toObject<Transaction>() }
                onSuccess(transactionsList)
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun getTransactionById(id: String, onSuccess: (Transaction?) -> Unit, onFailure: (Exception) -> Unit) {
        transactionsCollection.document(id).get()
            .addOnSuccessListener { document ->
                val transaction = document.toObject<Transaction>()
                onSuccess(transaction)
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun getTotalIncome(onSuccess: (Double) -> Unit, onFailure: (Exception) -> Unit) {
        transactionsCollection.whereEqualTo("type", "Receita")
            .get()
            .addOnSuccessListener { result ->
                val total = result.documents.mapNotNull { it.toObject<Transaction>() }
                    .sumOf { it.amount }
                onSuccess(total)
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun getTotalExpenses(onSuccess: (Double) -> Unit, onFailure: (Exception) -> Unit) {
        transactionsCollection.whereEqualTo("type", "Despesa")
            .get()
            .addOnSuccessListener { result ->
                val total = result.documents.mapNotNull { it.toObject<Transaction>() }
                    .sumOf { it.amount }
                onSuccess(total)
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun getIncomeByCategory(onSuccess: (Map<String, Double>) -> Unit, onFailure: (Exception) -> Unit) {
        transactionsCollection.whereEqualTo("type", "Receita")
            .get()
            .addOnSuccessListener { result ->
                val incomeByCategory = result.documents.mapNotNull { it.toObject<Transaction>() }
                    .groupBy { it.category }
                    .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
                onSuccess(incomeByCategory)
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun getExpensesByCategory(onSuccess: (Map<String, Double>) -> Unit, onFailure: (Exception) -> Unit) {
        transactionsCollection.whereEqualTo("type", "Despesa")
            .get()
            .addOnSuccessListener { result ->
                val expensesByCategory = result.documents.mapNotNull { it.toObject<Transaction>() }
                    .groupBy { it.category }
                    .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
                onSuccess(expensesByCategory)
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}
