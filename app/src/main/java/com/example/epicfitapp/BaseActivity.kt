package com.example.epicfitapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.item_workout -> {
                val intent = Intent(this, HistoricoActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.item_perfil -> {
                val intent = Intent(this, PerfilActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.item_entrenador -> {
                val intent = Intent(this, EntrenadorActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
        return true
    }
}
