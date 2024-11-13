package com.example.epicfitapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import modelo.pojos.UsuarioLogueado
import java.util.Locale


abstract class BaseActivity : AppCompatActivity() {
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

        // Cambiar el color del logo, dependiendo del tema
        val logoMenuItem = menu.findItem(R.id.logo_menu)
        val color = if (isDarkMode) {
            ContextCompat.getColor(this, R.color.white)
        } else {
            ContextCompat.getColor(this, R.color.black)
        }
        logoMenuItem.icon?.setTint(color)

        // Configurar el listener para el Switch
        switch?.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
        }

        // Ocultar ítems si la actividad actual es LoginActivity
        if (this is LoginActivity) //|| this is RegistroActivity)
        {
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
        menu.findItem(R.id.item_cerrar_sesion)?.isVisible = false
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
                if (this::class.java != HistoricoActivity::class.java) {
                    val intent = Intent(this, HistoricoActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.item_perfil -> {
                if (this::class.java != PerfilActivity::class.java) {
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.item_entrenador -> {
                if (this::class.java != EntrenadorActivity::class.java) {
                    val intent = Intent(this, EntrenadorActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.item_cerrar_sesion -> {
                UsuarioLogueado.usuario = null
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> return super.onOptionsItemSelected(menuItem)
        }
        return true
    }
}
