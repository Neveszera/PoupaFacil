package com.example.poupafacil.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.poupafacil.R
import com.example.poupafacil.data.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

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
        holder.tvCategory.text = transaction.category
        holder.tvAmount.text = formatCurrency(transaction.amount)
        holder.tvDate.text = formatDate(transaction.date)

        // Define as cores correspondentes
        val backgroundColor = if (transaction.type == "Receita") {
            Color.parseColor("#4CAF50") // Verde
        } else {
            Color.parseColor("#F44336") // Vermelho
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

    private fun formatCurrency(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return format.format(value)
    }

    private fun formatDate(dateString: String): String {
        val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        val targetFormat = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("pt", "BR"))
        val date = originalFormat.parse(dateString)
        return targetFormat.format(date!!)
    }
}
