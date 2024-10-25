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
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.*

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

        tvTotalIncome.text = "Total Receitas: R$ ${String.format("%,.2f", totalIncome)}"
        tvTotalExpenses.text = "Total Despesas: R$ ${String.format("%,.2f", totalExpenses)}"

        setupBarChart(totalIncome, totalExpenses)
        setupPieChartIncomeByCategory()
        setupPieChartExpenseByCategory()
    }

    private fun setupBarChart(income: Double, expenses: Double) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, income.toFloat()))
        entries.add(BarEntry(1f, expenses.toFloat()))

        val dataSet = BarDataSet(entries, "")

        dataSet.colors = listOf(
            Color.parseColor("#4CAF50"),
            Color.parseColor("#F44336")
        )

        val data = BarData(dataSet)
        barChartRevenueVsExpense.data = data

        barChartRevenueVsExpense.description = Description().apply {
            text = "Comparação de Receitas e Despesas"
            textColor = Color.BLACK
        }

        barChartRevenueVsExpense.axisLeft.apply {
            textColor = Color.BLACK
        }

        barChartRevenueVsExpense.axisRight.isEnabled = false

        barChartRevenueVsExpense.legend.isEnabled = false
        barChartRevenueVsExpense.invalidate()
    }

    private fun setupPieChartIncomeByCategory() {
        val categories = dbHelper.getIncomeByCategory()
        val entries = categories.map { PieEntry(it.value.toFloat(), it.key) }

        val colors = getColorList(entries.size)

        val pieDataSet = PieDataSet(entries, "Receitas por Categoria")
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 12f

        val pieData = PieData(pieDataSet)
        pieChartIncomeByCategory.data = pieData

        pieChartIncomeByCategory.description = Description().apply {
            text = "Distribuição de Receitas"
            textColor = Color.BLACK
        }

        pieChartIncomeByCategory.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.VERTICAL
            textColor = Color.BLACK
        }

        pieChartIncomeByCategory.invalidate()
    }

    private fun setupPieChartExpenseByCategory() {
        val categories = dbHelper.getExpensesByCategory()
        val entries = categories.map { PieEntry(it.value.toFloat(), it.key) }

        val colors = getColorList(entries.size)

        val pieDataSet = PieDataSet(entries, "Despesas por Categoria")
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 12f

        val pieData = PieData(pieDataSet)
        pieChartExpenseByCategory.data = pieData

        pieChartExpenseByCategory.description = Description().apply {
            text = "Distribuição de Despesas"
            textColor = Color.BLACK
        }

        pieChartExpenseByCategory.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.VERTICAL
            textColor = Color.BLACK
        }

        pieChartExpenseByCategory.invalidate()
    }

    private fun getColorList(size: Int): List<Int> {
        val colors = mutableListOf<Int>()
        val random = Random()

        for (i in 0 until size) {
            colors.add(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)))
        }

        return colors
    }
}
