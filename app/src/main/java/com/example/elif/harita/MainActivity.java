package com.example.elif.harita;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

 public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private GoogleMap map;
    //Google play servis kütüphaneleri kullanılmak istendiğinde "Google Api Client" örneği tanımlanır.
    //Google API Client, Google Play servislerine ortak giriş noktası oluşturur ve
    // kullanıcı cihazıyla herbir Google servisine ağ bağlanısını yönetir.
    private GoogleApiClient mGoogleApiClient;
    private final LatLng mDefaultLocation = new LatLng(41.007651, 28.976956);
    private static final int DEFAULT_ZOOM = 15;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // Fused Location Provider tarafından en son bilinen yer olarak alınan lokasyon.
    private Location mLastKnownLocation;
    public CameraPosition mCameraPosition ;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fused Location Provider ve the Places API kullanmak için Play services client kurar .
        // Google Places API and the Fused Location Provider isteği için addApi() kullanır.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    /**
     * Google Play services client başarılı bir şekilde bağlandığında haritayı kurar.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /**
     * Hazır olduğunda haritayı çalıştırır.
     * Bu callback Harita kullanıma hazır olduğunda tetiklenir.
     */
    @Override
    public void onMapReady(GoogleMap mMap) {
        map = mMap;

        // Do other setup activities here too, as described elsewhere in this tutorial.

        // Lokasyonum katmanını ve ilgili controlü açar .
        updateLocationUI();

        // Hali hazır konumu alır ve haritanın pozisyonunu belirler
        getDeviceLocation();


        LatLng Dernek = new LatLng(40.973087, 29.103677);
        mMap.addMarker(new MarkerOptions().position(Dernek).title("Dernek"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Dernek));
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }

    /*
     *Lokasyon izni isteği. Aracın lokasyonunu alabiliriz. İzin isteğinin sonucu callback ile ele alınıyor.
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            map.setMyLocationEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }
    // Uygulama çalıştığına verilen izinler
// Kullanıcıya lokasyon izni verip veya vermemesi için fırsat tanır.
    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Cihazın en iyi ve en son konumunu alır. Enderde olsa bazı durumlarda boş olabilir.
         */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Haritanın kamera pozisyonunu cihazın şimdiki konumuna ayarlar.
        if (mCameraPosition != null) {
            map.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Şimdiki konum bilgileri boş. Default kullanma");
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
    // İzin isteğinin sonucunu ele alır
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // eğer istek iptal edilirse, sonuç dizisi boş olur.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
}