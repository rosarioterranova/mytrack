package com.example.mytrack;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.util.FloatMath;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.provider.Settings.SettingNotFoundException;

//clase per l'activity di rilevazione
public class Rilevazione extends Activity implements SensorEventListener {
	SoundMeter sound;
	int x = 1; // variabile per il timerTMP
	boolean fineThread = false;
	int secondi = 10; // variabile per la form secondi
	Location loc;
	boolean actionsetted = false;
	// accelerometro
	private SensorManager sensorMan;
	private Sensor accelerometer;
	private float[] mGravity;
	private float mAccel;
	private float mAccelCurrent;
	private float mAccelLast;
	private float accellval = 0;
	// variabile rumore
	private double rumore;
	private float valoreLum = 0;
	private double latitudine;
	private double longitudine;
	public DataDbHelper dh;
	// variabile per il nome tracciato
	private EditText nometracciato;
	private int timetrack = 0;
	// lista da inserire
	private ArrayList<RilevazioneTracciato> lista;
	// oggetto per il timestamp
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss-dd/MM/yyyy");
	Button play;
	Button stop;
	private Sensor lightsensor;
	Context contesto = this;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rilevazione);

		// Creatore DB
		dh = new DataDbHelper(Rilevazione.this);

		// Creatore audio
		sound = new SoundMeter();
		sound.start();

		// Creatore Luminosità non necessario

		// Creatore GPS
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				mlocListener);

		// Creatore vibrazione
		sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mAccel = 0.00f;
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;

		// sensore luminosita
		lightsensor = sensorMan.getDefaultSensor(Sensor.TYPE_LIGHT);

		// disabilita lo spegnimento dello schermo
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	// Classe per il GPS
	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc1) { // metodo che si attiva
														// ad ogni spostamento
														// anche millimetrico
			loc = loc1;
			if (fineThread == false) {

				// GET GPS
				latitudine = loc.getLatitude();
				longitudine = loc.getLongitude();

				// GET RUMORE
				rumore = sound.soundDb();

			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		// notifica quando si accende il gps
		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(getApplicationContext(), "GPS acceso",
					Toast.LENGTH_LONG).show();
		}

		// notifia se il gos p spento
		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(getApplicationContext(), "GPS spento",
					Toast.LENGTH_LONG).show();

		}
	}

	// Pressione del bottone Inizia
	public void iniziaRiv(View v) {

		// Avvisi
		TextView caric = (TextView) findViewById(R.id.caricamento);
		caric.setVisibility(View.GONE);
		TextView gpa = (TextView) findViewById(R.id.avvGPS);
		caric.setVisibility(View.GONE);
		TextView gpa1 = (TextView) findViewById(R.id.avvGPS);
		gpa1.setVisibility(View.VISIBLE);

		count = 0;

		// Abilita il bottone stop e disabilita inizio
		play = (Button) findViewById(R.id.iniziaButton);
		play.setEnabled(false);
		inizioAutomatico();
	}

	public void inizioAutomatico() {
		new CountDownTimer(5000, 1000) { // ogni 5 secondi

			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				if (latitudine == 0.0 && longitudine == 0.0) {
					inizioAutomatico();
				} else {
					stop = (Button) findViewById(R.id.stoButton);
					stop.setEnabled(true);
					lista = new ArrayList<RilevazioneTracciato>();
					nometracciato = new EditText(contesto);
					new AlertDialog.Builder(Rilevazione.this)
							.setOnCancelListener(
									new DialogInterface.OnCancelListener() {
										@Override
										public void onCancel(
												DialogInterface dialog) {
											stop.setEnabled(false);
											play.setEnabled(true);

										}
									})
							.setTitle("Nuova rilevazione iniziata")
							.setMessage("inserisci il nome del tracciato")
							.setView(nometracciato)
							.setNegativeButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											fineThread = false;
											Toast.makeText(
													getApplicationContext(),
													"Rilevazione iniziata",
													Toast.LENGTH_SHORT).show();
											TextView caric = (TextView) findViewById(R.id.caricamento);
											caric.setVisibility(View.VISIBLE);
											sound.start();
											setaction();

											// Lettura form secondi
											TextView TSecondi = (TextView) findViewById(R.id.formSecondi);
											String stringSecondi = TSecondi
													.getText().toString();
											if (stringSecondi.isEmpty()) {
											} else
												secondi = Integer
														.parseInt(stringSecondi);
										}
									})
							.setPositiveButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											play.setEnabled(true);
											stop.setEnabled(false);
										}
									}).show();
					return;
				}
			}
		}.start();
	}

	// Pressione del bottone Stop
	public void stopRiv(View v) {
		if (count != 0)
			new AlertDialog.Builder(Rilevazione.this)
					.setTitle("Termina")
					.setMessage("Vuoi salvare la rilevazione?")
					.setNegativeButton("Si",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									stop.setEnabled(false);
									play.setEnabled(true);
									fineThread = true;
									sound.stop();
									Toast.makeText(getApplicationContext(),
											"Rilevazione fermata",
											Toast.LENGTH_SHORT).show();
									TextView caric = (TextView) findViewById(R.id.caricamento);
									caric.setVisibility(View.INVISIBLE);
									String s = sdf.format(new Date());
									dh.inserttrack(nometracciato.getText()
											.toString(), s);
									int id = dh.getid();
									Toast.makeText(getApplicationContext(),
											"Codice rilevazione: " + id,
											Toast.LENGTH_LONG).show();
									dh.insertintable(id, lista);
									lista = null;
									nometracciato = null;
									timetrack = 0;
								}
							})
					.setPositiveButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									stop.setEnabled(false);
									play.setEnabled(true);
									lista = null;
									nometracciato = null;
									fineThread = true;
									sound.stop();
									Toast.makeText(getApplicationContext(),
											"Rilevazione fermata",
											Toast.LENGTH_SHORT).show();
									TextView caric = (TextView) findViewById(R.id.caricamento);
									caric.setVisibility(View.INVISIBLE);
									timetrack = 0;
								}
							}).show();
		else
			new AlertDialog.Builder(Rilevazione.this)
					.setTitle("Termina")
					.setMessage(
							"Non è stato rilevato nulla il percorso non verrà salvato!")
					.setPositiveButton("ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									stop.setEnabled(false);
									play.setEnabled(true);
									lista = null;
									nometracciato = null;
									fineThread = true;
									sound.stop();
									Toast.makeText(getApplicationContext(),
											"Rilevazione fermata",
											Toast.LENGTH_SHORT).show();
									TextView caric = (TextView) findViewById(R.id.caricamento);
									caric.setVisibility(View.INVISIBLE);
									timetrack = 0;
								}
							}).show();

	}

	// Metodi per la creazione del fragment
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_rilevazione,
					container, false);
			return rootView;
		}
	}

	// Metodi per l'accelerometro
	public void onResume() {
		super.onResume();
		sensorMan.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
		sensorMan.registerListener(this, lightsensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorMan.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	// intercetta l'evento del sensore
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			mGravity = event.values.clone();
			// Shake detection
			float x = mGravity[0];
			float y = mGravity[1];
			float z = mGravity[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = FloatMath.sqrt(x * x + y * y + z * z);
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta;
			if (mAccel > 10) {
				mAccel = 10;
			}
			if (mAccel < 1)
				mAccel = 1;

			accellval = mAccel;
		}
		if (event.sensor.getType() == Sensor.TYPE_LIGHT)
			valoreLum = event.values[0];

	}

	// Thread (setta un azione con un ritardo di dieci secondi di default)
	public void setaction() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {

				if (fineThread == false) {
					Toast.makeText(
							getApplicationContext(),
							"Rilevazione num. " + x + " : Latitudine = "
									+ latitudine + " Longitudine = "
									+ longitudine + " Rumore = " + rumore
									+ " Luminosità = " + valoreLum
									+ "Accelerometro:" + accellval,
							Toast.LENGTH_SHORT).show();
					lista.add(new RilevazioneTracciato(timetrack, latitudine,
							longitudine, valoreLum, rumore, accellval));
					count += 1;
					timetrack += secondi;
					x = x + 1;
					setaction();

				}
			}
		}, secondi * 1000);
	}

	// gestione del tasto back
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			stop = (Button) findViewById(R.id.stoButton);
			if (stop.isEnabled() == true) {
				stopRiv(stop);
			} else {
				super.onBackPressed();

			}
		}
		return super.onKeyDown(keyCode, event);
	}
}