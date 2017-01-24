package com.example.mytrack;

public class RilevazioneTracciato {

	int tempo;
	double lat, longi, vibr, lum, rumore;

	//oggetto che viene creato per ogni rilevazione
	public RilevazioneTracciato(int temp, double lati, double lon, double lu,
			double rum, double vibra) {

		vibr = vibra;
		tempo = temp;
		lat = lati;
		longi = lon;
		lum = lu;
		rumore = rum;
	}

}
