package aop_pruefung;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 * Verwaltet die Daten ueber das Spiel, also alle Kategorien, den Verlauf von Kategorien und Fragen und die aktuellen Zustaende
 * 
 * @author Tom Sosedow
 *
 */
public class Spiel {
	
	private File actFile;
	private Map<String, String[]> kategorie = new HashMap<>();
	private Map<Integer, ArrayList<Integer>> history = new HashMap<>();
	private int actCat;
	private String actFrage ="";
	private Vector<File> dateien = new Vector<File>(); //Dateienverzeichnis
	private String[] keys;
	private JComboBox<File> jcbPopup;
	private Random random = new Random();
	
	public Spiel(Vector<File> dateien) {
		this.dateien = dateien;
		this.jcbPopup = new JComboBox<File>(dateien);
	}
	
	/**
	 * Liest die Datei {@code datei} ein und gibt Misserfolg zurueck, falls bspw. ein Fehler im Format auftritt.
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
					if(!scanner.nextLine().isBlank()) {
						scanner.close();
						return false; //Fehler im Format
					}
				}
				else if( (i-1)%7== 0) {
					if(!frage.isBlank()) {
						antworten[4] = conversion(antworten[4].replace(" ", ""));
						kategorie.put(frage, antworten);
					}
					antworten = new String[5];
					frage = scanner.nextLine();
				}
				else {
					antworten[(i%7)-2] = scanner.nextLine();
				}
				i++;
			}
			if(i!=1 && !frage.isBlank()) {
				antworten[4] = conversion(antworten[4]);
				kategorie.put(frage, antworten);
			}
			scanner.close();
			return true;
			
		}
		catch(NullPointerException|IOException e){
			return false;
		}
	}
	
	private String conversion(String a) throws IOException {
		String text = null;
		if(a.equals("1") || a.equals("A"))
			text = "A";
		else if(a.equals("2") || a.equals("B"))
			text = "B";
		else if(a.equals("3") || a.equals("C"))
			text = "C";
		else if(a.equals("4") || a.equals("D"))
			text = "D";
		
		if(text != null)
			return text;
		else
			throw new IOException("Falsche Kennzeichnung der richtigen Antworten");
	}
	
	/**
	 * Oeffnet ein Fenster, in dem Spieler {@code i} eine Kategorie auswaehlen soll.
	 * @param i Spielernummer
	 * @throws StopAppException 
	 */
	public String selectCat(int i) throws StopAppException {
		String cat ="";
		Boolean approve = false;
		while(!approve) {
			if(JOptionPane.showConfirmDialog( null, jcbPopup, "Spieler " + i + ": Bitte waehle eine Kategorie (\"Nein\" beendet das Spiel)", JOptionPane.OK_OPTION) == JOptionPane.OK_OPTION) {
				actCat = jcbPopup.getSelectedIndex();
				actFile = dateien.elementAt(actCat);
				kategorie.clear();
				readFile(actFile);
				if(kategorie.keySet().size()>2) {
					if(history.keySet().size()>=dateien.size()) {
						history.clear();
					}
					if(!history.containsKey(actCat)) {
						history.put(actCat, new ArrayList<Integer>());
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
				throw new StopAppException("Keine Kategorie gewaehlt!");
			}
		}
		return cat;
	}
	
	/**
	 * Der Bot waehlt eine Kategorie, die vorher noch nicht dran war. Falls schon alle verfuegbaren dran waren, wird das Merkblatt geloescht
	 * und die naechste Kategorie zum neuen merkblatt hinzugefuegt
	 */
	public String botChooseCat() {
		Boolean approve = false;
		String cat = "";
		while(!approve) {
			do {
				actCat = random.nextInt(dateien.size());
				actFile = dateien.elementAt(actCat);
			} while(history.containsKey(actCat) && history.keySet().size()<dateien.size());
			kategorie.clear();
			readFile(actFile);
			if(kategorie.keySet().size()>2) {
				if(history.keySet().size()>=dateien.size()) {
					history.clear();
				}
				if(!history.containsKey(actCat)) {
					history.put(actCat, new ArrayList<Integer>());
				}
				cat = actFile.getName().replace(".txt", "");
				keys = kategorie.keySet().toArray(new String[kategorie.size()]);
				approve = true;
			}
			else {
				history.put(actCat, new ArrayList<Integer>()); //verhindern einer Endlosschleife
			}
		}
		JOptionPane.showMessageDialog(null, "Der Bot hat die Kategorie " + cat + " gewaehlt!");
		return cat;
	}
	
	/**
	 * Waehlt eine neue, zufaellige Frage, die vorher noch nicht dran war, und speichert deren Index in den Verlauf der Kategorie.
	 * @return Index der Frage in der Fragenliste.
	 */
	public int nextQ() {
		int z;
		Random random = new Random();
		if(history.get(actCat).size()<kategorie.keySet().size()) { // wenn noch ungenutzte Fragen uebrig
			do{
				z = random.nextInt(keys.length);
			}while(history.get(actCat).contains(z));
		}
		else {
			int temp = history.get(actCat).get(history.get(actCat).size()-1); //merke den Index der zuletzt gestellten Frage
			history.put(actCat, new ArrayList<Integer>());
			history.get(actCat).add(temp); 
			do{
				z = random.nextInt(keys.length);
			}while(z == temp);
		}
		
		history.get(actCat).add(z);
		actFrage = keys[z];
		return z;
	}
	
	/**
	 * Leert die aktuelle Kategorie.
	 */
	public void clearCat() {
		kategorie.clear();
	}
	//Getter und Setter	
	//Setter fuer Label setzen den Text des Labels, nicht das gesamte Label
	
	/**
	 * Gibt die Antwort zu einer beliebigen Frage der aktuellen Kategorie aus.
	 * @param frage Frage zu welcher die Antworten gewuenscht sind.
	 * @param stelle Stelle der gewuenschten Antwort. 0-3 sind Antworten A-D, 4 ist der Buchstabe der Loesung
	 * @return Antwort zur Frage an der Stelle
	 */
	public String getAntwort(int frage, int stelle) {
		return kategorie.get(keys[frage])[stelle];
	}
	
	/**
	 * @return Die 4 Antwortmoeglichkeiten zur aktuellen Frage (Stellen 0-3) plus die richtige Antwort (Stelle 4) in einem String-Array
	 */
	public String[] getActValues() {
		return kategorie.get(actFrage);
	}
	
	/**
	 * @return Die aktuelle Frage
	 */
	public String getActFrage() {
		return actFrage;
	}
}
