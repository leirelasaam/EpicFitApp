package com.example.epicfitapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import modelo.pojos.UsuarioLogueado

open class BaseActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("mi_prefs", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        enableEdgeToEdge()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val item: MenuItem? = menu.findItem(R.id.switch_tema)
        val switch = item?.actionView?.findViewById<SwitchCompat>(R.id.switchTheme)

        val isDarkMode = sharedPreferences.getBoolean("dark_mode", false)
        switch?.isChecked = isDarkMode

        // Configurar el listener para el Switch
        switch?.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
        }

        // Ocultar ítems si la actividad actual es LoginActivity
        if (this is LoginActivity) {
            ocultarItemsDelMenu(menu)
        } else {
            mostrarItemsSegunUsuario(menu)
        }

        return true
    }

    // Función para ocultar los ítems del menú
    private fun ocultarItemsDelMenu(menu: Menu) {
        menu.findItem(R.id.item_workout)?.isVisible = false
        menu.findItem(R.id.item_perfil)?.isVisible = false
        menu.findItem(R.id.item_entrenador)?.isVisible = false
    }

    // Función para mostrar ítems según el tipo de usuario
    private fun mostrarItemsSegunUsuario(menu: Menu) {
        val usuarioActual = UsuarioLogueado.usuario
        if (usuarioActual != null && !usuarioActual.esEntrenador!!) {
            menu.findItem(R.id.item_entrenador)?.isVisible = false
        }
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
            else -> return super.onOptionsItemSelected(menuItem)
        }
        return true
    }
}
