package com.example.burparking.ui.view

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.burparking.databinding.FragmentBuscarDireccionBinding
import com.example.burparking.domain.model.Direccion
import com.example.burparking.domain.model.Parking
import com.example.burparking.ui.view.adapter.ParkingAdapter
import com.example.burparking.ui.viewModel.BuscarDireccionViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuscarDireccionFragment : Fragment() {

    private var _binding: FragmentBuscarDireccionBinding? = null
    private val binding get() = _binding!!

    private var permisosConcedidos = false
    private val CODIGO_PERMISOS_UBICACION_SEGUNDO_PLANO = 1

    private lateinit var localizacionUsuario: FusedLocationProviderClient

    private val buscarDireccionViewModel: BuscarDireccionViewModel by viewModels()
    private lateinit var adapterDirecciones: ArrayAdapter<Direccion>

    private var direccionActual: Direccion? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buscarDireccionViewModel.onCreate()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuscarDireccionBinding.inflate(inflater, container, false)
        verificarPermisos()
        val autoCompletado = binding.autoCompleteDirecciones
        buscarDireccionViewModel.isLoading.observe(requireActivity()) {
            if(it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
            binding.progressBar.isVisible = it
            Log.i("progressBar", it.toString())
        }

        buscarDireccionViewModel.direcciones.observe(requireActivity()) {
            if (it != null) {
                adapterDirecciones = ArrayAdapter<Direccion>(
                    requireActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    buscarDireccionViewModel.direcciones.value!!.toList()
                )
                autoCompletado.setAdapter(adapterDirecciones)
            }
        }

        autoCompletado.setOnItemClickListener { parent, view, position, id ->
            buscarDireccionViewModel.parkingCercanos(adapterDirecciones.getItem(position)!!)
            direccionActual = adapterDirecciones.getItem(position)
        }

        buscarDireccionViewModel.parkingCargados.observe(requireActivity()) {
            if(it == false) {
                binding.shimmer.startShimmer()
                binding.shimmer.visibility = View.VISIBLE
                binding.recyclerViewParking.visibility = View.GONE
            }
            if (it == true && !binding.autoCompleteDirecciones.text.isNullOrEmpty()) {
                cargarCardsParkings()
                binding.shimmer.visibility = View.GONE
                binding.shimmer.stopShimmer()
                binding.recyclerViewParking.visibility = View.VISIBLE
            }

        }

        binding.tvVerMapa.setOnClickListener {
            findNavController().navigate(
                BuscarDireccionFragmentDirections.actionBuscarDireccionFragmentToMapFragment(
                    Direccion(0, 42.340833, -3.699722, null, null, null),
                    arrayOf()
                )
            )
        }

        binding.tvUbicacionActual.setOnClickListener {
            buscarDireccionViewModel.parkingCargados.postValue(false)
            verificarPermisos()
            if (permisosConcedidos) {
                if (localizacionActivada()) {
                    localizacionUsuario =
                        LocationServices.getFusedLocationProviderClient(requireActivity())
                    localizacionUsuario.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).addOnSuccessListener {
                        direccionActual = Direccion(
                            0,
                            it.latitude,
                            it.longitude,
                            null,
                            null,
                            null
                        )
                        buscarDireccionViewModel.parkingCercanos(
                            direccionActual!!
                        )
                        buscarDireccionViewModel.getReverseDireccion(
                            Parking(
                                0,
                                it.latitude,
                                it.longitude,
                                0,
                                null,
                                null,
                                null,
                                null
                            )
                        )
                    }
                } else {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "Localización desactivada",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Permisos no concedidos",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        buscarDireccionViewModel.reverseDireccion.observe(requireActivity()) {
            if (it != null) {
                autoCompletado.setText(it.toString())
            }
        }

        return binding.root
    }

    private fun cargarCardsParkings() {
        val recyclerView = binding.recyclerViewParking
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter =
            ParkingAdapter(buscarDireccionViewModel.closestParkings.value!!, direccionActual!!)
    }

    private fun checkPermisos(permisos: Array<String>): Boolean {
        return permisos.all {
            return ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun solicitarPermisos(permisos: Array<String>) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            permisos,
            CODIGO_PERMISOS_UBICACION_SEGUNDO_PLANO
        )
    }

    private fun verificarPermisos() {
        val permisos =
            arrayListOf(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION,
                ACCESS_NETWORK_STATE,
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permisos.add(ACCESS_BACKGROUND_LOCATION)
        }
        val permisosArray = permisos.toTypedArray()

        if (checkPermisos(permisosArray)) {
            permisosConcedidos = true
        } else {
            solicitarPermisos(permisosArray)
        }
    }

    private fun localizacionActivada(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    override fun onPause() {
        super.onPause()
        buscarDireccionViewModel.parkingCargados.value = null
        binding.shimmer.visibility = View.GONE
        binding.autoCompleteDirecciones.setText("")
    }

}