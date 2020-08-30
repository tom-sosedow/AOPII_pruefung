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
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.swing.SwingConstants;
import javax.swing.JRadioButton;

/**
 * Beherbergt das Fenster fuer den Multiplayermodus.
 * @author Tom Sosedow
 *
 */
public class MultiplayerGUI extends JFrame {

	private JPanel contentPane, panel2;
	private JLabel lblCat, lblStatus, lblPunktestand, lblScore;
	private GridBagLayout gbl_contentPane;
	private GridBagConstraints gbc_panel1, gbc_panel2, gbc_panel3;
	private JComboBox<File> jcbPopup; 
	private Semaphore bereit = new Semaphore(1, true);
	private SpielerPanel sp1, sp2;
	private Spiel spiel;
	private int spieleranzahl = 2, runde = 1, frage = 1;
	private Consumer<Integer> cons;
	private JFrame frame = this;
	
	/**
	 * Initialisiert das Fenster und nutzt dabei den uebergebenen Vektor (Files) fuer die Kategorien
	 * @param files Vektor mit den File-Daten
	 * 
	 */
	public MultiplayerGUI(Vector<File> files) {
		initGUI();
		spiel = new Spiel(files);
		jcbPopup = new JComboBox<File>(files);
		
		try {
			bereit.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
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
						jcbPopup.setSelectedIndex(0);
						lblCat.setText("Kategorie: " + spiel.selectCat(1)); //Kategorie waehlen
						updateTitle();
						
						//erste Frage
						lblStatus.setText("");
						refreshQ();
						sp1.getBg().clearSelection();
						sp2.getBg().clearSelection();
						sp1.changeRdbtnState(true);
						sp2.changeRdbtnState(true);
						sp1.getBtnAccept().setEnabled(true);
						sp2.getBtnAccept().setEnabled(true);
						bereit.acquire();
						
						//2 Fragen 
						for(int i = 0; i<2; i++) {
							lblStatus.setText("Die Richtige Antwort ist " + spiel.getActValues()[4] + "!");
							askQ();
							updateTitle();
							bereit.acquire();
						}
						lblStatus.setText("Die Richtige Antwort ist " + spiel.getActValues()[4] + "!");
						
						jcbPopup.setSelectedIndex(0);
						lblCat.setText("Kategorie: " + spiel.selectCat(2)); //Kategorie waehlen
						
						//1 Frage der neuen Kategorie
						askQ();
						updateTitle();
						bereit.acquire();
						//naechste 2 Fragen
						for(int i = 0; i<2; i++) {
							lblStatus.setText("Die Richtige Antwort ist " + spiel.getActValues()[4] + "!");
							askQ();
							updateTitle();
							bereit.acquire();
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
					
					switch(z) {
						case 0:
							dispose();
							break;
						default:
							sp1.changeRdbtnState(false);
							sp1.getBtnAccept().setEnabled(false);
							sp2.changeRdbtnState(false);
							sp2.getBtnAccept().setEnabled(false);
							break;
					}
				} catch (InterruptedException|IllegalArgumentException|NullPointerException e) {
					e.printStackTrace();
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
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lblStatus.setText("");
		refreshQ();
		sp1.getBg().clearSelection();
		sp2.getBg().clearSelection();
		sp1.changeRdbtnState(true);
		sp2.changeRdbtnState(true);
		sp1.getBtnAccept().setEnabled(true);
		sp2.getBtnAccept().setEnabled(true);
	}
	
	/**
	 * Waehlt eine zufaellige naechste Frage (die noch nicht gestellt wurde) und zeigt 
	 * sie mit den Antwortmoeglichkeiten in allen entsprechenden Feldern an
	 */
	private void refreshQ() throws NullPointerException{
		int z = spiel.nextCat();
		
		////possible foreach:
		sp1.setLblFrage("<html><p>" + spiel.getActFrage() + "</p></html>");
		sp1.setAntworten(spiel.getAntwort(z, 0), spiel.getAntwort(z, 1), spiel.getAntwort(z, 2), spiel.getAntwort(z, 3));

		sp2.setLblFrage("<html><p>" + spiel.getActFrage() + "</p></html>");
		sp2.setAntworten(spiel.getAntwort(z, 0), spiel.getAntwort(z, 1), spiel.getAntwort(z, 2), spiel.getAntwort(z, 3));
		////
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
	 * Aktualisiert den Titel, sodass die aktuelle Runde und Fragennummer angezeigt wird
	 */
	private void updateTitle() {
		frame.setTitle("Runde: " + runde + " | Frage: " + frage); 
		frage++; 
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
		
		
		cons = new Consumer<Integer>() {
			public void accept(Integer i) {
				ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
				//Entscheidung ob Warten auf anderen Spieler noetig
				if (i == 1) {
					if(!sp1.getSpieler().getAuswahl().equals("")) {
						sp1.getSpieler().setBereit(true);
						sp1.setLblStatus("Warte auf Spieler 2");
						sp1.changeRdbtnState(false);
						sp1.getBtnAccept().setEnabled(false);
					}
					else {
						sp1.setLblStatus("Bitte waehle zuerst eine Antwort!");
						return;
					}
				}
				else {
					if(!sp2.getSpieler().getAuswahl().equals("")) {
						sp2.getSpieler().setBereit(true);
						sp2.setLblStatus("Warte auf Spieler 1");
						sp2.changeRdbtnState(false);
						sp2.getBtnAccept().setEnabled(false);
					}
					else {
						sp2.setLblStatus("Bitte waehle zuerst eine Antwort!");
						return;
					}
				}
				//Wenn beide Spieler bereit: Punkte aktualisieren, richtige Antwort anzeigen und Ausgangszustand fuer naechste Fragerunde herstellen
				if(sp1.getSpieler().getBereit() && sp2.getSpieler().getBereit()) {
					if(sp1.getSpieler().getAuswahl().equals(spiel.getActValues()[4])) {
						sp1.getSpieler().setPunkte(sp1.getSpieler().getPunkte()+1);
						lblScore.setText(sp1.getSpieler().getPunkte() + ":" + sp2.getSpieler().getPunkte());
						sp1.setLblStatus("<HTML><BODY BGCOLOR=#4EFF01>Richtig!</BODY></HTML>");
						executor.schedule(() -> {
							sp1.setLblStatus("Richtig!");
					    }, 2, TimeUnit.SECONDS);
					}
					else {
						sp1.setLblStatus("<HTML><BODY BGCOLOR=#FFCCCC>Leider falsch!</BODY></HTML>");
						executor.schedule(() -> {
							sp1.setLblStatus("Leider falsch!");
					    }, 2, TimeUnit.SECONDS);
					}
					if(sp2.getSpieler().getAuswahl().equals(spiel.getActValues()[4])) {
						sp2.getSpieler().setPunkte(sp2.getSpieler().getPunkte()+1);
						lblScore.setText(sp1.getSpieler().getPunkte() + ":" + sp2.getSpieler().getPunkte());
						sp2.setLblStatus("<HTML><BODY BGCOLOR=#4EFF01>Richtig!</BODY></HTML>");
						executor.schedule(() -> {
							sp2.setLblStatus("Richtig!");
					    }, 2, TimeUnit.SECONDS);
					}
					else {
						sp2.setLblStatus("<HTML><BODY BGCOLOR=#FFCCCC>Leider falsch!</BODY></HTML>");
						executor.schedule(() -> {
							sp2.setLblStatus("Leider falsch!");
					    }, 2, TimeUnit.SECONDS);
					}
					sp1.getSpieler().setBereit(false);
					sp2.getSpieler().setBereit(false);
					sp1.getSpieler().setAuswahl("");
					sp2.getSpieler().setAuswahl("");
					bereit.release();
				}
			}
		};
		gbc_panel1 = new GridBagConstraints();
		gbc_panel1.insets = new Insets(0, 0, 0, 5);
		gbc_panel1.fill = GridBagConstraints.BOTH;
		gbc_panel1.gridx = 0;
		gbc_panel1.gridy = 0;
		gbc_panel1.weightx = 0.5;
		sp1 = new SpielerPanel(spiel, cons);
		contentPane.add(sp1.getPanel(), gbc_panel1);
		
		
		initPanel2();
		
		gbc_panel3 = new GridBagConstraints();
		gbc_panel3.fill = GridBagConstraints.BOTH;
		gbc_panel3.gridx = 2;
		gbc_panel3.gridy = 0;
		gbc_panel3.weightx = 0.5;
		sp2 = new SpielerPanel(spiel,  cons);
		contentPane.add(sp2.getPanel(), gbc_panel3);
	}
}
