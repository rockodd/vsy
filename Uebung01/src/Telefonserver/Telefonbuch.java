package Telefonserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

public class Telefonbuch {
	
	static Vector<String> ausgabeName = new Vector<String>();
	static Vector<String> ausgabeNummer = new Vector<String>();
	
	
	
	// private Thread Klasse die die Suche realisiert
	class SuchThread extends Thread{
		
		String name;
		Integer number;

		// konstruktor wird mit Such Objekt aufgerufen und in seine Instanceof
		// Klasse zur�ck gecastet
		SuchThread(Object input) {
			if (input instanceof Integer)
				this.number = (Integer) input;
			if (input instanceof String)
				this.name = (String) input;
		}

		// thread run methode
		public void run() {
			try {
				// liest per Bufferedreader die txt ein und
				// �berpr�ft 
				BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\User\\workspaces\\VerteilteSysteme\\Uebung01\\telefonb.txt"));
				String zeile = null;
				if (name == null) {
					boolean found = false;
					while ((zeile = in.readLine()) != null) {
						if (zeile.contains(number.toString())) {
							System.out.println("A");
							ausgabeNummer.add(zeile);
							found = true;
						}
					}
					if (!found) {
						ausgabeNummer.add("Die Suche nach " + number.toString()
								+ " war erfolglos!");
					}
				} else {
					boolean found = false;
					while ((zeile = in.readLine()) != null) {
						if (zeile.toLowerCase().contains(name.toLowerCase())) {
							System.out.println("B");
							ausgabeName.add(zeile);
							found = true;
						}
					}
					if (!found) {
						ausgabeName.add("Die Suche nach " + name.toLowerCase()
								+ " war erfolglos!");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
