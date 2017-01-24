package com.example.mytrack;

import java.util.Date;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//classe per l'activity dei grafici
public class Graph extends Activity {

	private int id;
	DataDbHelper dh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		dh = new DataDbHelper(Graph.this);
		TextView t = (TextView) findViewById(R.id.title);
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		id = intent.getIntExtra("id", -1);
		t.setText(title);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		GraphViewData[] array = dh.getarrayofrum(id);

		GraphViewSeries rumoregraph = new GraphViewSeries(array);
		GraphView graphView = new LineGraphView(this // context
				, "Rumore" // heading

		);
		graphView.addSeries(rumoregraph); // data

		LinearLayout layout = (LinearLayout) findViewById(R.id.rumlay);
		layout.addView(graphView);

		// grafico vibrazione
		GraphViewData[] vibrarray = dh.getarrayofvibr(id);
		GraphViewSeries vibrgraph = new GraphViewSeries(vibrarray);
		GraphView vibrView = new LineGraphView(this // context
				, "Vibrazione" // heading

		);
		vibrView.addSeries(vibrgraph); // data

		LinearLayout vibrlay = (LinearLayout) findViewById(R.id.vibrlay);
		vibrlay.addView(vibrView);

		// grafico luminosità
		GraphViewData[] lumarray = dh.getarrayoflum(id);
		GraphViewSeries lumgraph = new GraphViewSeries(lumarray);
		GraphView lumView = new LineGraphView(this // context
				, "Luminosita'" // heading

		);
		lumView.addSeries(lumgraph);

		LinearLayout lumlay = (LinearLayout) findViewById(R.id.lumlay);
		lumlay.addView(lumView);
	}

	public void vediMappa(View v) {
		Intent i=new Intent(Graph.this, Mapview.class);
		i.putExtra("id", id);
		startActivity(i);
	}
	
	//gestione del tasto back
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}

	//
	public void cancellaTrack(View v) {
		
		new AlertDialog.Builder(Graph.this)
		.setTitle("Cancella")
		.setMessage("Vuoi cancellare la rilevazione?")
		.setNegativeButton("Si", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dh.deletewithid(id);
				finish();
				Toast.makeText(getApplicationContext(),"Cancellazione effettuata con successo",Toast.LENGTH_SHORT).show();
			}
		})
		.setPositiveButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		}).show();
		
		
		
	}
}