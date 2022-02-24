package com.example.burparking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.burparking.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

/*
 TODO: Dependiendo en que fragmento estemos, habilitar el botón "cerrar sesión"
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val photoURI = bundle?.getString("photoUri")
        guardarDatosUsuario(email.toString(), photoURI.toString())

        setSupportActionBar(binding.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        navView.setNavigationItemSelectedListener(this)

//        setSupportActionBar(binding.toolbar)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

    }



//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.activity_main_drawer, menu)
//        return true
//    }

    private fun guardarDatosUsuario(email: String, photoURI: String) {
        val prefs = getSharedPreferences("inicioSesion", Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("photoURI", photoURI)
        prefs.apply()
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId){
//            R.id.nav_cerrar_sesion -> cerrarSesion()
//        }
//        return true
////        return when (item.itemId) {
////            R.id.action_settings -> true
////            else -> super.onOptionsItemSelected(item)
////        }
//    }

    private fun cerrarSesion(): Boolean {
        val prefs = this.getSharedPreferences("inicioSesion", Context.MODE_PRIVATE)!!.edit()
        prefs.clear()
        prefs.apply()
        val drawerLayout: DrawerLayout = binding.drawerLayout
        drawerLayout.closeDrawers()
        navegarLogin()
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
    }

    private fun navegarLogin(){
        val intent = Intent(this, LoginActivity::class.java).apply {  }
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_cerrar_sesion -> cerrarSesion()
        }
        return true
    }


}