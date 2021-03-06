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
import java.awt.Insets;
import java.io.File;
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
 * Beherbergt das Fenster fuer den Multiplayermodus.
 * @author Tom Sosedow
 *
 */
public class MultiplayerGUI extends JFrame {

	private JPanel contentPane, panel1, panel2, panel3;
	private JLabel lblCat, lblA1, lblA2, lblB1, lblB2, lblC1, lblC2, lblD1, lblD2, lblStatus1, lblStatus2, lblStatus, lblFrage1, lblFrage2, lblPunktestand, lblScore;
	private JRadioButton rdbtnA1, rdbtnA2, rdbtnB1, rdbtnB2, rdbtnC1, rdbtnC2, rdbtnD1, rdbtnD2;
	private JSplitPane splitPaneA1, splitPaneA2, splitPaneB1, splitPaneB2, splitPaneC1, splitPaneC2, splitPaneD1, splitPaneD2;
	private JButton btnAccept1, btnAccept2;
	private GridBagLayout gbl_contentPane;
	private GridBagConstraints gbc_panel1, gbc_panel2, gbc_panel3;
	private ButtonGroup bg1, bg2;
	private File actFile;
	private Map<String, String[]> kategorie = new HashMap<>(); //Beinhaltet zu jeder Frage die dazugehoerigen Antworten und die richtige Antwort
	private Vector<File> dateien = new Vector<File>();
	private Map<Integer, ArrayList<Integer>> history = new HashMap<>(); //speichert die Information, welche Fragen aus welche Kategorie schon dran waren.
	private JComboBox<String> jcbPopup; 
	private String[] keys;
	private int actKat, runde = 1, frage = 1;
	private String actFrage ="";
	private Spieler spieler1, spieler2;
	private Semaphore bereit = new Semaphore(1, true);
	private Random random = new Random();
	private JFrame frame = this;
	
