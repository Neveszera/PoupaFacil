package com.example.poupafacil.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "transactions.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "transactions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_TYPE = "type"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, "
                + "$COLUMN_AMOUNT REAL, "
                + "$COLUMN_DATE TEXT, "
                + "$COLUMN_CATEGORY TEXT, "
                + "$COLUMN_TYPE TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTransaction(transaction: Transaction): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, transaction.name)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_DATE, transaction.date)
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_TYPE, transaction.type)
        }

        return db.insert(TABLE_NAME, null, contentValues)
    }

    fun updateTransaction(transaction: Transaction): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, transaction.name)
            put(COLUMN_AMOUNT, transaction.amount)
            put(COLUMN_DATE, transaction.date)
            put(COLUMN_CATEGORY, transaction.category)
            put(COLUMN_TYPE, transaction.type)
        }

        return db.update(TABLE_NAME, contentValues, "$COLUMN_ID=?", arrayOf(transaction.id.toString()))
    }

    fun deleteTransaction(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
    }

    fun getAllTransactions(): List<Transaction> {
        val transactionsList = mutableListOf<Transaction>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        if (cursor.moveToFirst()) {
            do {
                val transaction = Transaction(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                )
                transactionsList.add(transaction)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return transactionsList
    }

    fun getTransactionById(id: Int): Transaction? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(id.toString()))

        return if (cursor.moveToFirst()) {
            val transaction = Transaction(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
            )
            cursor.close()
            transaction
        } else {
            cursor.close()
            null
        }
    }

    fun getTotalIncome(): Double {
        val totalIncomeQuery = "SELECT SUM(amount) FROM transactions WHERE type = 'Receita'"
        val cursor = this.readableDatabase.rawQuery(totalIncomeQuery, null)
        var totalIncome = 0.0
        if (cursor.moveToFirst()) {
            totalIncome = cursor.getDouble(0)
        }
        cursor.close()
        return totalIncome
    }

    fun getTotalExpenses(): Double {
        val totalExpensesQuery = "SELECT SUM(amount) FROM transactions WHERE type = 'Despesa'"
        val cursor = this.readableDatabase.rawQuery(totalExpensesQuery, null)
        var totalExpenses = 0.0
        if (cursor.moveToFirst()) {
            totalExpenses = cursor.getDouble(0)
        }
        cursor.close()
        return totalExpenses
    }

    fun getIncomeByCategory(): Map<String, Double> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT category, SUM(amount) FROM transactions WHERE type = 'Receita' GROUP BY category", null)
        val incomeMap = mutableMapOf<String, Double>()

        while (cursor.moveToNext()) {
            val category = cursor.getString(0)
            val amount = cursor.getDouble(1)
            incomeMap[category] = amount
        }
        cursor.close()
        return incomeMap
    }

    fun getExpensesByCategory(): Map<String, Double> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT category, SUM(amount) FROM transactions WHERE type = 'Despesa' GROUP BY category", null)
        val expenseMap = mutableMapOf<String, Double>()

        while (cursor.moveToNext()) {
            val category = cursor.getString(0)
            val amount = cursor.getDouble(1)
            expenseMap[category] = amount
        }
        cursor.close()
        return expenseMap
    }
}