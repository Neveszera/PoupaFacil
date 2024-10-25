package com.example.poupafacil

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.poupafacil.ui.AddTransactionFragment
import com.example.poupafacil.ui.DevelopersFragment
import com.example.poupafacil.ui.StatisticsFragment
import com.example.poupafacil.ui.TransactionListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTheme(R.style.AppTheme_Light);

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add_transaction -> {
                    loadFragment(AddTransactionFragment())
                    true
                }
                R.id.nav_statistics -> {
                    loadFragment(StatisticsFragment())
                    true
                }
                R.id.nav_devs -> {
                    loadFragment(DevelopersFragment())
                    true
                }
                R.id.nav_list_transactions -> {
                    loadFragment(TransactionListFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            loadFragment(TransactionListFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        Log.d("MainActivity", "Fragment loaded: ${fragment.javaClass.simpleName}")
    }
}
