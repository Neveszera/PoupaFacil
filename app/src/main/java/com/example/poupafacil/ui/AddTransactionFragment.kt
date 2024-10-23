package com.example.poupafacil.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.poupafacil.data.DatabaseHelper
import com.example.poupafacil.data.Transaction
import com.example.poupafacil.databinding.FragmentAddTransactionBinding
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private var isUpdating = false
    private val localeBR = Locale("pt", "BR")
    private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(localeBR)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        setupAmountFormatting()
        setupDateFormatting()
        setupInputValidation()

        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                val name = binding.etName.text.toString()
                val amountString = binding.etAmount.text.toString()

                val amount = parseCurrencyToDouble(amountString)

                val date = binding.etDate.text.toString()
                val category = binding.etCategory.text.toString()
                val type = if (binding.rbIncome.isChecked) "Receita" else "Despesa"

                if (amount != null && amount > 0.0) {
                    val transaction = Transaction(
                        name = name,
                        amount = amount,
                        date = date,
                        category = category,
                        type = type
                    )

                    dbHelper.insertTransaction(transaction)
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    binding.etAmount.error = "Insira um valor válido"
                }
            }
        }
    }

    private fun setupAmountFormatting() {
        binding.etAmount.addTextChangedListener(object : TextWatcher {
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

                        binding.etAmount.setText(formatted)
                        binding.etAmount.setSelection(formatted.length)
                    }
                }

                isUpdating = false
            }
        })
    }

    private fun setupDateFormatting() {
        binding.etDate.addTextChangedListener(object : TextWatcher {
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

                binding.etDate.setText(formatted.toString(), TextView.BufferType.NORMAL)
                binding.etDate.setSelection(formatted.length)

                isFormatting = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun parseCurrencyToDouble(currency: String): Double? {
        return try {
            val cleanString = currency.replace("[R$,.\\s]".toRegex(), "")
            cleanString.toDoubleOrNull()?.div(100)
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun setupInputValidation() {
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (binding.etName.text.toString().isEmpty()) {
            binding.etName.error = "Campo obrigatório"
            isValid = false
        }

        val amountString = binding.etAmount.text.toString()
        if (amountString.isEmpty() || parseCurrencyToDouble(amountString) == null) {
            binding.etAmount.error = "Insira um valor válido"
            isValid = false
        }

        val dateText = binding.etDate.text.toString()
        if (dateText.isEmpty() || !isValidDate(dateText)) {
            binding.etDate.error = "Data inválida. Use o formato dd/MM/yyyy"
            isValid = false
        }

        if (binding.etCategory.text.toString().isEmpty()) {
            binding.etCategory.error = "Campo obrigatório"
            isValid = false
        }

        if (!binding.rbIncome.isChecked && !binding.rbExpense.isChecked) {
            Toast.makeText(requireContext(), "Selecione o tipo (Receita ou Despesa)", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun isValidDate(date: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        dateFormat.isLenient = false
        return try {
            dateFormat.parse(date)
            true
        } catch (e: ParseException) {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


