package aop_pruefung;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.io.File;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.SwingConstants;

/**
 * Beinhaltet das Fenster fuer den Singleplayermodus und den Spielablauf.
 * @author Tom Sosedow
 * 
 */
public class SingleplayerGUI extends JFrame {
	
	//UI
	private JPanel contentPane, panelMitte;
	private JLabel lblCat, lblStatus, lblPunktestand, lblScore;
	private GridBagLayout gbl_contentPane;
	private GridBagConstraints gbc_panel1, gbc_panel2, gbc_panel3;
	private JComboBox<String> jcbPopup; 
	private JComboBox<String> jcbChooseDiff;	
	
	//interne Hilfen
	private Semaphore ready = new Semaphore(1, true);
	private int diff, runde = 1, frage = 1;
	private String[] diffs = {"Leicht", "Mittel", "Schwer", "Dr. Kawashima"};
	private JFrame frame = this;
	private Consumer<Integer> cons;
	private Spiel spiel;
	private SpielerPanel sp1, sp2;
	
	/**
	 * Initialisiert das Fenster und nutzt dabei den uebergebenen Vektor (Files) fuer die Kategorien
	 * @param files Vektor mit den Dateien (Kategorien)
	 */
	public SingleplayerGUI(Vector<File> files) {
		initGUI();
		spiel = new Spiel(files);

		jcbChooseDiff = new JComboBox<String>(diffs);
		jcbChooseDiff.setSelectedIndex(0);
		JOptionPane.showMessageDialog( null, jcbChooseDiff, "Wie schlau soll dein Gegner sein?", JOptionPane.QUESTION_MESSAGE);
		if(jcbChooseDiff.getSelectedIndex() != -1) {
			diff = jcbChooseDiff.getSelectedIndex();
		}
		else {
			diff = 4;
		}
		
		String[] array = new String[files.size()];
		for(int i = 0; i< files.size(); i++) {
			array[i] = files.elementAt(i).getName().replace(".txt", "");		
		}
		jcbPopup = new JComboBox<String>(array);
		
		
		try {
			ready.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Spielablauf wird hier durchgegangen entsprechend der Spielregeln. Startet dafuer einen neuen Thread damit mithilfe 
	 * von Semaphores auf Spielereingabe gewartet werden kann. Kategorieauswahl von Spieler 2 wird zufaellig getaetigt.
	 */
	public void spielen() {
		Runnable spielen = new Runnable() {
			@Override public void run() {
				try {
					//3 Runden
					for(int a = 0; a < 3; a++) {
						jcbPopup.setSelectedIndex(0);
						lblCat.setText("Kategorie: " + spiel.selectCat(1)); //Kategorie waehlen
						updateTitle();
						
						//erste Frage
						lblStatus.setText("");
						refreshQ();
						sp1.clearSelection();
						sp2.clearSelection();
						sp1.changeRdbtnState(true);
						sp1.enableBtnAccept(true);
						auswahlBot(diff);
						ready.acquire();
						
						//2 Fragen 
						for(int i = 0; i<2; i++) {
							lblStatus.setText("Die Richtige Antwort ist " + spiel.getActValues()[4] + "!");
							askQ();
							updateTitle();
							ready.acquire();
						}
						lblStatus.setText("Die Richtige Antwort ist " + spiel.getActValues()[4] + "!");
						
						jcbPopup.setSelectedIndex(0);
						lblCat.setText("Kategorie: " + spiel.botChooseCat()); //Kategorie waehlen
						
						//1 Frage der neuen Kategorie
						askQ();
						updateTitle();
						ready.acquire();
						//naechste 2 Fragen
						for(int i = 0; i<2; i++) {
							lblStatus.setText("Die Richtige Antwort ist " + spiel.getActValues()[4] + "!");
							askQ();
							updateTitle();
							ready.acquire();
						}
						lblStatus.setText("Die Richtige Antwort ist " + spiel.getActValues()[4] + "!");
						TimeUnit.SECONDS.sleep(2);
						runde++;
						frage = 1;
						
					}
					
					int z = 1;
					if(sp1.getSpieler().getPunkte() > sp2.getSpieler().getPunkte())
						z = JOptionPane.showConfirmDialog(getParent(), "<html>Spieler 1 gewinnt! Gutes Spiel!<br> Moechtet ihr das Quizfenster schliessen?</html>", "Ergebnis:", JOptionPane.YES_NO_OPTION);
					else if(sp1.getSpieler().getPunkte() < sp2.getSpieler().getPunkte())
						z = JOptionPane.showConfirmDialog(getParent(), "<html>Spieler 2 gewinnt! Gutes Spiel!<br> Moechtet ihr das Quizfenster schliessen?</html>", "Ergebnis:", JOptionPane.YES_NO_OPTION);
					else
						z = JOptionPane.showConfirmDialog(getParent(), "<html>Gleichstand! Was fuer ein Spiel!<br> Moechtet ihr das Quizfenster schliessen?</html>", "Ergebnis:", JOptionPane.YES_NO_OPTION);
					
					if(z == 0)
						dispose();
					else {
						sp1.changeRdbtnState(false);
						sp1.enableBtnAccept(false);
					}
					
				} catch (InterruptedException|IllegalArgumentException|NullPointerException e) { 
					dispose();
					JOptionPane.showMessageDialog(getParent(), "Ein Fehler ist aufgetreten", "Fehler", JOptionPane.ERROR_MESSAGE);
				}
				catch(StopAppException e) {
					dispose();
					JOptionPane.showMessageDialog(getParent(), e.getMessage(), "Spiel beendet!", JOptionPane.INFORMATION_MESSAGE);
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
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lblStatus.setText("");
		sp1.setLblStatus("");
		sp2.setLblStatus("");
		refreshQ();
		sp1.clearSelection();
		sp2.clearSelection();
		sp1.changeRdbtnState(true);
		sp1.enableBtnAccept(true);
		auswahlBot(diff);
	}
	
	/**
	 * Waehlt eine zufaellige naechste Frage (die noch nicht gestellt wurde) und zeigt 
	 * sie mit den Antwortmoeglichkeiten in allen entsprechenden Feldern an
	 */
	private void refreshQ(){
		int z = spiel.nextQ();
		
		sp1.setLblFrage("<html><p>" + spiel.getActFrage() + "</p></html>");
		sp1.setLblAntworten(spiel.getAntwort(z, 0), spiel.getAntwort(z, 1), spiel.getAntwort(z, 2), spiel.getAntwort(z, 3));

		sp2.setLblFrage("<html><p>" + spiel.getActFrage() + "</p></html>");
		sp2.setLblAntworten(spiel.getAntwort(z, 0), spiel.getAntwort(z, 1), spiel.getAntwort(z, 2), spiel.getAntwort(z, 3));
	}
	
	/**
	 * Bot waehlt entsprechend seiner Schwierigkeitsstufe eine richtige oder falsche Antwort
	 * @param i Schwierigkeit (0-3)
	 */
	private void auswahlBot(float i) {
		String rAntwort = spiel.getActValues()[4];
		String[] ABCD = {"A", "B", "C", "D"};
		int temp = 0;
		Random random = new Random();
		float schranke = (i*0.2f)+0.2f;
		float z = random.nextFloat();
		
		// |0								<Schranke>							1|
		//  	->z->richtige Antw.			<  -  -  >		->z->falsche Antwort
		//wenn z vor der Schranke landet gibt der Bot die richtige Antwort ab, falls nicht eine zufaellige falsche.
		if(z<schranke) {
			sp2.setAntwort(rAntwort);
		}
		else {
			temp = random.nextInt(4);
			while(rAntwort.equals(ABCD[temp])) {
				temp = random.nextInt(4);
			}
			sp2.setAntwort(ABCD[temp]);
		}
	}
	
	/**
	 * Initialisiert das mittlere Panel fuer Punktestand, Ausgaben, Ansagen und aktuelle gewaehlte Kategorie
	 */
	private void initPanelMitte(){
		panelMitte = new JPanel();
		gbc_panel2 = new GridBagConstraints();
		gbc_panel2.fill = GridBagConstraints.BOTH;
		gbc_panel2.gridx = 1;
		gbc_panel2.gridy = 0;
		gbc_panel2.weightx = 0.1;
		contentPane.add(panelMitte, gbc_panel2);
		panelMitte.setLayout(new GridLayout(5, 1, 0, 0));
		
		lblCat = new JLabel("Gewaehlte Kategorie:");
		lblCat.setHorizontalAlignment(SwingConstants.CENTER);
		panelMitte.add(lblCat);
		
		lblPunktestand = new JLabel("Punktestand:");
		lblPunktestand.setHorizontalAlignment(SwingConstants.CENTER);
		panelMitte.add(lblPunktestand);
		
		lblScore = new JLabel("0:0");
		lblScore.setHorizontalAlignment(SwingConstants.CENTER);
		panelMitte.add(lblScore);
		
		lblStatus = new JLabel("");
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		panelMitte.add(lblStatus);
	}
	
	/**
	 * Initialisiert das Hauptfenster mit allen zugehoerigen grafischen Elementen. Ausserdem wird ein Pop-Up geoeffnet, 
	 * in dem der Spieler eine Schwierigkeitsstufe waehlen soll.
	 * Wird dieses ohne Auswahl geschlossen, wird die schwierigste Stufe gewaehlt.
	 * Außerdem wird hier die Routine in Form eines Consumers gespeichert, welche ausgefuehrt wird, wenn ein Spieler seine Auswahl bestaetigt.
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
		
		cons = new Consumer<Integer>() {
			public void accept(Integer i) {
				ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
				//Entscheidung ob Warten auf anderen Spieler noetig

				if(sp1.getSpieler().getAuswahl().equals("")) {
					sp1.setLblStatus("Bitte waehle zuerst eine Antwort!");
					return;
				}
				else {
					sp1.changeRdbtnState(false);
					sp1.enableBtnAccept(false);
					if(sp1.getSpieler().getAuswahl().equals(spiel.getActValues()[4])) {
						sp1.getSpieler().setPunkte(sp1.getSpieler().getPunkte()+1);
						lblScore.setText(sp1.getSpieler().getPunkte() + ":" + sp2.getSpieler().getPunkte());
						sp1.setLblStatus("<HTML><BODY BGCOLOR=#4EFF01>Richtig!</BODY></HTML>");
						executor.schedule(() -> {
							sp1.setLblStatus("");
					    }, 2, TimeUnit.SECONDS);
					}
					else {
						sp1.setLblStatus("<HTML><BODY BGCOLOR=#FFCCCC>Leider falsch!</BODY></HTML>");
						executor.schedule(() -> {
							sp1.setLblStatus("");
					    }, 2, TimeUnit.SECONDS);
					}
					if(sp2.getSpieler().getAuswahl().equals(spiel.getActValues()[4])) {
						sp2.getSpieler().setPunkte(sp2.getSpieler().getPunkte()+1);
						lblScore.setText(sp1.getSpieler().getPunkte() + ":" + sp2.getSpieler().getPunkte());
						sp2.setLblStatus("<HTML><BODY BGCOLOR=#4EFF01>Richtig!</BODY></HTML>");
						executor.schedule(() -> {
							sp2.setLblStatus("");
					    }, 2, TimeUnit.SECONDS);
					}
					else {
						sp2.setLblStatus("<HTML><BODY BGCOLOR=#FFCCCC>Leider falsch!</BODY></HTML>");
						executor.schedule(() -> {
							sp2.setLblStatus("");
					    }, 2, TimeUnit.SECONDS);
					}
					sp1.getSpieler().setAuswahl("");
					sp2.getSpieler().setAuswahl("");
					ready.release();
				}
			}
		};
		
		
		gbc_panel1 = new GridBagConstraints();
		gbc_panel1.insets = new Insets(0, 0, 0, 5);
		gbc_panel1.fill = GridBagConstraints.BOTH;
		gbc_panel1.gridx = 0;
		gbc_panel1.gridy = 0;
		gbc_panel1.weightx = 0.5;
		sp1 = new SpielerPanel(cons);
		contentPane.add(sp1.getPanel(), gbc_panel1);
		
		initPanelMitte();
		
		gbc_panel3 = new GridBagConstraints();
		gbc_panel3.fill = GridBagConstraints.BOTH;
		gbc_panel3.gridx = 2;
		gbc_panel3.gridy = 0;
		gbc_panel3.weightx = 0.5;
		sp2 = new SpielerPanel(cons);
		contentPane.add(sp2.getPanel(), gbc_panel3);
		
		sp2.enableBtnAccept(false);
		sp2.changeRdbtnState(false);
	}
	
	/**
	 * Aktualisiert den Titel, sodass die aktuelle Runde und Fragennummer angezeigt wird
	 */
	private void updateTitle() {
		frame.setTitle("Runde: " + runde + " | Frage: " + frage); 
		frage++; 
	}
}
