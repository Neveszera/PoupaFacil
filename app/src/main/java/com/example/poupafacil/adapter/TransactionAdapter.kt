package com.example.poupafacil.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.poupafacil.R
import com.example.poupafacil.data.Transaction
import com.example.poupafacil.data.DatabaseHelper

class TransactionAdapter(
    private val transactions: List<Transaction>,
    private val onEditClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_card, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.tvName.text = transaction.name
        holder.tvAmount.text = "R$ ${transaction.amount}"
        holder.tvDate.text = transaction.date
        holder.tvCategory.text = transaction.category

        val backgroundColor = if (transaction.type == "Receita") {
            android.graphics.Color.GREEN
        } else {
            android.graphics.Color.RED
        }
        holder.itemView.setBackgroundColor(backgroundColor)

        holder.ivEdit.setOnClickListener {
            onEditClick(transaction)
        }

        holder.ivDelete.setOnClickListener {
            onDeleteClick(transaction)
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }
}
