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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
 * Beherbergt das Fenster fuer den Multiplayermodus.
 * @author Tom Sosedow
 *
 */
public class MultiplayerGUI extends JFrame {

	private JPanel contentPane, panel1, panel2, panel3;
	private JLabel lblCat, lblA2, lblB2, lblC2, lblD2, lblStatus2, lblStatus, lblFrage2, lblPunktestand, lblScore;
	private JRadioButton rdbtnA1, rdbtnA2, rdbtnB1, rdbtnB2, rdbtnC1, rdbtnC2, rdbtnD1, rdbtnD2;
	private JSplitPane splitPaneA1, splitPaneA2, splitPaneB1, splitPaneB2, splitPaneC1, splitPaneC2, splitPaneD1, splitPaneD2;
	private JButton btnAccept1, btnAccept2;
	private GridBagLayout gbl_contentPane;
	private GridBagConstraints gbc_panel1, gbc_panel2, gbc_panel3;
	private ButtonGroup bg2;
	private JComboBox<String> jcbPopup; 
	private Spieler spieler2;
	private Semaphore bereit = new Semaphore(1, true);
	private Random random = new Random();
	private SpielerPanel sp1;
	private Vector<File> dateien = new Vector<File>();
	private Spiel spiel;
	
	/**
	 * Initialisiert das Fenster und nutzt dabei den uebergebenen Vektor (Files) fuer die Kategorien
	 * @param files Vektor mit den File-Daten
	 * 
	 */
	public MultiplayerGUI(Vector<File> files) {
		initGUI();
		this.dateien = files;
		spiel = new Spiel(files);
		//sp1.getSpieler() = new Spieler();
		spieler2 = new Spieler();
		
		jcbPopup = new JComboBox<String>(spiel.getArr());
		
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
			if(!sp1.getSpieler().getAuswahl().equals("")) {
				sp1.getSpieler().setBereit(true);
				sp1.getLblStatus().setText("Warte auf Spieler 2");
				changeRdbtnState(1, false);
				btnAccept1.setEnabled(false);
			}
			else {
				sp1.getLblStatus().setText("Bitte waehle zuerst eine Antwort!");
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
		if(sp1.getSpieler().getBereit() && spieler2.getBereit()) {
			if(sp1.getSpieler().getAuswahl().equals(spiel.getActValues()[4])) {
				sp1.getSpieler().setPunkte(sp1.getSpieler().getPunkte()+1);
				lblScore.setText(sp1.getSpieler().getPunkte() + ":" + spieler2.getPunkte());
				sp1.getLblStatus().setText("<HTML><BODY BGCOLOR=#4EFF01>Richtig!</BODY></HTML>");
				executor.schedule(() -> {
					sp1.getLblStatus().setText("Richtig!");
			    }, 3, TimeUnit.SECONDS);
			}
			else {
				sp1.getLblStatus().setText("<HTML><BODY BGCOLOR=#FFCCCC>Leider falsch!</BODY></HTML>");
				executor.schedule(() -> {
					sp1.getLblStatus().setText("Leider falsch!");
			    }, 3, TimeUnit.SECONDS);
			}
			if(spieler2.getAuswahl().equals(spiel.getActValues()[4])) {
				spieler2.setPunkte(spieler2.getPunkte()+1);
				lblScore.setText(sp1.getSpieler().getPunkte() + ":" + spieler2.getPunkte());
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
			sp1.getSpieler().setBereit(false);
			spieler2.setBereit(false);
			sp1.getSpieler().setAuswahl("");
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
						
						//erste Frage
						refreshQ();
						lblStatus.setText("");
						sp1.getBg().clearSelection();
						bg2.clearSelection();
						bereit.acquire();
						accept(1);
						bereit.acquire();
						accept(2);
						//2 Fragen 
						for(int i = 0; i<2; i++) {
							lblStatus.setText("Die Richtige Antwort ist " + spiel.getActValues()[4] + "!");
							askQ();
							bereit.acquire();
						}
						lblStatus.setText("Die Richtige Antwort ist " + spiel.getActValues()[4] + "!");
						
						selectCat(2);
						
						//1 Frage der neuen Kategorie
						askQ();
						bereit.acquire();
						//naechste 2 Fragen
						for(int i = 0; i<2; i++) {
							lblStatus.setText("Die Richtige Antwort ist " + spiel.getActValues()[4] + "!");
							askQ();
							bereit.acquire();
						}
						lblStatus.setText("Die Richtige Antwort ist " + spiel.getActValues()[4] + "!");
						TimeUnit.SECONDS.sleep(2);
						
					}
					
					int z = 1;
					if(sp1.getSpieler().getPunkte() > spieler2.getPunkte())
						z = JOptionPane.showConfirmDialog(getParent(), "<html>Spieler 1 gewinnt! Gutes Spiel!<br> Wenn du das fenster schliessen moechtest, druecke Ok.</html>", "Ergebnis:", JOptionPane.YES_NO_OPTION);
					else if(sp1.getSpieler().getPunkte() < spieler2.getPunkte())
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
		sp1.getBg().clearSelection();
		bg2.clearSelection();
		changeRdbtnState(1, true);
		changeRdbtnState(2, true);
		btnAccept1.setEnabled(true);
		btnAccept2.setEnabled(true);
	}
	
	/**
	 * Öffnet ein Fenster, in dem Spieler {@code i} eine Kategorie auswaehlen soll.
	 * @param i Spielernummer
	 * @throws StopGameException 
	 */
	private void selectCat(int i) throws StopGameException {
		jcbPopup.setSelectedIndex(0);
		Boolean approve = false;
		while(!approve) {
			if(JOptionPane.showConfirmDialog( getParent(), jcbPopup, "Spieler " + i + ": Bitte waehle eine Kategorie (\"Nein\" beendet das Spiel)", JOptionPane.OK_OPTION) == JOptionPane.OK_OPTION) {
				spiel.setActKat(jcbPopup.getSelectedIndex());
				spiel.setActFile(dateien.elementAt(spiel.getActKat()));
				spiel.getKategorie().clear();
				spiel.readFile(spiel.getActFile());
				if(spiel.getKategorie().keySet().size()>2) {
					if(spiel.getHistory().keySet().size()>=dateien.size()) {
						spiel.getHistory().clear();
					}
					if(!spiel.getHistory().containsKey(spiel.getActKat())) {
						spiel.getHistory().put(spiel.getActKat(), new ArrayList<Integer>());
					}
					lblCat.setText("Kategorie: " + spiel.getActFile().getName().replace(".txt", ""));
					spiel.setKeys(spiel.getKategorie().keySet().toArray(new String[spiel.getKategorie().size()]));
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
		if(spiel.getHistory().get(spiel.getActKat()).size()<spiel.getKategorie().keySet().size()) { // wenn noch ungenutzte Fragen uebrig
			do{
				z = random.nextInt(spiel.getKeys().length);
			}while(spiel.getHistory().get(spiel.getActKat()).contains(z));
		}
		else {
			int temp = spiel.getHistory().get(spiel.getActKat()).get(spiel.getHistory().get(spiel.getActKat()).size()-1); //merke den Index der zuletzt gestellten Frage
			spiel.getHistory().put(spiel.getActKat(), new ArrayList<Integer>());
			spiel.getHistory().get(spiel.getActKat()).add(temp); 
			do{
				z = random.nextInt(spiel.getKeys().length);
			}while(z == temp);
		}
		
		spiel.getHistory().get(spiel.getActKat()).add(z);
		spiel.setActFrage(spiel.getKeys()[z]);
		sp1.getLblFrage().setText("<html><p>" + spiel.getActFrage() + "</p></html>");
		rdbtnA1.setText(spiel.getKategorie().get(spiel.getKeys()[z])[0]);
		rdbtnB1.setText(spiel.getKategorie().get(spiel.getKeys()[z])[1]);
		rdbtnC1.setText(spiel.getKategorie().get(spiel.getKeys()[z])[2]);
		rdbtnD1.setText(spiel.getKategorie().get(spiel.getKeys()[z])[3]);

		lblFrage2.setText("<html><p>" + spiel.getActFrage() + "</p></html>");
		rdbtnA2.setText(spiel.getKategorie().get(spiel.getKeys()[z])[0]);
		rdbtnB2.setText(spiel.getKategorie().get(spiel.getKeys()[z])[1]);
		rdbtnC2.setText(spiel.getKategorie().get(spiel.getKeys()[z])[2]);
		rdbtnD2.setText(spiel.getKategorie().get(spiel.getKeys()[z])[3]);
		
	}
	
	/**
	 * Initialisiert das rechte Panel fuer Spieler 1
	 * JLabel sp1.getLblFrage()
	 * SplitPane[JLabel A1 | RadioButton rdbtnA1]
	 * SplitPane[JLabel B1 | RadioButton rdbtnB1]
	 * SplitPane[JLabel C1 | RadioButton rdbtnC1]
	 * SplitPane[JLabel D1 | RadioButton rdbtnD1]
	 * JButton btnAccept1
	 * JLabel sp1.getLblStatus()
	 */
	private void initPanel1() {
		/*
		panel1 = new JPanel();
		gbc_panel1 = new GridBagConstraints();
		gbc_panel1.insets = new Insets(0, 0, 0, 5);
		gbc_panel1.fill = GridBagConstraints.BOTH;
		gbc_panel1.gridx = 0;
		gbc_panel1.gridy = 0;
		gbc_panel1.weightx = 0.5;
		
		contentPane.add(panel1, gbc_panel1);
		panel1.setLayout(new GridLayout(7, 1, 0, 0));
		
		sp1.getLblFrage() = new JLabel("Frage:");
		panel1.add(sp1.getLblFrage());
		
		splitPaneA1 = new JSplitPane();
		panel1.add(splitPaneA1);
		
		lblA = new JLabel("A:");
		splitPaneA1.setLeftComponent(sp1.getLblA());
		
		rdbtnA1 = new JRadioButton("");
		rdbtnA1.addActionListener(e -> sp1.getSpieler().setAuswahl("A"));
		splitPaneA1.setRightComponent(rdbtnA1);
		
		splitPaneB1 = new JSplitPane();
		panel1.add(splitPaneB1);
		
		sp1.getLblB() = new JLabel("B:");
		splitPaneB1.setLeftComponent(sp1.getLblB());
		
		rdbtnB1 = new JRadioButton("");
		rdbtnB1.addActionListener(e -> sp1.getSpieler().setAuswahl("B"));
		splitPaneB1.setRightComponent(rdbtnB1);
		
		splitPaneC1 = new JSplitPane();
		panel1.add(splitPaneC1);
		
		sp1.getLblC() = new JLabel("C:");
		splitPaneC1.setLeftComponent(sp1.getLblC());
		
		rdbtnC1 = new JRadioButton("");
		rdbtnC1.addActionListener(e -> sp1.getSpieler().setAuswahl("C"));
		splitPaneC1.setRightComponent(rdbtnC1);
		
		splitPaneD1 = new JSplitPane();
		panel1.add(splitPaneD1);
		
		sp1.getLblD() = new JLabel("D:");
		splitPaneD1.setLeftComponent(sp1.getLblD());
		
		rdbtnD1 = new JRadioButton("");
		splitPaneD1.setRightComponent(rdbtnD1);
		rdbtnD1.addActionListener(e -> sp1.getSpieler().setAuswahl("D"));
		
		btnAccept1 = new JButton("Bestaetigen");
		btnAccept1.addActionListener(e-> accept(1));
		panel1.add(btnAccept1);
		
		sp1.getLblStatus() = new JLabel("");
		panel1.add(sp1.getLblStatus());
		
		splitPaneA1.setEnabled(false);
		splitPaneB1.setEnabled(false);
		splitPaneC1.setEnabled(false);
		splitPaneD1.setEnabled(false);
		*/
		
//		SpielerPanel sp1 = new SpielerPanel();
//		contentPane.add(sp1.getPanel(), gbc_panel1);
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
		
		//initPanel1();
		sp1 = new SpielerPanel(spiel, bereit);
		contentPane.add(sp1.getPanel(), gbc_panel1);
		initPanel2();
		initPanel3();

		bg2.add(rdbtnA2);
		bg2.add(rdbtnB2);
		bg2.add(rdbtnC2);
		bg2.add(rdbtnD2);
	}
}
