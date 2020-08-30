package aop_pruefung;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class Spiel {
	
	private File actFile;
	private Map<String, String[]> kategorie = new HashMap<>();
	private Map<Integer, ArrayList<Integer>> history = new HashMap<>();
	private int actKat;
	private String actFrage ="";
	private Vector<File> dateien = new Vector<File>();
	private String[] keys;
	private JComboBox jcbPopup;
	
	public Spiel(Vector<File> dateien) {
		this.dateien = dateien;
		this.jcbPopup = new JComboBox<File>(dateien);
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
	
	/**
	 * Öffnet ein Fenster, in dem Spieler {@code i} eine Kategorie auswaehlen soll.
	 * @param i Spielernummer
	 * @throws StopGameException 
	 */
	public String selectCat(int i) throws StopGameException {
		String cat ="";
		Boolean approve = false;
		while(!approve) {
			if(JOptionPane.showConfirmDialog( null, jcbPopup, "Spieler " + i + ": Bitte waehle eine Kategorie (\"Nein\" beendet das Spiel)", JOptionPane.OK_OPTION) == JOptionPane.OK_OPTION) {
				actKat = jcbPopup.getSelectedIndex();
				actFile = dateien.elementAt(actKat);
				kategorie.clear();
				readFile(actFile);
				if(kategorie.keySet().size()>2) {
					if(history.keySet().size()>=dateien.size()) {
						history.clear();
					}
					if(!history.containsKey(actKat)) {
						history.put(actKat, new ArrayList<Integer>());
					}
					cat = actFile.getName().replace(".txt", "");
					keys = kategorie.keySet().toArray(new String[kategorie.size()]);
					approve = true;
				}
				else {
					JOptionPane.showMessageDialog(null, "Die gewaehlte Kategorie ist leer oder beinhaltet zu wenige Fragen. Waehle erneut.");
				}
			}
			else {
				throw new StopGameException("Keine Kategorie gewaehlt!");
			}
		}
		return cat;
	}
	
	//Getter und Setter
	public String[] getArr() {
		String[] array = new String[dateien.size()];
		for(int i = 0; i< dateien.size(); i++) {
			array[i] = dateien.elementAt(i).getName().replace(".txt", "");		
		}
		return array;
	}
	
	public int nextCat() {
		int z;
		Random random = new Random();
		if(history.get(actKat).size()<kategorie.keySet().size()) { // wenn noch ungenutzte Fragen uebrig
			do{
				z = random.nextInt(keys.length);
			}while(history.get(actKat).contains(z));
		}
		else {
			int temp = history.get(actKat).get(history.get(actKat).size()-1); //merke den Index der zuletzt gestellten Frage
			history.put(actKat, new ArrayList<Integer>());
			history.get(actKat).add(temp); 
			do{
				z = random.nextInt(keys.length);
			}while(z == temp);
		}
		return z;
	}
	
	public String getAntwort(int frage, int stelle) {
		return kategorie.get(keys[frage])[stelle];
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
