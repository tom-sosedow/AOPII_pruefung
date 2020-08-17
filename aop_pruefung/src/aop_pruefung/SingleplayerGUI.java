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
import java.io.FileFilter;
import java.io.FileNotFoundException;
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
	private Map<String, String[]> kategorie = new HashMap<>();
	private Vector<File> dateien = new Vector<File>();
	private JComboBox<File> jcbPopup; 
	private JComboBox<String> jcbDiff;
	private String[] keys;
	private Spieler spieler1, spieler2;
	private Semaphore bereit = new Semaphore(1, true);
	private Random random = new Random();
	private float diff;
	private String[] diffs = {"Leicht", "Mittel", "Schwer", "Dr. Kawashima"};
	private Vector<Integer> fragen = new Vector<Integer>();
	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	
	/**
	 * Initialisiert das Fenster und liest alle Dateien aus dem �bergebenen Ordner ein.
	 * 
	 * @param pfad Verzeichnis, in dem die Dateien liegen
	 */
	public SingleplayerGUI(File pfad) {
		this.pfad = pfad;
		initGUI();	
		ls = this.pfad.listFiles(new FileFilter() {
			public boolean accept(File f) {
					return f.isFile()&&f.getName().endsWith(".txt");}});
		
		if (ls != null && ls.length != 0) 
			for(int i = 0; i< ls.length; i++) 
				dateien.add(ls[i]);	
	}
	
	/**
	 * Initialisiert das Fenster und nutzt dabei den �bergebenen Vektor (Files)
	 * @param dateien Vektor mit den File-Daten
	 * @wbp.parser.constructor
	 */
	public SingleplayerGUI(Vector<File> files) {
		this.dateien = files;
		initGUI();
	}
	
	/**
	 * Loggt die Auswahl des Spielers ein (falls sie nicht leer ist) wenn der Spieler Best�tigen dr�ckt.
	 * Die Antworten ausgewertet, ggf. ein Punkt vergeben und der Startzustand f�r die n�chste Fragerunde hergestellt.
	 */
	private void accept() {
		if(spieler1.getAuswahl().equals("")) {
			lblStatus1.setText("Bitte w�hle zuerst eine Antwort!");
			return;
		}
		if(spieler1.getAuswahl().equals(kategorie.get(lblFrage1.getText())[4])) {
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
		if(spieler2.getAuswahl().equals(kategorie.get(lblFrage2.getText())[4])) {
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
	 * Spielablauf wird hier durchgegangen entsprechend der Spielregeln. Startet daf�r einen neuen Thread damit mithilfe 
	 * von Semaphores auf Spielereingabe gewartet werden kann. Kategorieauswahl von Spieler 2 wird zuf�llig get�tigt.
	 */
	public void spielen() {
		Runnable spielen = new Runnable() {
			@Override public void run() {
				try {
					//3 Runden
					for(int a = 0; a < 3; a++) {
						selectCat(); //Kategorie w�hlen
						keys = kategorie.keySet().toArray(new String[kategorie.size()]); //Fragenliste
						
						//erste Frage
						refreshQ();
						auswahlBot(diff);
						bereit.acquire();
						//2 Fragen 
						for(int i = 0; i<2; i++) {
							askQ();
							bereit.acquire();
						}
						
						//Bot w�hlt Kategorie
						actFile = dateien.elementAt(random.nextInt(dateien.size()));
						readFile(actFile);
						lblCat.setText("Kategorie: " + actFile.getName().replace(".txt", ""));
						keys = kategorie.keySet().toArray(new String[kategorie.size()]);
						
						//n�chste 3 fragen mit neuer Kategorie
						for(int i = 0; i<3; i++) {
							askQ();
							bereit.acquire();
						}
					}
					if(spieler1.getPunkte() > spieler2.getPunkte())
						lblStatus.setText("Spieler 1 gewinnt.");
					else if(spieler1.getPunkte() < spieler2.getPunkte())
						lblStatus.setText("Spieler 2 gewinnt.");
					else
						lblStatus.setText("Gleichstand! Was f�r ein Spiel!");
				} catch (InterruptedException|IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(spielen).start();
	}
	
	/**
	 * Zeigt richtige Antwort der letzten Frage an, wartet und stellt die neue Frage, 
	 * nachdem die vorherigen Auswahlen geleert werden. Bot w�hlt Antwort entsprechend der gew�hlten Schwierigkeitsstufe aus.
	 */
	private void askQ() {
		lblStatus.setText("Die Richtige Antwort ist " + kategorie.get(lblFrage1.getText())[4] + "!");
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
	 * �ffnet ein fenster, in dem der Spieler eine Kategorie ausw�hlen soll.
	 */
	private void selectCat() {
		JOptionPane.showMessageDialog( null, jcbPopup, "Bitte waehle eine Kategorie", JOptionPane.QUESTION_MESSAGE);
		if(jcbPopup.getSelectedIndex() != -1) {
			actFile = dateien.elementAt(jcbPopup.getSelectedIndex());
			lblCat.setText("Kategorie: " + actFile.getName().replace(".txt", ""));
			readFile(actFile);
			fragen.clear();
		}
		else {
			this.dispose();
		}
		
	}
	
	/**
	 * W�hlt eine zuf�llige n�chste Frage (die noch nicht gestellt wurde) und zeigt 
	 * sie mit den Antwortm�glichkeiten in allen entsprechenden Feldern an
	 * 
	 * @see MuliplayerGUI
	 */
	private void refreshQ() {
		int z;
		do{
			z = random.nextInt(keys.length);
		}while(fragen.contains(z));
		fragen.add(z);
		lblFrage1.setText(keys[z]);
		rdbtnA1.setText(kategorie.get(keys[z])[0]);
		rdbtnB1.setText(kategorie.get(keys[z])[1]);
		rdbtnC1.setText(kategorie.get(keys[z])[2]);
		rdbtnD1.setText(kategorie.get(keys[z])[3]);

		lblFrage2.setText(keys[z]);
		rdbtnA2.setText(kategorie.get(keys[z])[0]);
		rdbtnB2.setText(kategorie.get(keys[z])[1]);
		rdbtnC2.setText(kategorie.get(keys[z])[2]);
		rdbtnD2.setText(kategorie.get(keys[z])[3]);
		
	}
	
	/**
	 * Bot w�hlt entsprechend seiner Schwerigkeitsstufe eine richtige oder falsche Antwort
	 * @param i Schwierigkeit (0-3)
	 */
	private void auswahlBot(float i) {
		String rAntwort = kategorie.get(lblFrage1.getText())[4];
		String[] ABCD = {"A", "B", "C", "D"};
		int temp = 0;
		float schranke = (i*0.2f)+0.2f;
		float temp2 = random.nextFloat();
		if(temp2<schranke) {
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
	 * Initialisiert das rechte Panel f�r Spieler 1
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
	 * Initialisiert das mittlere Panel f�r Punktestand, Ausgaben, Ansagen und aktuelle gew�hlte Kategorie
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
	 * Initialisiert das rechte Panel f�r Spieler 2
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
		gbc_panel2.weightx = 0.5;
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
	 * Liest die Datei {@code datei} ein und gibt Erfolg/Misserfolg zur�ck
	 * 
	 * @see EditorGUI
	 * @param datei einzulesende Datei
	 * @return Erfolg/Misserfolg des Einlesens
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
		catch(FileNotFoundException e){
			System.out.println("Fehler.");
			e.printStackTrace();
			return false;
		}
	    catch(NullPointerException e) {
	    	System.out.println("Fehler.");
	    	e.printStackTrace();
	    	return false;
	    }
	}
	/**
	 * Initialisiert das Hauptfenster mit den 3 Panels und initialisiert die benutzte 
	 * Semaphore. Au�erdem wird ein Pop-Up ge�ffnet, in dem der Spieler eine Schwierigkeitsstufe w�hlen soll.
	 * Wird dieses ohne Auswahl geschlossen, wird die schwierigste Stufe gew�hlt.
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
		
		spieler1 = new Spieler(1);
		spieler2 = new Spieler(2);
		
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
		jcbPopup = new JComboBox<File>(dateien);

		jcbDiff = new JComboBox<String>(diffs);
		JOptionPane.showMessageDialog( null, jcbDiff, "W�hle eine Schwierigkeitsstufe (des Gegners)", JOptionPane.QUESTION_MESSAGE);
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
