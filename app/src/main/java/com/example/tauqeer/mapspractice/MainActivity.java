package com.example.tauqeer.mapspractice;

import android.app.Dialog;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    DatabaseHelper myDb = new DatabaseHelper(MainActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        if (googleServicesAvailable())
        {
            Toast.makeText(this,"Perfect",Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
            initMap();
        }
        else {
            // No google Map
        }
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragmen);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS)
        {
            return true;
        }else if (api.isUserResolvableError(isAvailable))
        {
            Dialog dialog = api.getErrorDialog(this,isAvailable,0);
            dialog.show();
        }
        else {
            Toast.makeText(this,"Can't Connect to Play Service",Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        //goToLocation(32.1452177,74.1193106);
        //goToLocationZoom(32.1452177,74.1193106,10);
    }

    Marker marker;
    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,zoom);
        mGoogleMap.moveCamera(update);

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Log.v("latitude is", String.valueOf(latLng.latitude));
                Log.v("longitude is", String.valueOf(latLng.longitude));

                Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(latLng.latitude,latLng.longitude,1);
                    Log.v("List is ", String.valueOf(list));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = list.get(0);
                Log.v("Full Address is", String.valueOf(address));
                String address1 = address.getAddressLine(0);
                Log.v("Address is", String.valueOf(address1));
                String locality = address.getLocality();
                Log.v("City is", String.valueOf(locality));

                if(marker != null)
                {
                    marker.remove();
                }

                MarkerOptions options = new MarkerOptions()
                                            .title(locality)
                                            .position(latLng)
                                            .snippet("Selected");
                marker=mGoogleMap.addMarker(options);
                /////////insert data/////////////////

                boolean data = myDb.insertData(String.valueOf(latLng.latitude),String.valueOf(latLng.longitude),String.valueOf(address1),String.valueOf(locality));
                if(data == true)
                    Toast.makeText(MainActivity.this,"Data Saved",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this,"Data not Saved",Toast.LENGTH_SHORT);

            }
        });
    }
    public void show(View view) {

        Cursor raw = myDb.getAllData();
        if(raw.getCount()==0) {
            showMessage("Error", "Nothing found");
            return;
        }
        else
        {
            StringBuffer buffer = new StringBuffer();

            Geocoder gc = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> list = null;



            while (raw.moveToNext())
            {
                buffer.append("ID : "+raw.getString(0)+"\n");
                buffer.append("Latitude : "+raw.getString(1)+"\n");
                buffer.append("Longitude : "+raw.getString(2)+"\n");
                buffer.append("Address : "+raw.getString(3)+"\n");
                buffer.append("City : "+raw.getString(4)+"\n");

                try {
                    list = gc.getFromLocation(Double.parseDouble(raw.getString(1)),Double.parseDouble (raw.getString(2)),1);
                    Log.v("List is ", String.valueOf(list));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                String locality = raw.getString(4);

                MarkerOptions options = new MarkerOptions()
                        .title(locality)
                        .position(new LatLng(Double.parseDouble(raw.getString(1)),Double.parseDouble (raw.getString(2))))
                        .snippet("Selected");
                marker=mGoogleMap.addMarker(options);
            }
            //showMessage("Data",buffer.toString());
        }

    }
    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mGoogleMap.moveCamera(update);
    }

    public void geoLocate(View view) throws IOException {

        EditText edt = (EditText)findViewById(R.id.edtPlace);
        String location = edt.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location,1);
        Address address = list.get(0);
        String locality = address.getLocality();

        Toast.makeText(this,locality,Toast.LENGTH_LONG).show();

        double lat = address.getLatitude();
        double lng = address.getLongitude();
        goToLocationZoom(lat,lng,10);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.itemNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.itemNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.itemSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.itemTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.itemHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void clear(View view) {
        Integer deleteRows = myDb.deleteData();
        if(deleteRows>0) {
            mGoogleMap.clear();
            Toast.makeText(MainActivity.this, "Data Deleted", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(MainActivity.this,"Data not Deleted",Toast.LENGTH_SHORT).show();
    }
}
