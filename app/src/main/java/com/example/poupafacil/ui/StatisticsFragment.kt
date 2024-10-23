package com.example.poupafacil.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.poupafacil.R
import com.example.poupafacil.data.DatabaseHelper
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter

class StatisticsFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
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

        dbHelper = DatabaseHelper(requireContext())

        loadStatistics()

        return view
    }

    private fun loadStatistics() {
        val totalIncome = dbHelper.getTotalIncome()
        val totalExpenses = dbHelper.getTotalExpenses()

        tvTotalIncome.text = "Total Receitas: R$ $totalIncome"
        tvTotalExpenses.text = "Total Despesas: R$ $totalExpenses"

        setupBarChart(totalIncome, totalExpenses)
        setupPieChartIncomeByCategory()
        setupPieChartExpenseByCategory()
    }

    private fun setupBarChart(income: Double, expenses: Double) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, income.toFloat()))
        entries.add(BarEntry(1f, expenses.toFloat()))

        val incomeDataSet = BarDataSet(listOf(BarEntry(0f, income.toFloat())), "Receitas")
        incomeDataSet.color = Color.GREEN

        val expenseDataSet = BarDataSet(listOf(BarEntry(1f, expenses.toFloat())), "Despesas")
        expenseDataSet.color = Color.RED

        val data = BarData(incomeDataSet, expenseDataSet)
        barChartRevenueVsExpense.data = data

        barChartRevenueVsExpense.description = Description().apply {
            text = "Comparação de Receitas e Despesas"
            textColor = Color.BLACK
        }

        barChartRevenueVsExpense.axisLeft.apply {
            textColor = Color.BLACK
        }

        barChartRevenueVsExpense.axisRight.isEnabled = false

        barChartRevenueVsExpense.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.BLACK
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return when (value.toInt()) {
                        0 -> "Receitas"
                        1 -> "Despesas"
                        else -> ""
                    }
                }
            }
        }

        barChartRevenueVsExpense.invalidate()
    }

    private fun setupPieChartIncomeByCategory() {
        val categories = dbHelper.getIncomeByCategory()
        val entries = categories.map { PieEntry(it.value.toFloat(), it.key) }

        val pieDataSet = PieDataSet(entries, "Receitas por Categoria")
        pieDataSet.colors = listOf(Color.BLUE, Color.CYAN, Color.MAGENTA)
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 12f

        val pieData = PieData(pieDataSet)
        pieChartIncomeByCategory.data = pieData

        pieChartIncomeByCategory.description = Description().apply {
            text = "Distribuição de Receitas"
            textColor = Color.BLACK
        }

        pieChartIncomeByCategory.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            textColor = Color.BLACK
        }

        pieChartIncomeByCategory.invalidate()
    }

    private fun setupPieChartExpenseByCategory() {
        val categories = dbHelper.getExpensesByCategory()
        val entries = categories.map { PieEntry(it.value.toFloat(), it.key) }

        val pieDataSet = PieDataSet(entries, "Despesas por Categoria")
        pieDataSet.colors = listOf(Color.RED, Color.YELLOW, Color.GREEN)
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 12f

        val pieData = PieData(pieDataSet)
        pieChartExpenseByCategory.data = pieData

        pieChartExpenseByCategory.description = Description().apply {
            text = "Distribuição de Despesas"
            textColor = Color.BLACK
        }

        pieChartExpenseByCategory.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            textColor = Color.BLACK
        }

        pieChartExpenseByCategory.invalidate()
    }
}
