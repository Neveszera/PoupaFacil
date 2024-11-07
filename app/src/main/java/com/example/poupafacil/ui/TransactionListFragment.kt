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
import com.example.poupafacil.data.FirestoreHelper
import com.example.poupafacil.data.Transaction

class TransactionListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var dbHelper: FirestoreHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewTransactions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        dbHelper = FirestoreHelper()

        loadTransactions()

        return view
    }

    private fun loadTransactions() {
        dbHelper.getAllTransactions(
            onSuccess = { transactions ->
                transactionAdapter = TransactionAdapter(transactions, { transaction ->
                    navigateToEditTransaction(transaction)
                }, { transaction ->
                    deleteTransaction(transaction)
                })
                recyclerView.adapter = transactionAdapter
            },
            onFailure = { exception ->
                Toast.makeText(requireContext(), "Erro ao carregar transações: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun navigateToEditTransaction(transaction: Transaction) {
        val editTransactionFragment = EditTransactionFragment.newInstance(transaction.id)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, editTransactionFragment)
            .addToBackStack(null)
            .commit()
    }

    fun deleteTransaction(transaction: Transaction) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmação")
        builder.setMessage("Você tem certeza que deseja deletar esta transação?")

        builder.setPositiveButton("Sim") { dialog, _ ->
            val transactionId = transaction.id
            if (!transactionId.isNullOrEmpty()) {
                dbHelper.deleteTransaction(transactionId,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Transação deletada", Toast.LENGTH_SHORT).show()
                        loadTransactions()
                    },
                    onFailure = { exception ->
                        Toast.makeText(requireContext(), "Erro ao deletar transação: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(requireContext(), "ID da transação não encontrado", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("Não") { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()
    }
}