	/**
	 * Initialisiert das Fenster und nutzt dabei den uebergebenen Vektor (Files) fuer die Kategorien
	 * @param files Vektor mit den File-Daten (Kategorien)
	 * 
	 */
	public MultiplayerGUI(Vector<File> files) {
		this.dateien = files;
		initGUI();
		
		spieler1 = new Spieler();
		spieler2 = new Spieler();
		
		String[] array = new String[dateien.size()];
		for(int i = 0; i< dateien.size(); i++) {
			array[i] = dateien.elementAt(i).getName().replace(".txt", "");		
		}
		jcbPopup = new JComboBox<String>(array);
		
		try {
			bereit.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loggt die Auswahl des Spielers {@code i} ein (falls sie nicht leer ist) wenn der Spieler Bestaetigen drueckt und setzt ihn auf "Bereit".
	 * Wenn beide Spieler bereit sind, also ihre Antworten eingeloggt haben, werden die Antworten ausgewertet, entsprechend 
	 * Punkte verteilt und der Startzustand fuer die naechste Fragerunde hergestellt.
	 * 
	 * @param i Nummer des Spielers der akzeptiert
	 */
	private void accept(int i) {
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		//Entscheidung ob Warten auf anderen Spieler noetig
		if (i == 1) {
			if(!spieler1.getAuswahl().equals("")) {
				spieler1.setBereit(true);
				lblStatus1.setText("Warte auf Spieler 2");
				changeRdbtnState(1, false);
				btnAccept1.setEnabled(false);
			}
			else {
				lblStatus1.setText("Bitte waehle zuerst eine Antwort!");
				return;
			}
		}
		else {
			if(!spieler2.getAuswahl().equals("")) {
				spieler2.setBereit(true);
				lblStatus2.setText("Warte auf Spieler 1");
				changeRdbtnState(2, false);
				btnAccept2.setEnabled(false);
			}
			else {
				lblStatus2.setText("Bitte waehle zuerst eine Antwort!");
				return;
			}
		}
		//Wenn beide Spieler bereit: Punkte aktualisieren, richtige Antwort anzeigen und Ausgangszustand fuer naechste Fragerunde herstellen
		if(spieler1.getBereit() && spieler2.getBereit()) {
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
			spieler1.setBereit(false);
			spieler2.setBereit(false);
			spieler1.setAuswahl("");
			spieler2.setAuswahl("");
			bereit.release();
		}
		
	}
	
	/**
	 * Aktiviert(b=true) bzw. deaktiviert (b=false) die Moeglichkeit des Sspielers i, eine Auswahl in seiner Buttongroup zu taetigen
	 * @param i Spielernummer
	 * @param b gewuenschter Status der Buttons (Boolean)
	 */
	private void changeRdbtnState(int i, Boolean b) {
		if (i == 1) {
			rdbtnA1.setEnabled(b);
			rdbtnB1.setEnabled(b);
			rdbtnC1.setEnabled(b);
			rdbtnD1.setEnabled(b);
		}
		else {
			rdbtnA2.setEnabled(b);
			rdbtnB2.setEnabled(b);
			rdbtnC2.setEnabled(b);
			rdbtnD2.setEnabled(b);
		}
	}
	
	/**
	 * Spielablauf wird hier durchgegangen entsprechend der Spielregeln. Startet dafuer einen neuen Thread damit mithilfe 
	 * von Semaphores auf Spielereingaben gewartet werden kann.
	 */
	public void spielen() {
		Runnable spielen = new Runnable() {
			@Override public void run() {
				try {
					//3 Runden
					for(int a = 0; a < 3; a++) {
						selectCat(1); //Kategorie waehlen
						keys = kategorie.keySet().toArray(new String[kategorie.size()]); 
						updateTitle();
						//erste Frage
						refreshQ();
						lblStatus.setText("");
						bg1.clearSelection();
						bg2.clearSelection();
						bereit.acquire();
						//2 Fragen 
						for(int i = 0; i<2; i++) {
							lblStatus.setText("Die Richtige Antwort ist " + kategorie.get(actFrage)[4] + "!");
							askQ();
							updateTitle();
							bereit.acquire();
						}
						lblStatus.setText("Die Richtige Antwort ist " + kategorie.get(actFrage)[4] + "!");
						
						selectCat(2);
						
						//1 Frage der neuen Kategorie
						askQ();
						updateTitle();
						bereit.acquire();
						//naechste 2 Fragen
						for(int i = 0; i<2; i++) {
							lblStatus.setText("Die Richtige Antwort ist " + kategorie.get(actFrage)[4] + "!");
							askQ();
							updateTitle();
							bereit.acquire();
						}
						lblStatus.setText("Die Richtige Antwort ist " + kategorie.get(actFrage)[4] + "!");
						TimeUnit.SECONDS.sleep(2);
						runde++;
						frage = 1;
					}
					
					int z = 1;
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
							changeRdbtnState(1, false);
							btnAccept1.setEnabled(false);
							changeRdbtnState(2, false);
							btnAccept2.setEnabled(false);
							break;
					}
				} catch (InterruptedException|IllegalArgumentException|NullPointerException e) {
					JOptionPane.showMessageDialog(getParent(), "Ein Fehler ist aufgetreten", "Fehler", JOptionPane.ERROR_MESSAGE);
					dispose();
				}
				catch(StopGameException e) {
					dispose();
					JOptionPane.showMessageDialog(getParent(), e.getMessage(), "Spiel beendet!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		new Thread(spielen).start();
	}
	
	/**
	 * Stellt eine neue Frage und stellt den Wartezustand auf die Eingaben der Nutzer her.
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
		changeRdbtnState(1, true);
		changeRdbtnState(2, true);
		btnAccept1.setEnabled(true);
		btnAccept2.setEnabled(true);
	}
	
	/**
	 * �ffnet ein Fenster, in dem Spieler {@code i} eine Kategorie auswaehlen soll.
	 * @param i Spielernummer
	 * @throws StopGameException 
	 */
	private void selectCat(int i) throws StopGameException {
		jcbPopup.setSelectedIndex(0);
		Boolean approve = false;
		while(!approve) {
			if(JOptionPane.showConfirmDialog( getParent(), jcbPopup, "Spieler " + i + ": Bitte waehle eine Kategorie (\"Nein\" beendet das Spiel)", JOptionPane.OK_OPTION) == JOptionPane.OK_OPTION) {
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
					lblCat.setText("Kategorie: " + actFile.getName().replace(".txt", ""));
					keys = kategorie.keySet().toArray(new String[kategorie.size()]);
					approve = true;
				}
				else {
					JOptionPane.showMessageDialog(getParent(), "Die gewaehlte Kategorie ist leer oder beinhaltet zu wenige Fragen. Waehle erneut.");
				}
			}
			else {
				throw new StopGameException("Keine Kategorie gewaehlt!");
			}
		}
	}
	
	/**
	 * Waehlt eine zufaellige naechste Frage (die noch nicht gestellt wurde) und zeigt 
	 * sie mit den Antwortmoeglichkeiten in allen entsprechenden Feldern an
	 */
	private void refreshQ() throws NullPointerException{
		int z;
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
		
		history.get(actKat).add(z);
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
		gbc_panel1.insets = new Insets(0, 0, 0, 5);
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
		btnAccept1.addActionListener(e-> accept(1));
		panel1.add(btnAccept1);
		
		lblStatus1 = new JLabel("");
		panel1.add(lblStatus1);
		
		splitPaneA1.setEnabled(false);
		splitPaneB1.setEnabled(false);
		splitPaneC1.setEnabled(false);
		splitPaneD1.setEnabled(false);
	}
	
	/**
	 * Initialisiert das mittlere Panel fuer Punktestand, Ausgaben, Ansagen und aktuelle gewaehlte Kategorie
	 */
	private void initPanel2(){
		panel2 = new JPanel();
		gbc_panel2 = new GridBagConstraints();
		gbc_panel2.insets = new Insets(0, 0, 0, 5);
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
	 * JButton btnAccept2
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
		
		btnAccept2 = new JButton("Bestaetigen");
		btnAccept2.addActionListener(e-> accept(2));
		panel3.add(btnAccept2);
		
		lblStatus2 = new JLabel("");
		panel3.add(lblStatus2);
		
		splitPaneA2.setEnabled(false);
		splitPaneB2.setEnabled(false);
		splitPaneC2.setEnabled(false);
		splitPaneD2.setEnabled(false);
	}
	
	/**
	 * Liest die Datei {@code datei} ein und gibt Erfolg/Misserfolg zurueck
	 * 
	 * @see EditorGUI
	 * @param datei einzulesende Datei
	 * @return Boolean: Erfolg/Misserfolg des Einlesens
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
	 * Initialisiert das Hauptfenster mit den 3 Panels und initialisiert die benutzte Semaphore
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
	}
	
	/**
	 * Aktualisiert den Titel, sodass die aktuelle Runde und Fragennummer angezeigt wird
	 */
	private void updateTitle() {
		frame.setTitle("Runde: " + runde + " | Frage: " + frage); 
		frage++; 
	}
}
