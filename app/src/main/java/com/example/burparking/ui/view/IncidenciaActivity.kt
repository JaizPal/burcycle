package com.example.burparking.ui.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.burparking.R
import com.example.burparking.databinding.ActivityIncidenciaBinding
import com.example.burparking.domain.model.Parking
import com.example.burparking.ui.viewModel.IncidenciaViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncidenciaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIncidenciaBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapterTiposIncidencias: ArrayAdapter<String>
    private var parking: Parking? = null

    private val incidenciaViewModel: IncidenciaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncidenciaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        incidenciaViewModel.onCreate()
        parking = intent.extras?.get("parking") as? Parking
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        getWindow().statusBarColor = ContextCompat.getColor(this, R.color.verdeClaro)

        if (parking == null) {
            Snackbar.make(
                this.findViewById(android.R.id.content),
                "Si deseas enviar una incidencia de un aparcamiento, hágalo seleccionándolo en el mapa",
                Snackbar.LENGTH_LONG
            ).show()
        }

        incidenciaViewModel.tiposIncidencias.observe(this) {
            if (!it.isNullOrEmpty()) {
                val adapter = ArrayAdapter(
                    this,
                    R.layout.item_tipo_incidencia,
                    it
                )
                (binding.incidenciaInputLayout.editText as? AutoCompleteTextView)?.setAdapter(
                    adapter
                )
                binding.tipoIncidencia.setText(it[0], false)

            }
        }

        binding.enviarButton.setOnClickListener {
            incidenciaViewModel.addIncidencia(
                binding.descripcion.text.toString(),
                binding.tipoIncidencia.text.toString(),
                parking
            )
        }

        incidenciaViewModel.succes.observe(this) {

            if (it == 0) {
                Toast.makeText(this, "Incidencia agregada", Toast.LENGTH_LONG).show()
                finish()
            } else if (it == -1) {
                Snackbar.make(
                    this.findViewById(android.R.id.content),
                    "Se ha producido un error",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}