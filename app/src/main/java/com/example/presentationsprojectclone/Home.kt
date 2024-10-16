package com.example.presentationsprojectclone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.my_projects -> {
                    replaceFragment(My_Project_Page())
                    true
                }

                R.id.templates -> {
                    replaceFragment(templates_page())
                    true
                }

                R.id.image -> {
                    replaceFragment(Images_Page())
                    true
                }

                R.id.assets -> {
                    replaceFragment(Assets_page())
                    true
                }

                R.id.more -> {
                    replaceFragment(More_page())
                    true
                }

                else -> false
            }
        }
        replaceFragment(My_Project_Page())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.Frame_Layout, fragment).commit()
    }
}

