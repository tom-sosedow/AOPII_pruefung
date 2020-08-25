package aop_pruefung;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ButtonGroup;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;

/**
 * Beherbergt das Fenster fuer den Singleplayermodus.
 * @author Tom Sosedow
 * 
 */
public class SingleplayerGUI extends JFrame {

	private JPanel contentPane, panel1, panel2, panel3;
	private JLabel lblCat, lblA1, lblA2, lblB1, lblB2, lblC1, lblC2, lblD1, lblD2, lblStatus1, lblStatus2, lblStatus, lblFrage1, lblFrage2, lblPunktestand, lblScore;
	private JRadioButton rdbtnA1, rdbtnA2, rdbtnB1, rdbtnB2, rdbtnC1, rdbtnC2, rdbtnD1, rdbtnD2;
	private JSplitPane splitPaneA1, splitPaneA2, splitPaneB1, splitPaneB2, splitPaneC1, splitPaneC2, splitPaneD1, splitPaneD2;
	private JButton btnAccept1;
	private GridBagLayout gbl_contentPane;
	private GridBagConstraints gbc_panel1, gbc_panel2, gbc_panel3;
	private ButtonGroup bg1, bg2;
	private File[] ls = null;
	private File pfad, actFile;
	private Map<String, String[]> kategorie = new HashMap<>(); //Beinhaltet alle Fragen, dazugehoerige Antworten und die richtige Antwort
	private Vector<File> dateien = new Vector<File>();
	private JComboBox<String> jcbPopup; 
	private JComboBox<String> jcbDiff;
	private String[] keys; //Beinhaltet alle Fragen der aktuellen Kategorie
	private Spieler spieler1, spieler2;
	private Semaphore bereit = new Semaphore(1, true);
	private Random random = new Random();
	private float diff;
	private String[] diffs = {"Leicht", "Mittel", "Schwer", "Dr. Kawashima"};
	private ArrayList<Integer> fragen = new ArrayList<Integer>(); //Indizes der Fragen, die in der derzeitigen Kategorie bereits dran kamen
	private ArrayList<Integer> history = new ArrayList<Integer>(); //Indizes der Kategorien, die in der aktuellen Partie bereits dran kamen
	private String actFrage ="";
	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	/**
	 * Initialisiert das Fenster und liest alle Dateien aus dem uebergebenen Ordner ein.
	 * 
	 * @param pfad Verzeichnis, in dem die Dateien liegen
	 */
	public SingleplayerGUI(File pfad) {
		this.pfad = pfad;
		
		ls = this.pfad.listFiles(new FileFilter() {
			public boolean accept(File f) {
					return f.isFile()&&f.getName().endsWith(".txt");}});
		
		if (ls != null && ls.length != 0) 
			for(int i = 0; i< ls.length; i++) 
				dateien.add(ls[i]);	
		
		initGUI();
		
	}
	
	/**
	 * Initialisiert das Fenster und nutzt dabei den uebergebenen Vektor (Files)
	 * @param dateien Vektor mit den Dateien
	 * @wbp.parser.constructor
	 */
	public SingleplayerGUI(Vector<File> files) {
		this.dateien = files;
		initGUI();
	}
	
