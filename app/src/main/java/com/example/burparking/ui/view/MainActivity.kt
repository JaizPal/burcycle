package com.example.burparking.ui.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.burparking.R
import com.example.burparking.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.picasso.transformations.CropCircleTransformation

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val photoURI = bundle?.getString("photoUri")
        guardarDatosUsuario(email.toString(), photoURI.toString())
        setAvatarCorreo(photoURI, email)
        setSupportActionBar(binding.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        navView.setNavigationItemSelectedListener(this)
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        getWindow().statusBarColor = ContextCompat.getColor(this, R.color.verdeClaro)
        supportActionBar?.setDisplayShowTitleEnabled(false);
    }

    private fun guardarDatosUsuario(email: String, photoURI: String) {
        val prefs = getSharedPreferences("inicioSesion", Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("photoURI", photoURI)
        prefs.apply()
    }

    private fun setAvatarCorreo(photoURI: String?, email: String?) {
        val header = binding.navView.getHeaderView(0)
        val imagen = header.findViewById<ImageView>(R.id.imagenUsuario)
        val correo = header.findViewById<TextView>(R.id.textView)
        correo.text = email.toString()
        val imagenURL = Uri.parse(photoURI)
        Picasso.get().load(imagenURL)
            .resize(220, 220)
            .transform(CropCircleTransformation())
            .into(imagen)

    }

    private fun cerrarSesion(): Boolean {
        val prefs = this.getSharedPreferences("inicioSesion", Context.MODE_PRIVATE)!!.edit()
        prefs.clear()
        prefs.apply()
        val drawerLayout: DrawerLayout = binding.drawerLayout
        drawerLayout.closeDrawers()
        navegarLogin()
        return true
    }

    private fun irIncidencia(): Boolean {
        binding.drawerLayout.closeDrawers()
        val intent = Intent(this, IncidenciaActivity::class.java).apply { }
        startActivity(intent)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
    }

    private fun navegarLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply { }
        AuthUI.getInstance().signOut(this)
        startActivity(intent)
        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.i("Algo", item.itemId.toString() + " Jaime")
        when (item.itemId) {
            R.id.nav_cerrar_sesion -> cerrarSesion()
            R.id.incidenciaActivity -> irIncidencia()
        }

        return true
    }
}