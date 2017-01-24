package com.example.mytrack;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//classe che visualizza la mappa
public class Mapview extends Activity implements OnMarkerClickListener {

	GoogleMap map;
	DataDbHelper dh;
	int id;

	ArrayList<LatLng> gps;
	ArrayList<Double> rum, vibr, lum;
	ArrayList<String> time;

	ArrayList<Marker> m = new ArrayList<Marker>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapview);

		Intent i = getIntent();

		// apertura database
		dh = new DataDbHelper(Mapview.this);

		id = i.getIntExtra("id", -1);

		// polilinea tracciato
		
		

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		
		//disegno del tracciato
		PolylineOptions p = dh.getgpscord(id);

		// listener click sui marker
		map.setOnMarkerClickListener(this);

		// array per ogni rilevazione
		gps = dh.getlatlngfromgps(id);
		rum = dh.getlistofrum(id);
		vibr = dh.getlistofvibr(id);
		lum = dh.getlistoflum(id);
		time = dh.getlistoftime(id);

		for (int a = 0; a < gps.size(); a++) {
			if (rum.get(a) > 70.0)
				m.add(map.addMarker(new MarkerOptions().position(gps.get(a))));
			else
				m.add(map.addMarker(new MarkerOptions()
						.position(gps.get(a))
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
		}

		// zoom sull'inizio del tracciato
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(gps.get(0), 21));

		Polyline po = map.addPolyline(p);

		po.setColor(Color.BLUE);
	}

	// gestione click marker
	@Override
	public boolean onMarkerClick(Marker arg0) {

		for (int i = 0; i < gps.size(); i++)
			if (m.get(i).getPosition().equals((arg0.getPosition())))
				new AlertDialog.Builder(Mapview.this)
						.setTitle("Dati Registrati")
						.setMessage(
								"Tempo:" + time.get(i) + "(s)" + "\nRumore:"
										+ rum.get(i) + "\n" + "Vibrazione:"
										+ vibr.get(i) + "\nLuminosità:"
										+ lum.get(i))
						.setNegativeButton("ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {

									}
								}).show();

		return false;
	}
}
