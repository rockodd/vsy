package Telefonserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Main Klasse startet einzelne Threads zur Suche in einem externen Telefonbuch
 * 
 * @className Main
 * @author Robert Ullmann
 * @date 10.05.2016
 */

public class Main {
	static ArrayList<String> ausgabeName = new ArrayList<String>();
	static ArrayList<String> ausgabeNummer = new ArrayList<String>();
	static CopyOnWriteArrayList<String> ausgabe = new CopyOnWriteArrayList<String>();


	private static Main telbuch = new Main();

	
	

	public static void main(String[] args) throws Exception {	
		while (true) {
			
			ausgabe.clear();
	
			
			ArrayList<Integer> nummer = new ArrayList<Integer>();
			String name = "";

			System.out
					.println("\n\n##############################################");
			System.out
					.println("Telefonserver gestartet \nNamen oder Nummer oder Namen und Nummer eingeben, startet die Suche.");
			System.out.println("EXIT beendet das Program. \n");

			// Eingabe mit java.util.Scanner einlesen
			Scanner sc = new Scanner(System.in);
			String[] eingabe = sc.nextLine().trim().split(" ");

			// �berpr�ft auf eine leere Eingabe
			if (eingabe.length == 0 || eingabe[0].equals("")) {
				System.out.println("Leere Eingaben sind Ungültig!");
				continue;
			}

			// �berpr�ft auf 'exit'
			if (eingabe[0].equals("EXIT")) {
				System.out.println("Programm wurde beendet");
				System.exit(0);
			}

			// Schleife untersucht String Array
			for (int i = 0; i < eingabe.length; i++) {
				if (eingabe[i].matches("\\d+")) {
					nummer.add(Integer.parseInt(eingabe[i]));
				} else if (eingabe[i].matches("\\D+")) {
					name += eingabe[i].trim() + " ";
				} else {
					System.out.println(" Eingabe ung�ltig!");
				}
			}

			// wenn eine Nummer und ein Name eingegeben wird,
			// dann werden zwei Nebenl�ufige Threads gestartet.
			if (!name.equals("") && nummer.size() != 0) {
				Thread thread1 = telbuch.new SuchThread(name.trim());
				Thread thread2 = telbuch.new SuchThread(nummer.get(0));
				thread1.start();
				thread2.start();
				thread1.join();
				thread2.join();
			} else {
				// wurde nur eine nummer gesucht dann startet die suche in einem
				// neuen thread
				for (int i = 0; i < nummer.size(); i++) {
					Thread thread = telbuch.new SuchThread(nummer.get(i));
					thread.start();
					thread.join();
				}
				// wurde nur ein name gesucht dann startet die suche in einem
				// neuen thread
				if (!name.equals("")) {
					Thread thread = telbuch.new SuchThread(name.trim());
					thread.start();
					thread.join();
				}

				// gibt die Suchergebnisse aus
				for (String e : telbuch.ausgabeName) {
					ausgabe.add(e);
					//System.out.println(e);
				}
				telbuch.ausgabeName.clear();

				for (String e : telbuch.ausgabeNummer) {
					//System.out.println(e);
					ausgabe.add(e);
				}
				telbuch.ausgabeNummer.clear();
			}
			
			for (String e : ausgabe) {
			System.out.println(e);
			
			}
		}
		
		}
	

	/**
	 * private Thread Klasse die die Suche realisiert
	 * 
	 * @className SuchThread
	 * @author Robert Ullmann
	 * @date 10.05.2016
	 */
			
			
			// private Thread Klasse die die Suche realisiert
			public class SuchThread extends Thread{

				
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
						BufferedReader in = new BufferedReader(new FileReader("/home/BEUTH/s59322/git/vsy/Uebung01/telefonb.txt"));
						String zeile = null;
						
						if (name == null) {
							boolean found = false;
							while ((zeile = in.readLine()) != null) {
								
									
								String[] eingabenummer = zeile.trim().split("\t");
								//if (zeile.contains(number.toString())) {
								if (eingabenummer[1].equals(number.toString())) {
									System.out.println("A");
									ausgabe.add(zeile);
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
								
									
								String[] eingabename = zeile.trim().split("\t");
								if (eingabename[0].toLowerCase().equals(name.toLowerCase())) {
									System.out.println("B");
									ausgabe.add(zeile);
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

			// debug
			// System.out.println(eingabe[0]);
		}



