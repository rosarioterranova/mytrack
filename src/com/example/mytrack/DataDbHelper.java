package com.example.mytrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DataDbHelper {
	//classe per la gestione del database
	private static final String DATABASE_NAME = "example.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_EXAMPLE = "tbl_example";
	private static final String FIELD_EXAMPLE_ID = "id";
	private static final String FIELD_EXAMPLE_NAME = "name";

	private Context context;
	private SQLiteDatabase db;
	private SQLiteStatement insertStmt;
	private OpenHelper openHelper;

	public DataDbHelper(Context context) {
		this.context = context;
		openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		openHelper.onUpgrade(this.db, 1, DATABASE_VERSION);
	}

	public void closeDb() {
		if (this.db.isOpen())
			this.db.close();
	}

	public ArrayList gettrack() {

		Cursor c = db.rawQuery("SELECT " + TableAttributes.NOME + ","
				+ TableAttributes.DATA + " FROM "
				+ TableAttributes.TRACCIATO_TABLE_NAME, null);
		ArrayList<String> results = new ArrayList<String>();
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String nome = c.getString(c
							.getColumnIndex(TableAttributes.NOME));
					String data = c.getString(c
							.getColumnIndex(TableAttributes.DATA));
					results.add("" + nome + ",  Rilevato: " + data);
				} while (c.moveToNext());
			}
		}
		return results;
	}

	public int getidfromname(String name) {

		Cursor c = db.rawQuery("SELECT " + TableAttributes._ID + ","
				+ TableAttributes.NOME + "," + TableAttributes.DATA + " FROM "
				+ TableAttributes.TRACCIATO_TABLE_NAME, null);

		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String nome = c.getString(c
							.getColumnIndex(TableAttributes.NOME));
					String data = c.getString(c
							.getColumnIndex(TableAttributes.DATA));
					if (name.equals("" + nome + ",  Rilevato: " + data))
						return c.getInt((c.getColumnIndex(TableAttributes._ID)));
				} while (c.moveToNext());
			}
		}
		return -1;
	}

	public int getid() {
		Cursor cursor = db.rawQuery("SELECT MAX(" + TableAttributes._ID
				+ ") AS max_id FROM " + TableAttributes.TRACCIATO_TABLE_NAME,
				null);
		int id = 0;
		if (cursor.moveToFirst()) {
			do {
				id = cursor.getInt(0);
			} while (cursor.moveToNext());
		}
		return id;
	}

	public void insertintable(int id, ArrayList<RilevazioneTracciato> t) {

		ArrayList<RilevazioneTracciato> t1 = t;
		for (int i = 0; i < t1.size(); i++) {
			insertrum(id, t1.get(i).rumore, t1.get(i).tempo);
			insertlum(id, t1.get(i).lum, t1.get(i).tempo);
			insertvibr(id, t1.get(i).vibr, t1.get(i).tempo);
			insertingps(id, t1.get(i).lat, t1.get(i).longi, t1.get(i).tempo);

		}

	}

	private static class OpenHelper extends SQLiteOpenHelper {
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TableAttributes.TRACCIATO_TABLE_NAME
					+ " (" + TableAttributes._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ TableAttributes.NOME + " TEXT NOT NULL,"
					+ TableAttributes.DATA + " TEXT NOT NULL)");
			db.execSQL("CREATE TABLE " + TableAttributes.LUMINOSITA_TABLE_NAME
					+ " (" + TableAttributes.IDTRACCIATO + " INTEGER NOT NULL,"
					+ TableAttributes.VALORE + " REAL NOT NULL,"
					+ TableAttributes.TEMPO
					+ " INTEGER NOT NULL, PRIMARY KEY ("
					+ TableAttributes.IDTRACCIATO + ", "
					+ TableAttributes.TEMPO + "))");
			db.execSQL("CREATE TABLE " + TableAttributes.RUMORE_TABLE_NAME
					+ " (" + TableAttributes.IDTRACCIATO + " INTEGER NOT NULL,"
					+ TableAttributes.VALORE + " REAL NOT NULL,"
					+ TableAttributes.TEMPO
					+ " INTEGER NOT NULL, PRIMARY KEY ("
					+ TableAttributes.IDTRACCIATO + ", "
					+ TableAttributes.TEMPO + "))");
			db.execSQL("CREATE TABLE " + TableAttributes.VIBRAZIONE_TABLE_NAME
					+ " (" + TableAttributes.IDTRACCIATO + " INTEGER NOT NULL,"
					+ TableAttributes.VALORE + " REAL NOT NULL,"
					+ TableAttributes.TEMPO
					+ " INTEGER NOT NULL, PRIMARY KEY ("
					+ TableAttributes.IDTRACCIATO + ", "
					+ TableAttributes.TEMPO + "))");
			db.execSQL("CREATE TABLE " + TableAttributes.GPS_TABLE_NAME + " ("
					+ TableAttributes.IDTRACCIATO + " INTEGER NOT NULL,"
					+ TableAttributes.LATITUDINE + " REAL NOT NULL,"
					+ TableAttributes.LONGITUDINE + " REAL NOT NULL,"
					+ TableAttributes.TEMPO
					+ " INTEGER NOT NULL, PRIMARY KEY ("
					+ TableAttributes.IDTRACCIATO + ", "
					+ TableAttributes.TEMPO + "))");

		}

		public void inserttracciato(SQLiteDatabase db, String nome, String data) {

			ContentValues v = new ContentValues();

			v.put(TableAttributes.NOME, nome);
			v.put(TableAttributes.DATA, data);

			db.insert(TableAttributes.TRACCIATO_TABLE_NAME, null, v);
		}

		public void insertrumore(SQLiteDatabase db, int idtracciato,
				double valore, int tempo) {

			ContentValues v = new ContentValues();

			v.put(TableAttributes.IDTRACCIATO, idtracciato);
			v.put(TableAttributes.VALORE, valore);
			v.put(TableAttributes.TEMPO, tempo);

			db.insert(TableAttributes.RUMORE_TABLE_NAME, null, v);

		}

		public void insertluminosita(SQLiteDatabase db, int idtracciato,
				double valore, int tempo) {

			ContentValues v = new ContentValues();

			v.put(TableAttributes.IDTRACCIATO, idtracciato);
			v.put(TableAttributes.VALORE, valore);
			v.put(TableAttributes.TEMPO, tempo);

			db.insert(TableAttributes.LUMINOSITA_TABLE_NAME, null, v);

		}

		public void insertvibrazione(SQLiteDatabase db, int idtracciato,
				double valore, int tempo) {

			ContentValues v = new ContentValues();

			v.put(TableAttributes.IDTRACCIATO, idtracciato);
			v.put(TableAttributes.VALORE, valore);
			v.put(TableAttributes.TEMPO, tempo);

			db.insert(TableAttributes.VIBRAZIONE_TABLE_NAME, null, v);

		}

		public void insertgps(SQLiteDatabase db, int idtracciato,
				double latitudine, double longitudine, int tempo) {

			ContentValues v = new ContentValues();

			v.put(TableAttributes.IDTRACCIATO, idtracciato);
			v.put(TableAttributes.LATITUDINE, latitudine);
			v.put(TableAttributes.LONGITUDINE, longitudine);
			v.put(TableAttributes.TEMPO, tempo);

			db.insert(TableAttributes.GPS_TABLE_NAME, null, v);

		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		}
	}

	public void inserttrack(String nome, String data) {
		openHelper.inserttracciato(db, nome, data);
	}

	public void insertrum(int idtracciato, double valore, int tempo) {
		openHelper.insertrumore(db, idtracciato, valore, tempo);
	}

	public void insertlum(int idtracciato, double valore, int tempo) {
		openHelper.insertluminosita(db, idtracciato, valore, tempo);
	}

	public void insertvibr(int idtracciato, double valore, int tempo) {
		openHelper.insertvibrazione(db, idtracciato, valore, tempo);
	}

	public void insertingps(int idtracciato, double latitudine,
			double longitudine, int tempo) {
		openHelper.insertgps(db, idtracciato, latitudine, longitudine, tempo);
	}

	public GraphViewData[] getarrayofrum(int id) {

		Cursor c = db.rawQuery("SELECT " + TableAttributes.VALORE + ","
				+ TableAttributes.TEMPO + " FROM "
				+ TableAttributes.RUMORE_TABLE_NAME + " WHERE " + id + " = "
				+ TableAttributes.IDTRACCIATO, null);
		ArrayList<GraphViewData> results = new ArrayList<GraphViewData>();
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					double tempo = c.getInt((c
							.getColumnIndex(TableAttributes.TEMPO)));
					double valore = c.getDouble(c
							.getColumnIndex(TableAttributes.VALORE));
					results.add(new GraphViewData(tempo, valore));
				} while (c.moveToNext());
			}
		}
		GraphViewData[] array = new GraphViewData[results.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = results.get(i);
		}
		return array;

	}
	
	public ArrayList<Double> getlistofrum(int id) {

		Cursor c = db.rawQuery("SELECT " + TableAttributes.VALORE + ","
				+ TableAttributes.TEMPO + " FROM "
				+ TableAttributes.RUMORE_TABLE_NAME + " WHERE " + id + " = "
				+ TableAttributes.IDTRACCIATO, null);
		ArrayList<Double> results = new ArrayList<Double>();
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					double valore = c.getDouble(c
							.getColumnIndex(TableAttributes.VALORE));
					results.add(valore);
				} while (c.moveToNext());
			}
		}
		return results;

	}

	public GraphViewData[] getarrayofvibr(int id) {

		Cursor c = db.rawQuery("SELECT " + TableAttributes.VALORE + ","
				+ TableAttributes.TEMPO + " FROM "
				+ TableAttributes.VIBRAZIONE_TABLE_NAME + " WHERE " + id
				+ " = " + TableAttributes.IDTRACCIATO, null);
		ArrayList<GraphViewData> results = new ArrayList<GraphViewData>();
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					double tempo = c.getInt((c
							.getColumnIndex(TableAttributes.TEMPO)));
					double valore = c.getDouble(c
							.getColumnIndex(TableAttributes.VALORE));
					results.add(new GraphViewData(tempo, valore));
				} while (c.moveToNext());
			}
		}
		GraphViewData[] array = new GraphViewData[results.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = results.get(i);
		}
		return array;

	}
	
	public ArrayList<Double> getlistofvibr(int id) {

		Cursor c = db.rawQuery("SELECT " + TableAttributes.VALORE + ","
				+ TableAttributes.TEMPO + " FROM "
				+ TableAttributes.VIBRAZIONE_TABLE_NAME + " WHERE " + id + " = "
				+ TableAttributes.IDTRACCIATO, null);
		ArrayList<Double> results = new ArrayList<Double>();
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					double valore = c.getDouble(c
							.getColumnIndex(TableAttributes.VALORE));
					results.add(valore);
				} while (c.moveToNext());
			}
		}
		return results;

	}

	public GraphViewData[] getarrayoflum(int id) {

		Cursor c = db.rawQuery("SELECT " + TableAttributes.VALORE + ","
				+ TableAttributes.TEMPO + " FROM "
				+ TableAttributes.LUMINOSITA_TABLE_NAME + " WHERE " + id
				+ " = " + TableAttributes.IDTRACCIATO, null);
		ArrayList<GraphViewData> results = new ArrayList<GraphViewData>();
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					double tempo = c.getInt((c
							.getColumnIndex(TableAttributes.TEMPO)));
					double valore = c.getDouble(c
							.getColumnIndex(TableAttributes.VALORE));
					results.add(new GraphViewData(tempo, valore));
				} while (c.moveToNext());
			}
		}
		GraphViewData[] array = new GraphViewData[results.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = results.get(i);
		}
		return array;

	}
	
	public ArrayList<Double> getlistoflum(int id) {

		Cursor c = db.rawQuery("SELECT " + TableAttributes.VALORE + ","
				+ TableAttributes.TEMPO + " FROM "
				+ TableAttributes.LUMINOSITA_TABLE_NAME + " WHERE " + id + " = "
				+ TableAttributes.IDTRACCIATO, null);
		ArrayList<Double> results = new ArrayList<Double>();
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					double valore = c.getDouble(c
							.getColumnIndex(TableAttributes.VALORE));
					results.add(valore);
				} while (c.moveToNext());
			}
		}
		return results;

	}
	
	public ArrayList<String> getlistoftime(int id) {

		Cursor c = db.rawQuery("SELECT " + TableAttributes.VALORE + ","
				+ TableAttributes.TEMPO + " FROM "
				+ TableAttributes.LUMINOSITA_TABLE_NAME + " WHERE " + id + " = "
				+ TableAttributes.IDTRACCIATO, null);
		ArrayList<String> results = new ArrayList<String>();
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String valore = c.getString(c
							.getColumnIndex(TableAttributes.TEMPO));
					results.add(valore);
				} while (c.moveToNext());
			}
		}
		return results;

	}

	public PolylineOptions getgpscord(int id) {

		Cursor c = db.rawQuery("SELECT " + TableAttributes.LATITUDINE + ","
				+ TableAttributes.LONGITUDINE + " FROM "
				+ TableAttributes.GPS_TABLE_NAME + " WHERE " + id + " = "
				+ TableAttributes.IDTRACCIATO, null);
		PolylineOptions p=new PolylineOptions();
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String latitudine = c.getString((c
							.getColumnIndex(TableAttributes.LATITUDINE)));
					String longitudine = c.getString(c
							.getColumnIndex(TableAttributes.LONGITUDINE));
					p.add(new LatLng(Double.parseDouble(latitudine), Double.parseDouble(longitudine))).geodesic(true);
				} while (c.moveToNext());
			}
		}
		return p;

	}
	
	public ArrayList<LatLng> getlatlngfromgps(int id) {

		Cursor c = db.rawQuery("SELECT " + TableAttributes.LATITUDINE + ","
				+ TableAttributes.LONGITUDINE + " FROM "
				+ TableAttributes.GPS_TABLE_NAME + " WHERE " + id + " = "
				+ TableAttributes.IDTRACCIATO, null);
		ArrayList<LatLng> result=new ArrayList<LatLng>();
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String latitudine = c.getString((c
							.getColumnIndex(TableAttributes.LATITUDINE)));
					String longitudine = c.getString(c
							.getColumnIndex(TableAttributes.LONGITUDINE));
					result.add(new LatLng(Double.parseDouble(latitudine),Double.parseDouble(longitudine)));
				} while (c.moveToNext());
			}
		}
		
		return result;

	}
	
	

	public void deletewithid(int id) {
		db.delete(TableAttributes.TRACCIATO_TABLE_NAME, id + " = "
				+ TableAttributes._ID, null);
		db.delete(TableAttributes.GPS_TABLE_NAME, id + " = "
				+ TableAttributes.IDTRACCIATO, null);
		db.delete(TableAttributes.LUMINOSITA_TABLE_NAME, id + " = "
				+ TableAttributes.IDTRACCIATO, null);
		db.delete(TableAttributes.VIBRAZIONE_TABLE_NAME, id + " = "
				+ TableAttributes.IDTRACCIATO, null);
		db.delete(TableAttributes.RUMORE_TABLE_NAME, id + " = "
				+ TableAttributes.IDTRACCIATO, null);

	}
}
