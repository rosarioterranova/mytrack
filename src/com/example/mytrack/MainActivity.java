package com.example.mytrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void vaiRilevazione(View v) {
		Intent intent = new Intent(this, Rilevazione.class);
		startActivity(intent);
	}

	public void vaiInfo(View v) {
		Intent intent = new Intent(this, Info.class);
		startActivity(intent);
	}

	public void vaiTracciato(View v) {
		Intent intent = new Intent(this, Tracklist.class);
		startActivity(intent);
	}
}
