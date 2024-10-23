package com.example.poupafacil.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.poupafacil.R
import com.example.poupafacil.adapter.TransactionAdapter
import com.example.poupafacil.data.DatabaseHelper
import com.example.poupafacil.data.Transaction

class TransactionListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewTransactions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        dbHelper = DatabaseHelper(requireContext())
        loadTransactions()

        return view
    }

    private fun loadTransactions() {
        val transactions: List<Transaction> = dbHelper.getAllTransactions()
        transactionAdapter = TransactionAdapter(transactions, { transaction ->
            navigateToEditTransaction(transaction)
        }, { transaction ->
            deleteTransaction(transaction)
        })
        recyclerView.adapter = transactionAdapter
    }

    fun navigateToEditTransaction(transaction: Transaction) {
        val editTransactionFragment = EditTransactionFragment.newInstance(transaction.id)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, editTransactionFragment)
            .addToBackStack(null)
            .commit()
    }

    fun deleteTransaction(transaction: Transaction) {
        val result = dbHelper.deleteTransaction(transaction.id)
        if (result > 0) {
            Toast.makeText(requireContext(), "Transação deletada", Toast.LENGTH_SHORT).show()
            loadTransactions()
        } else {
            Toast.makeText(requireContext(), "Erro ao deletar", Toast.LENGTH_SHORT).show()
        }
    }
}
