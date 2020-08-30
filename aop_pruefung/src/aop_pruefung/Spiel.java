package aop_pruefung;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

public class Spiel {
	
	private File actFile;
	private Map<String, String[]> kategorie = new HashMap<>();
	private Map<Integer, ArrayList<Integer>> history = new HashMap<>();
	private int actKat;
	private String actFrage ="";
	private Vector<File> dateien = new Vector<File>();
	private String[] keys;
	
	public Spiel(Vector<File> dateien) {
		this.dateien = dateien;
	}
	
	/**
	 * Liest die Datei {@code datei} ein und gibt Erfolg/Misserfolg zurueck
	 * 
	 * @see EditorGUI
	 * @param datei einzulesende Datei
	 * @return Boolean: Erfolg/Misserfolg des Einlesens
	 */
	public boolean readFile(File datei) {		
	    try {
			Scanner scanner = new Scanner(datei);
			int i = 1;
			String frage = "";
			String[] antworten = new String[5];
			while(scanner.hasNextLine()) {
				if(i == 1) {
					frage = scanner.nextLine();
				}
				else if (i%7 == 0) {
					scanner.nextLine();
				}
				else if( (i-1)%7== 0) {
					kategorie.put(frage, antworten);
					antworten = new String[5];
					frage = scanner.nextLine();
				}
				else {
					antworten[(i%7)-2] = scanner.nextLine();
				}
				i++;
			}
			if(i!=1)
				kategorie.put(frage, antworten);
			
			scanner.close();
			return true;
			
		}
		catch(FileNotFoundException|NullPointerException e){
			return false;
		}
	}
	public String[] getArr() {
		String[] array = new String[dateien.size()];
		for(int i = 0; i< dateien.size(); i++) {
			array[i] = dateien.elementAt(i).getName().replace(".txt", "");		
		}
		return array;
	}
	
	public void clearKat() {
		kategorie.clear();
	}
	public String[] getActValues() {
		return kategorie.get(actFrage);
	}
	public void setActKat(int i) {
		actKat = i;
	}
	public int getActKat() {
		return actKat;
	}
	public void setActFile(File i) {
		actFile = i;
	}

	public Map<Integer, ArrayList<Integer>> getHistory() {
		return history;
	}

	public void setHistory(Map<Integer, ArrayList<Integer>> history) {
		this.history = history;
	}
	
	public String getActFrage() {
		return actFrage;
	}

	public void setActFrage(String actFrage) {
		this.actFrage = actFrage;
	}

	public Vector<File> getDateien() {
		return dateien;
	}

	public void setDateien(Vector<File> dateien) {
		this.dateien = dateien;
	}

	public File getActFile() {
		return actFile;
	}

	public Map<String, String[]> getKategorie() {
		return kategorie;
	}

	public void setKategorie(Map<String, String[]> kategorie) {
		this.kategorie = kategorie;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}
}
