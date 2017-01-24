package com.example.mytrack;

import android.provider.BaseColumns;

public interface TableAttributes extends BaseColumns {

	String GPS_TABLE_NAME = "gps";

	String TRACCIATO_TABLE_NAME = "tracciato";

	String LUMINOSITA_TABLE_NAME = "luminosita";

	String RUMORE_TABLE_NAME = "rumore";

	String VIBRAZIONE_TABLE_NAME = "vibrazione";

	String IDTRACCIATO = "idtracciato";

	String LATITUDINE = "latitudine";

	String LONGITUDINE = "longitudine";

	String VALORE = "valore";

	String TEMPO = "tempo";

	String NOME = "nome";

	String DATA = "data";

	String[] GENERIC_COLUMNS = new String[] { IDTRACCIATO, VALORE, TEMPO };

	String[] GPS_COLUMNS = new String[] { IDTRACCIATO, LATITUDINE, LONGITUDINE,
			TEMPO };

	String[] TRACCIATO_COLUMNS = new String[] { _ID, NOME, DATA };

}
