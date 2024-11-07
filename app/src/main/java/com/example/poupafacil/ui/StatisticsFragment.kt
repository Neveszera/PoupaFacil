package com.example.poupafacil.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.poupafacil.R
import com.example.poupafacil.data.FirestoreHelper
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.*

class StatisticsFragment : Fragment() {

    private lateinit var dbHelper: FirestoreHelper
    private lateinit var barChartRevenueVsExpense: BarChart
    private lateinit var pieChartIncomeByCategory: PieChart
    private lateinit var pieChartExpenseByCategory: PieChart
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpenses: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        barChartRevenueVsExpense = view.findViewById(R.id.barChartRevenueVsExpense)
        pieChartIncomeByCategory = view.findViewById(R.id.pieChartIncomeByCategory)
        pieChartExpenseByCategory = view.findViewById(R.id.pieChartExpenseByCategory)
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome)
        tvTotalExpenses = view.findViewById(R.id.tvTotalExpenses)

        dbHelper = FirestoreHelper()

        loadStatistics()

        return view
    }

    private fun loadStatistics() {
        dbHelper.getTotalExpenses(onSuccess = { totalExpenses ->
            tvTotalExpenses.text = "Total Despesas: R$ ${String.format("%,.2f", totalExpenses)}"
            dbHelper.getTotalIncome(onSuccess = { totalIncome ->
                tvTotalIncome.text = "Total Receitas: R$ ${String.format("%,.2f", totalIncome)}"
                setupBarChart(totalIncome, totalExpenses)
            }, onFailure = { exception -> handleError(exception) })
        }, onFailure = { exception -> handleError(exception) })

        setupPieChartIncomeByCategory()
        setupPieChartExpenseByCategory()
    }

    private fun setupBarChart(income: Double, expenses: Double) {
        val entries = arrayListOf(
            BarEntry(0f, income.toFloat()),
            BarEntry(1f, expenses.toFloat())
        )

        val dataSet = BarDataSet(entries, "").apply {
            colors = listOf(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"))
        }

        barChartRevenueVsExpense.apply {
            data = BarData(dataSet)
            description = Description().apply {
                text = "Comparação de Receitas e Despesas"
                textColor = Color.BLACK
            }
            axisLeft.textColor = Color.BLACK
            axisRight.isEnabled = false
            legend.isEnabled = false
            invalidate()
        }
    }

    private fun setupPieChartIncomeByCategory() {
        dbHelper.getIncomeByCategory(onSuccess = { categories ->
            val entries = categories.map { PieEntry(it.value.toFloat(), it.key) }
            val colors = getColorList(entries.size)

            val pieDataSet = PieDataSet(entries, "Receitas por Categoria").apply {
                this.colors = colors
                valueTextColor = Color.BLACK
                valueTextSize = 12f
            }

            pieChartIncomeByCategory.apply {
                data = PieData(pieDataSet)
                description = Description().apply {
                    text = "Distribuição de Receitas"
                    textColor = Color.BLACK
                }
                legend.apply {
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                    orientation = Legend.LegendOrientation.VERTICAL
                    textColor = Color.BLACK
                }
                invalidate()
            }
        }, onFailure = { exception -> handleError(exception) })
    }

    private fun setupPieChartExpenseByCategory() {
        dbHelper.getExpensesByCategory(onSuccess = { categories ->
            val entries = categories.map { PieEntry(it.value.toFloat(), it.key) }
            val colors = getColorList(entries.size)

            val pieDataSet = PieDataSet(entries, "Despesas por Categoria").apply {
                this.colors = colors
                valueTextColor = Color.BLACK
                valueTextSize = 12f
            }

            pieChartExpenseByCategory.apply {
                data = PieData(pieDataSet)
                description = Description().apply {
                    text = "Distribuição de Despesas"
                    textColor = Color.BLACK
                }
                legend.apply {
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                    orientation = Legend.LegendOrientation.VERTICAL
                    textColor = Color.BLACK
                }
                invalidate()
            }
        }, onFailure = { exception -> handleError(exception) })
    }

    private fun getColorList(size: Int): List<Int> {
        val colors = mutableListOf<Int>()
        val random = Random()

        for (i in 0 until size) {
            colors.add(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)))
        }

        return colors
    }

    private fun handleError(exception: Exception) {
        // Handle error (e.g., show a toast or log the error)
    }
}
