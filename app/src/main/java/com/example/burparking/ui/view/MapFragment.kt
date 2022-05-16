package com.example.burparking.ui.view

import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.preference.PreferenceManager
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.burparking.BuildConfig
import com.example.burparking.R
import com.example.burparking.databinding.FragmentMapBinding
import com.example.burparking.domain.model.Direccion
import com.example.burparking.domain.model.Parking
import com.example.burparking.ui.viewModel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.ThunderforestTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


@AndroidEntryPoint
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    lateinit var map: MapView
    private lateinit var parkings: Array<Parking>
    private var direccionActual: Direccion? = null

    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapViewModel.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setupOsmdroid()
        mapViewModel.onCreate()
        map = binding.map
//        mapColorConfigure()
//        val provider: Array<String> =
//            arrayOf("https://tile.thunderforest.com/cycle/{z}/{x}/{y}.png?apikey=c8fa4a1f921d4384b4e755f57c8e668d")
//        val tileSource = XYTileSource(
//            "cycle",
//            0,
//            18,
//            256,
//            ".png",
//            provider,
//            "© Colaboradores de OpenStreetMap",
//            TileSourcePolicy(2, TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL)
//        )
//        val aceeptUserAgent = TileSourcePolicy(2, TileSourcePolicy.FLAG_NO_PREVENTIVE)

        val thunderforestTileSource =
            ThunderforestTileSource(requireContext(), ThunderforestTileSource.CYCLE)
        map.setTileSource(thunderforestTileSource)
        val copyrightOverlay = CopyrightOverlay(requireContext())
        copyrightOverlay.setCopyrightNotice(thunderforestTileSource.copyrightNotice)
        copyrightOverlay.setAlignRight(true)
        map.overlays.add(copyrightOverlay)

        /*
         * Recupera los aparcamientos mandados por parámetros en la navegación
         */
        parkings = MapFragmentArgs.fromBundle(requireArguments()).parkings
        direccionActual = MapFragmentArgs.fromBundle(requireArguments()).direccionActual
        getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        configureMap()
        if (parkings.isEmpty()) {
            mapViewModel.parkingCargados.observe(requireActivity()) {
                if (it == true) {
                    val items = ArrayList<OverlayItem>()

                    mapViewModel.parkings.value?.forEach {
                        val overlayItem = OverlayItem(
                            it.id.toString(),
                            it.numero,
                            GeoPoint(it.lat, it.lon)
                        )
                        overlayItem.setMarker(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_location_pin
                            )
                        )
                        items.add(overlayItem)

                    }
                    var overlay = ItemizedOverlayWithFocus<OverlayItem>(
                        items,
                        object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                            override fun onItemSingleTapUp(
                                index: Int,
                                item: OverlayItem?
                            ): Boolean {
                                TransitionManager.beginDelayedTransition(
                                    binding.mapLayout,
                                    AutoTransition()
                                )

                                binding.cardParking.visibility = View.VISIBLE
                                binding.LayoutInfo.visibility = View.VISIBLE
                                mapViewModel.setParking(item!!.title.toLong())
                                map.overlays.clear()
                                setRoad(item.point as GeoPoint)

                                return true
                            }

                            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {

                                return false
                            }
                        },
                        requireContext()
                    )
                    map.overlays.add(overlay)
                }

            }
            map.invalidate()
        } else {
            val items = ArrayList<OverlayItem>()
            val overlayItem = OverlayItem(
                parkings[0].id.toString(),
                parkings[0].numero,
                GeoPoint(parkings[0].lat, parkings[0].lon)
            )
            overlayItem.setMarker(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_location_pin
                )
            )
            items.add(overlayItem)
            val overlay = ItemizedOverlayWithFocus(
                items,
                object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                    override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                        TransitionManager.beginDelayedTransition(
                            binding.mapLayout,
                            AutoTransition()
                        )
                        if (binding.cardParking.visibility == View.VISIBLE) {
                            binding.cardParking.visibility = View.GONE
                            binding.LayoutInfo.visibility = View.GONE
                        } else {
                            binding.cardParking.visibility = View.VISIBLE
                            binding.LayoutInfo.visibility = View.VISIBLE
                        }
                        mapViewModel.setParking(item!!.title.toLong())

                        return true
                    }

                    override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {

                        return false
                    }
                },
                requireContext()
            )
            map.overlays.add(overlay)

            setRoad(GeoPoint(parkings[0].lat, parkings[0].lon))
        }
        mapViewModel.parkingPulsado.observe(requireActivity()) {
            binding.tvDireccionMap.text = (if (!it.direccion?.calle.isNullOrEmpty()) {
                it.direccion?.calle + " " + it.direccion?.numero
                if (!it.direccion?.numero.isNullOrEmpty()) {
                    it.direccion?.calle + " " + it.direccion?.numero
                } else {
                    it.direccion?.calle
                }
            } else {
                " "
            }).toString()
            binding.tvCapacidadMap.text = it.capacidad.toString()
        }

        binding.buttonClose.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                binding.mapLayout,
                AutoTransition()
            )
            binding.cardParking.visibility = View.GONE
        }

        binding.floatingActionButton.setOnClickListener {
            TransitionManager.beginDelayedTransition(
                binding.mapLayout,
                AutoTransition()
            )
            if (binding.leyendaCard.visibility == View.VISIBLE) {
                binding.leyendaCard.visibility = View.GONE
            } else {
                binding.leyendaCard.visibility = View.VISIBLE
            }
        }

        binding.buttonIrMap.setOnClickListener {
            findNavController().navigate(
                MapFragmentDirections.actionMapFragmentToInformacionFragment(
                    mapViewModel.parkingPulsado.value!!
                )
            )
        }

        return binding.root
    }

    /*
     * Establece la ruta al aparcamiento
     */
    private fun setRoad(point: GeoPoint) {
        val wayPoints = arrayListOf<GeoPoint>()
        val localizacionActual = getLastKnownLocation()
        wayPoints.add(GeoPoint(localizacionActual!!.latitude, localizacionActual.longitude))
        wayPoints.add(point)
        mapViewModel.setRoad(wayPoints, map, requireContext())
    }

    /*
 * Recupera la última localización concida del usuaria y devuelve
 */
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        val mLocationManager =
            requireContext().getSystemService(LOCATION_SERVICE) as LocationManager?
        val providers = mLocationManager!!.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                bestLocation = l
            }
        }
        return bestLocation
    }

    /*
     * Realiza la configuración del mapa:
     *  - establece el zoom
     *  - activa el escalado de los tiles
     *  - desactiva los botones del zoom
     *  - activa el multitouch
     *  - activa la visualización del la ubicación del usuario
     */
    private fun configureMap() {
        setZoomMap()
//        val tileSource = XYTileSource(
//            "cycle", 0, 20, 256, ".png", arrayOf(
//                "https://a.tile-cyclosm.openstreetmap.fr/[cyclosm|cyclosm-lite]/0,0,0.png"
//            )
//        )
//        Log.i("Pro", (map.tileProvider.tileSource.toString() + map.tileProvider.tileCache.size))
//        mapColorConfigure2()
//        map.setTileSource(tileSource)
//        Log.i("Pro", (map.tileProvider.tileSource.toString() + map.tileProvider.tileCache.size))

        map.isTilesScaledToDpi = true
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.setMultiTouchControls(true)

        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)
        map.invalidate()

    }

    /*
     * Establece el zoom y el centrado del mapa
     * según si el usuario ha navegado desde un aparcamiento
     * específico o no
     */
    private fun setZoomMap() {
        val mapController = map.controller
        if (parkings.isEmpty()) {
            mapController.setZoom(17.0)
            val startPoint =
                GeoPoint(getLastKnownLocation()!!.latitude, getLastKnownLocation()!!.longitude)
            mapController.setCenter(startPoint)
        } else {
            mapController.setZoom(18.0)
            val startPoint = GeoPoint(parkings[0].lat, parkings[0].lon)
            mapController.setCenter(startPoint)
        }
    }

    private fun mapColorConfigure2() {
        val matrixA = ColorMatrix()
        matrixA.setSaturation(0.3f)
        val matrixB = ColorMatrix()
        matrixB.setScale(1.12f, 1.13f, 1.13f, 1.0f)
        matrixA.setConcat(matrixB, matrixA)
        val filter = ColorMatrixColorFilter(matrixA)
        map.overlayManager.tilesOverlay.setColorFilter(filter)
    }

    private fun mapColorConfigure() {
        val inverseMatrix = ColorMatrix(
            floatArrayOf(
                -1.0f, 0.0f, 0.0f, 0.0f, 255f,
                0.0f, -1.0f, 0.0f, 0.0f, 255f,
                0.0f, 0.0f, -1.0f, 0.0f, 255f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
        val destinationColor = Color.parseColor("#FF2A2A2A")
        val lr = (255.0f - Color.red(destinationColor)) / 255.0f
        val lg = (255.0f - Color.green(destinationColor)) / 255.0f
        val lb = (255.0f - Color.blue(destinationColor)) / 255.0f
        val grayscaleMatrix = ColorMatrix(
            floatArrayOf(
                lr, lg, lb, 0f, 0f,
                lr, lg, lb, 0f, 0f,
                lr, lg, lb, 0f, 0f,
                0f, 0f, 0f, 0f, 255f,
            )
        )
        grayscaleMatrix.preConcat(inverseMatrix)
        val dr = Color.red(destinationColor)
        val dg = Color.green(destinationColor)
        val db = Color.blue(destinationColor)
        val drf = dr / 255f
        val dgf = dg / 255f
        val dbf = db / 255f
        val tintMatrix = ColorMatrix(
            floatArrayOf(
                drf, 0f, 0f, 0f, 0f,
                0f, dgf, 0f, 0f, 0f,
                0f, 0f, dbf, 0f, 0f,
                0f, 0f, 0f, 1f, 0f,
            )
        )
        tintMatrix.preConcat(grayscaleMatrix)
        val lDestianation = drf * lr + dgf * lg + dbf * lb
        val scale = 1f - lDestianation
        val translate = 1 - scale * 0.5f
        val scaleMatrix = ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, dr * translate,
                0f, scale, 0f, 0f, dg * translate,
                0f, 0f, scale, 0f, db * translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
        scaleMatrix.preConcat(tintMatrix)
        val filter = ColorMatrixColorFilter(scaleMatrix)
        map.overlayManager.tilesOverlay.setColorFilter(filter)
    }

    override fun onResume() {
        super.onResume()
        MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map).onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
        MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map).onPause()
    }

    private fun setupOsmdroid() {
        getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

}