	/**
	 * Loggt die Auswahl des Spielers ein (falls sie nicht leer ist) wenn der Spieler Bestaetigen drueckt.
	 * Die Antworten ausgewertet, ggf. ein Punkt vergeben und der Startzustand fuer die naechste Fragerunde hergestellt.
	 */
	private void accept(){
		if(spieler1.getAuswahl().equals("")) {
			lblStatus1.setText("Bitte waehle zuerst eine Antwort!");
			return;
		}
		if(spieler1.getAuswahl().equals(kategorie.get(actFrage)[4])) {
			spieler1.setPunkte(spieler1.getPunkte()+1);
			lblScore.setText(spieler1.getPunkte() + ":" + spieler2.getPunkte());
			lblStatus1.setText("<HTML><BODY BGCOLOR=#4EFF01>Richtig!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus1.setText("Richtig!");
		    }, 3, TimeUnit.SECONDS);
		}
		else {
			lblStatus1.setText("<HTML><BODY BGCOLOR=#FFCCCC>Leider falsch!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus1.setText("Leider falsch!");
		    }, 3, TimeUnit.SECONDS);
		}
		if(spieler2.getAuswahl().equals(kategorie.get(actFrage)[4])) {
			spieler2.setPunkte(spieler2.getPunkte()+1);
			lblScore.setText(spieler1.getPunkte() + ":" + spieler2.getPunkte());
			lblStatus2.setText("<HTML><BODY BGCOLOR=#4EFF01>Richtig!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus2.setText("Richtig!");
		    }, 3, TimeUnit.SECONDS);
		}
		else {
			lblStatus2.setText("<HTML><BODY BGCOLOR=#FFCCCC>Leider falsch!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus2.setText("Leider falsch!");
		    }, 3, TimeUnit.SECONDS);
		}
		spieler1.setAuswahl("");
		bereit.release();
	}
	
	/**
	 * Spielablauf wird hier durchgegangen entsprechend der Spielregeln. Startet dafuer einen neuen Thread damit mithilfe 
	 * von Semaphores auf Spielereingabe gewartet werden kann. Kategorieauswahl von Spieler 2 wird zufaellig getaetigt.
	 */
	public void spielen() {
		Runnable spielen = new Runnable() {
			@Override public void run() {
				try {
					int z;
					//3 Runden
					for(int a = 0; a < 3; a++) {
						selectCat(); //Kategorie waehlen
						keys = kategorie.keySet().toArray(new String[kategorie.size()]); //Fragenliste
						
						//erste Frage
						refreshQ();
						lblStatus.setText("");
						bg1.clearSelection();
						bg2.clearSelection();
						auswahlBot(diff);
						bereit.acquire();
						//2 Fragen 
						for(int i = 0; i<2; i++) {
							lblStatus.setText("Die Richtige Antwort ist " + kategorie.get(actFrage)[4] + "!");
							askQ();
							bereit.acquire();
						}
						lblStatus.setText("Die Richtige Antwort ist " + kategorie.get(actFrage)[4] + "!");
						
						//Bot waehlt Kategorie die vorher noch nicht dran war
						
						do {
							z = random.nextInt(dateien.size());
							actFile = dateien.elementAt(z);
						} while(history.contains(z) && !(history.size()>=dateien.size()));
						if(history.size()>=dateien.size()) {
							history.clear();
						}
						history.add(z);
						kategorie.clear();
						readFile(actFile); // Rueckgabewert entegen nehmen in while
						lblCat.setText("Kategorie: " + actFile.getName().replace(".txt", ""));
						keys = kategorie.keySet().toArray(new String[kategorie.size()]);
						fragen.clear();
						
						//1 Frage der neuen Kategorie
						askQ();
						bereit.acquire();
						//naechste 2 Fragen
						for(int i = 0; i<2; i++) {
							lblStatus.setText("Die Richtige Antwort ist " + kategorie.get(actFrage)[4] + "!");
							askQ();
							bereit.acquire();
						}
						lblStatus.setText("Die Richtige Antwort ist " + kategorie.get(actFrage)[4] + "!");
						TimeUnit.SECONDS.sleep(2);
						
					}
					
					z = 1;
					if(spieler1.getPunkte() > spieler2.getPunkte())
						z = JOptionPane.showConfirmDialog(getParent(), "<html>Spieler 1 gewinnt! Gutes Spiel!<br> Wenn du das fenster schliessen moechtest, druecke Ok.</html>", "Ergebnis:", JOptionPane.YES_NO_OPTION);
					else if(spieler1.getPunkte() < spieler2.getPunkte())
						z = JOptionPane.showConfirmDialog(getParent(), "<html>Spieler 2 gewinnt! Gutes Spiel!<br> Wenn du das fenster schliessen moechtest, druecke Ok.</html>", "Ergebnis:", JOptionPane.YES_NO_OPTION);
					else
						z = JOptionPane.showConfirmDialog(getParent(), "<html>Gleichstand! Was fuer ein Spiel!<br> Wenn du das fenster schliessen moechtest, druecke Ok.</html>", "Ergebnis:", JOptionPane.YES_NO_OPTION);
					
					switch(z) {
						case 0:
							dispose();
							break;
						default:
							rdbtnA1.setEnabled(false);
							rdbtnB1.setEnabled(false);
							rdbtnC1.setEnabled(false);
							rdbtnD1.setEnabled(false);
							btnAccept1.setEnabled(false);
							break;
					}
				} catch (InterruptedException|IllegalArgumentException|NullPointerException e) { 
					e.printStackTrace();
					JOptionPane.showMessageDialog(getParent(), "Ein Fehler ist aufgetreten", "Fehler", JOptionPane.ERROR_MESSAGE);
					dispose();
				}
			}
		};
		new Thread(spielen).start();
	}
	
	/**
	 * Stellt eine neue Frage und stellt den Wartezustand auf die Eingabe des Nutzers her. Der Bot waehlt seine Antwort.
	 */
	private void askQ() {
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lblStatus.setText("");
		refreshQ();
		bg1.clearSelection();
		bg2.clearSelection();
		auswahlBot(diff);
	}
	
	/**
	 * Öffnet ein fenster, in dem der Spieler eine Kategorie auswaehlen soll.
	 */
	private void selectCat() {
		jcbPopup.setSelectedIndex(0);
		JOptionPane.showMessageDialog( null, jcbPopup, "Bitte waehle eine Kategorie", JOptionPane.QUESTION_MESSAGE);
		if(jcbPopup.getSelectedIndex() != -1) {
			actFile = dateien.elementAt(jcbPopup.getSelectedIndex());
			lblCat.setText("Kategorie: " + actFile.getName().replace(".txt", ""));
			kategorie.clear();
			readFile(actFile);
			fragen.clear();
			history.add(jcbPopup.getSelectedIndex());
		}
		
	}
	
	/**
	 * Waehlt eine zufaellige naechste Frage (die noch nicht gestellt wurde) und zeigt 
	 * sie mit den Antwortmoeglichkeiten in allen entsprechenden Feldern an
	 * 
	 * @see MuliplayerGUI
	 */
	private void refreshQ() throws NullPointerException{
		int z;
		if(fragen.size()<kategorie.keySet().size()) {
			do{
			z = random.nextInt(keys.length);
			}while(fragen.contains(z));
		}
		else {
			fragen.clear();
			z = random.nextInt(keys.length);
		}
		
		fragen.add(z);
		actFrage = keys[z];
		lblFrage1.setText("<html><p>" + actFrage + "</p></html>");
		rdbtnA1.setText(kategorie.get(keys[z])[0]);
		rdbtnB1.setText(kategorie.get(keys[z])[1]);
		rdbtnC1.setText(kategorie.get(keys[z])[2]);
		rdbtnD1.setText(kategorie.get(keys[z])[3]);

		lblFrage2.setText("<html><p>" + actFrage + "</p></html>");
		rdbtnA2.setText(kategorie.get(keys[z])[0]);
		rdbtnB2.setText(kategorie.get(keys[z])[1]);
		rdbtnC2.setText(kategorie.get(keys[z])[2]);
		rdbtnD2.setText(kategorie.get(keys[z])[3]);
		
	}
	
	/**
	 * Bot waehlt entsprechend seiner Schwierigkeitsstufe eine richtige oder falsche Antwort
	 * @param i Schwierigkeit (0-3)
	 */
	private void auswahlBot(float i) {
		String rAntwort = kategorie.get(actFrage)[4];
		String[] ABCD = {"A", "B", "C", "D"};
		int temp = 0;
		float schranke = (i*0.2f)+0.2f;
		float z = random.nextFloat();
		
		// |0								<Schranke>							1|
		//  	->z->richtige Antw.			<  -  -  >		->z->falsche Antwort
		//wenn z vor der Schranke landet gibt der Bot die richtige Antwort ab, falls nicht eine zufaellige falsche.
		if(z<schranke) {
			switch(rAntwort) {
				case "A":
					rdbtnA2.setSelected(true);
					spieler2.setAuswahl("A");
					break;
				case "B":
					rdbtnB2.setSelected(true);
					spieler2.setAuswahl("B");
					break;
				case "C":
					rdbtnC2.setSelected(true);
					spieler2.setAuswahl("C");
					break;
				case "D":
					rdbtnD2.setSelected(true);
					spieler2.setAuswahl("D");
					break;
			}
		}
		else {
			temp = random.nextInt(4);
			while(rAntwort.equals(ABCD[temp])) {
				temp = random.nextInt(4);
			}
			switch(temp){
				case 0:
					rdbtnA2.setSelected(true);
					spieler2.setAuswahl("A");
					break;
				case 1:
					rdbtnB2.setSelected(true);
					spieler2.setAuswahl("B");
					break;
				case 2:
					rdbtnC2.setSelected(true);
					spieler2.setAuswahl("C");
					break;
				case 3:
					rdbtnD2.setSelected(true);
					spieler2.setAuswahl("D");
					break;
			}
		}
	}
	
	/**
	 * Initialisiert das rechte Panel fuer Spieler 1
	 * JLabel lblFrage1
	 * SplitPane[JLabel A1 | RadioButton rdbtnA1]
	 * SplitPane[JLabel B1 | RadioButton rdbtnB1]
	 * SplitPane[JLabel C1 | RadioButton rdbtnC1]
	 * SplitPane[JLabel D1 | RadioButton rdbtnD1]
	 * JButton btnAccept1
	 * JLabel lblStatus1
	 */
	private void initPanel1() {
		panel1 = new JPanel();
		gbc_panel1 = new GridBagConstraints();
		gbc_panel1.fill = GridBagConstraints.BOTH;
		gbc_panel1.gridx = 0;
		gbc_panel1.gridy = 0;
		gbc_panel1.weightx = 0.5;
		contentPane.add(panel1, gbc_panel1);
		panel1.setLayout(new GridLayout(7, 1, 0, 0));
		
		lblFrage1 = new JLabel("Frage:");
		panel1.add(lblFrage1);
		
		splitPaneA1 = new JSplitPane();
		panel1.add(splitPaneA1);
		
		lblA1 = new JLabel("A:");
		splitPaneA1.setLeftComponent(lblA1);
		
		rdbtnA1 = new JRadioButton("");
		rdbtnA1.addActionListener(e -> spieler1.setAuswahl("A"));
		splitPaneA1.setRightComponent(rdbtnA1);
		
		splitPaneB1 = new JSplitPane();
		panel1.add(splitPaneB1);
		
		lblB1 = new JLabel("B:");
		splitPaneB1.setLeftComponent(lblB1);
		
		rdbtnB1 = new JRadioButton("");
		rdbtnB1.addActionListener(e -> spieler1.setAuswahl("B"));
		splitPaneB1.setRightComponent(rdbtnB1);
		
		splitPaneC1 = new JSplitPane();
		panel1.add(splitPaneC1);
		
		lblC1 = new JLabel("C:");
		splitPaneC1.setLeftComponent(lblC1);
		
		rdbtnC1 = new JRadioButton("");
		rdbtnC1.addActionListener(e -> spieler1.setAuswahl("C"));
		splitPaneC1.setRightComponent(rdbtnC1);
		
		splitPaneD1 = new JSplitPane();
		panel1.add(splitPaneD1);
		
		lblD1 = new JLabel("D:");
		splitPaneD1.setLeftComponent(lblD1);
		
		rdbtnD1 = new JRadioButton("");
		splitPaneD1.setRightComponent(rdbtnD1);
		rdbtnD1.addActionListener(e -> spieler1.setAuswahl("D"));
		
		btnAccept1 = new JButton("Bestaetigen");
		btnAccept1.addActionListener(e-> accept());
		panel1.add(btnAccept1);
		
		lblStatus1 = new JLabel("");
		panel1.add(lblStatus1);
		
	}
	
	/**
	 * Initialisiert das mittlere Panel fuer Punktestand, Ausgaben, Ansagen und aktuelle gewaehlte Kategorie
	 */
	private void initPanel2(){
		panel2 = new JPanel();
		gbc_panel2 = new GridBagConstraints();
		gbc_panel2.fill = GridBagConstraints.BOTH;
		gbc_panel2.gridx = 1;
		gbc_panel2.gridy = 0;
		gbc_panel2.weightx = 0.1;
		contentPane.add(panel2, gbc_panel2);
		panel2.setLayout(new GridLayout(5, 1, 0, 0));
		
		lblCat = new JLabel("Gewaehlte Kategorie:");
		lblCat.setHorizontalAlignment(SwingConstants.CENTER);
		panel2.add(lblCat);
		
		lblPunktestand = new JLabel("Punktestand:");
		lblPunktestand.setHorizontalAlignment(SwingConstants.CENTER);
		panel2.add(lblPunktestand);
		
		lblScore = new JLabel("0:0");
		lblScore.setHorizontalAlignment(SwingConstants.CENTER);
		panel2.add(lblScore);
		
		lblStatus = new JLabel("");
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		panel2.add(lblStatus);
	}
	
	/**
	 * Initialisiert das rechte Panel fuer Spieler 2
	 * JLabel lblFrage2
	 * SplitPane[JLabel A2 | RadioButton rdbtnA2]
	 * SplitPane[JLabel B2 | RadioButton rdbtnB2]
	 * SplitPane[JLabel C2 | RadioButton rdbtnC2]
	 * SplitPane[JLabel D2 | RadioButton rdbtnD2]
	 * JLabel lblStatus2
	 */
	private void initPanel3() {
		panel3 = new JPanel();
		gbc_panel3 = new GridBagConstraints();
		gbc_panel3.fill = GridBagConstraints.BOTH;
		gbc_panel3.gridx = 2;
		gbc_panel3.gridy = 0;
		gbc_panel3.weightx = 0.5;
		contentPane.add(panel3, gbc_panel3);
		panel3.setLayout(new GridLayout(7, 1, 0, 0));
		
		lblFrage2 = new JLabel("Frage:");
		panel3.add(lblFrage2);
		
		splitPaneA2 = new JSplitPane();
		panel3.add(splitPaneA2);
		
		lblA2 = new JLabel("A:");
		splitPaneA2.setLeftComponent(lblA2);
		
		rdbtnA2 = new JRadioButton("");
		rdbtnA2.addActionListener(e -> spieler2.setAuswahl("A"));
		splitPaneA2.setRightComponent(rdbtnA2);
		
		splitPaneB2 = new JSplitPane();
		panel3.add(splitPaneB2);
		
		lblB2 = new JLabel("B:");
		splitPaneB2.setLeftComponent(lblB2);
		
		rdbtnB2 = new JRadioButton("");
		rdbtnB2.addActionListener(e -> spieler2.setAuswahl("B"));
		splitPaneB2.setRightComponent(rdbtnB2);
		
		splitPaneC2 = new JSplitPane();
		panel3.add(splitPaneC2);
		
		lblC2 = new JLabel("C:");
		splitPaneC2.setLeftComponent(lblC2);
		
		rdbtnC2 = new JRadioButton("");
		rdbtnC2.addActionListener(e -> spieler2.setAuswahl("C"));
		splitPaneC2.setRightComponent(rdbtnC2);
		
		splitPaneD2 = new JSplitPane();
		panel3.add(splitPaneD2);
		
		lblD2 = new JLabel("D:");
		splitPaneD2.setLeftComponent(lblD2);
		
		rdbtnD2 = new JRadioButton("");
		rdbtnD2.addActionListener(e -> spieler2.setAuswahl("D"));
		splitPaneD2.setRightComponent(rdbtnD2);
		
		lblStatus2 = new JLabel("");
		panel3.add(lblStatus2);
		rdbtnA2.setEnabled(false);
		rdbtnB2.setEnabled(false);
		rdbtnC2.setEnabled(false);
		rdbtnD2.setEnabled(false);
	}
	
	/**
	 * Liest die Datei {@code datei} ein und gibt Erfolg/Misserfolg zurueck
	 * 
	 * @see EditorGUI
	 * @param datei einzulesende Datei
	 * @return Erfolg/Misserfolg des Einlesens
	 */
	private boolean readFile(File datei) {		
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
	 * Initialisiert das Hauptfenster mit den 3 Panels und initialisiert die benutzte 
	 * Semaphore. Ausserdem wird ein Pop-Up geoeffnet, in dem der Spieler eine Schwierigkeitsstufe waehlen soll.
	 * Wird dieses ohne Auswahl geschlossen, wird die schwierigste Stufe gewaehlt.
	 */
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1600, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{290, 290, 290, 0};
		gbl_contentPane.rowHeights = new int[]{454, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		initPanel1();
		initPanel2();
		initPanel3();
		
		spieler1 = new Spieler();
		spieler2 = new Spieler();
		
		bg1 = new ButtonGroup();
		bg2 = new ButtonGroup();
		bg1.add(rdbtnA1);
		bg1.add(rdbtnB1);
		bg1.add(rdbtnC1);
		bg1.add(rdbtnD1);
		bg2.add(rdbtnA2);
		bg2.add(rdbtnB2);
		bg2.add(rdbtnC2);
		bg2.add(rdbtnD2);
		
		String[] array = new String[dateien.size()];
		for(int i = 0; i< dateien.size(); i++) {
			array[i] = dateien.elementAt(i).getName().replace(".txt", "");		
		}
		jcbPopup = new JComboBox<String>(array);

		jcbDiff = new JComboBox<String>(diffs);
		jcbDiff.setSelectedIndex(0);
		JOptionPane.showMessageDialog( null, jcbDiff, "Wie schlau soll dein Gegner sein?", JOptionPane.QUESTION_MESSAGE);
		if(jcbDiff.getSelectedIndex() != -1) {
			diff = jcbDiff.getSelectedIndex();
		}
		else {
			diff = 4;
		}
		try {
			bereit.acquire();
		} catch (InterruptedException e) {
		}
	}
}
