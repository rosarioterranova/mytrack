package com.example.mytrack;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//classe per l'activity della lista di tracciati
public class Tracklist extends Activity {

	private ArrayList<String> list;
	private ArrayAdapter<String> adapter;
	DataDbHelper dh;
	int id;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracklist);

		dh = new DataDbHelper(Tracklist.this);

		list = dh.gettrack();
		final ListView mylist = (ListView) findViewById(R.id.listView1);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);

		mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				final String titoloriga = (arg0.getItemAtPosition(arg2))
						.toString();

				Bundle b = new Bundle();
				Intent intent = new Intent(Tracklist.this, Graph.class);
				int id = dh.getidfromname(titoloriga);
				b.putInt("id", id);
				b.putString("title", titoloriga);
				intent.putExtras(b);
				startActivity(intent);

			}
		});

		mylist.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dh.closeDb();

	}
	

	public void onResume() {
		super.onResume();
			ArrayList<String> lista = dh.gettrack();
			list.clear();
			list.addAll(lista);

			adapter.notifyDataSetChanged();
		
	}
}
