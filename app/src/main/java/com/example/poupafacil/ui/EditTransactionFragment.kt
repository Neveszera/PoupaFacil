package com.example.poupafacil.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.example.poupafacil.R
import com.example.poupafacil.data.DatabaseHelper
import com.example.poupafacil.data.Transaction
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etCategory: EditText
    private lateinit var rgType: RadioGroup
    private lateinit var rbIncome: RadioButton
    private lateinit var rbExpense: RadioButton
    private lateinit var btnEdit: Button
    private lateinit var dbHelper: DatabaseHelper
    private var isUpdating = false
    private val localeBR = Locale("pt", "BR")
    private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(localeBR)

    private var transactionId: Int = -1

    companion object {
        private const val ARG_TRANSACTION_ID = "transaction_id"

        fun newInstance(transactionId: Int): EditTransactionFragment {
            val fragment = EditTransactionFragment()
            val args = Bundle()
            args.putInt(ARG_TRANSACTION_ID, transactionId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_transaction, container, false)

        etName = view.findViewById(R.id.etName)
        etAmount = view.findViewById(R.id.etAmount)
        etDate = view.findViewById(R.id.etDate)
        etCategory = view.findViewById(R.id.etCategory)
        rgType = view.findViewById(R.id.rgType)
        rbIncome = view.findViewById(R.id.rbIncome)
        rbExpense = view.findViewById(R.id.rbExpense)
        btnEdit = view.findViewById(R.id.btnEdit)

        dbHelper = DatabaseHelper(requireContext())

        val bundle = arguments
        if (bundle != null) {
            transactionId = bundle.getInt(ARG_TRANSACTION_ID, -1)
            if (transactionId != -1) {
                loadTransactionDetails(transactionId)
            }
        }

        setupAmountFormatting()
        setupDateFormatting()

        btnEdit.setOnClickListener {
            editTransaction()
        }

        return view
    }

    private fun loadTransactionDetails(transactionId: Int) {
        val transaction = dbHelper.getTransactionById(transactionId)
        if (transaction != null) {
            etName.setText(transaction.name)
            etAmount.setText(currencyFormatter.format(transaction.amount))
            etDate.setText(transaction.date)
            etCategory.setText(transaction.category)

            if (transaction.type == "Receita") {
                rbIncome.isChecked = true
            } else {
                rbExpense.isChecked = true
            }
        } else {
            Toast.makeText(requireContext(), "Transação não encontrada", Toast.LENGTH_SHORT).show()
            btnEdit.isEnabled = false
        }
    }

    private fun setupAmountFormatting() {
        etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return

                isUpdating = true

                val cleanString = s.toString().replace("[R$,.\\s]".toRegex(), "")
                if (cleanString.isNotEmpty()) {
                    val parsed = cleanString.toDoubleOrNull()
                    if (parsed != null) {
                        val formatted = currencyFormatter.format(parsed / 100)

                        etAmount.setText(formatted)
                        etAmount.setSelection(formatted.length)
                    }
                }

                isUpdating = false
            }
        })
    }

    private fun setupDateFormatting() {
        etDate.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isFormatting) return

                isFormatting = true

                val input = s.toString().replace("[^\\d]".toRegex(), "")
                val formatted = StringBuilder()

                for (i in input.indices) {
                    if (i == 2 || i == 4) {
                        formatted.append("/")
                    }
                    formatted.append(input[i])
                }

                etDate.setText(formatted.toString())
                etDate.setSelection(formatted.length)

                isFormatting = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun editTransaction() {
        val name = etName.text.toString()
        val amount = etAmount.text.toString().replace("[R$,.\\s]".toRegex(), "").toDoubleOrNull()?.div(100)
        val date = etDate.text.toString()
        val category = etCategory.text.toString()
        val type = if (rgType.checkedRadioButtonId == R.id.rbIncome) "Receita" else "Despesa"

        if (name.isEmpty() || amount == null || date.isEmpty() || category.isEmpty()) {
            Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidDate(date)) {
            Toast.makeText(requireContext(), "Data inválida. Use o formato dd/MM/yyyy", Toast.LENGTH_SHORT).show()
            return
        }

        val transaction = Transaction(
            id = transactionId,
            name = name,
            amount = amount,
            date = date,
            category = category,
            type = type
        )

        val success = dbHelper.updateTransaction(transaction) > 0
        if (success) {
            Toast.makeText(requireContext(), "Transação atualizada com sucesso", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            Toast.makeText(requireContext(), "Erro ao atualizar transação", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidDate(date: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false
        return try {
            dateFormat.parse(date)
            true
        } catch (e: ParseException) {
            false
        }
    }
}
