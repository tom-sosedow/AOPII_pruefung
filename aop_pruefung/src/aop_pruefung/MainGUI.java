package aop_pruefung;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.util.Vector;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.FlowLayout;

/**
 * Beinhaltet die main welche das Startfenster erstellt. Von hier aus wird das Datenset fuer die 3 Modi gewaehlt und die Anleitung angezeigt.
 * @author Tom Sosedow
 *
 */
public class MainGUI extends JFrame {
	private Vector<File> dateien = null;
	private JPanel contentPane, panelBottom, panelTop;
	private File pfad;
	private JButton btnEinzelspieler, btnMehrspieler, btnPfad, btnEditor, btnChooseDir, btnInst;
	private JLabel lblPfad, lblFiles, lblPlatzhalter;
	
	public static void main(String[] args) {
		try {
			MainGUI main = new MainGUI();
			main.setVisible(true);
		} catch (Exception e) {
			System.out.println("Etwas lief schief");
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialisieren der GUI des Hauptfensters
	 */
	public MainGUI() {
		setTitle("Hauptmenue");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 935, 381);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		panelBottom = new JPanel();
		contentPane.add(panelBottom, BorderLayout.CENTER);
		panelBottom.setLayout(new GridLayout(0, 2, 10, 10));
		
		btnEditor = new JButton("Editor");
		btnEditor.addActionListener(e-> editorMode());
		panelBottom.add(btnEditor);
		
		btnEinzelspieler = new JButton("Einzelspieler (vs. COM)");
		btnEinzelspieler.addActionListener(e-> singleplayerMode());
		panelBottom.add(btnEinzelspieler);
		
		lblPlatzhalter = new JLabel("");
		panelBottom.add(lblPlatzhalter);
		
		btnMehrspieler = new JButton("Mehrspieler");
		btnMehrspieler.addActionListener(e-> multiplayerMode());
		panelBottom.add(btnMehrspieler);
		
		btnChooseDir = new JButton("Ordner waehlen");
		btnChooseDir.addActionListener(e-> chooseDir());
		panelBottom.add(btnChooseDir);
		
		lblPfad = new JLabel("");
		panelBottom.add(lblPfad);
		
		btnPfad = new JButton("Dateien waehlen");
		btnPfad.addActionListener(e -> chooseFiles());
		panelBottom.add(btnPfad);
		
		lblFiles = new JLabel("");
		panelBottom.add(lblFiles);
		
		panelTop = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelTop.getLayout();
		flowLayout.setHgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(panelTop, BorderLayout.NORTH);
		
		btnInst = new JButton("Anleitung");
		panelTop.add(btnInst);
		btnInst.addActionListener(e-> instructions());
	}
	
	/**
	 * Öffnet das Fenster fuer den Multiplayermode, falls Dateien oder ein Verzeichnis gewaehlt wurden
	 */
	private void multiplayerMode() {
		MultiplayerGUI multiplayer;
		if(dateien != null) {
			multiplayer = new MultiplayerGUI(dateien);
			multiplayer.setVisible(true);
			multiplayer.spielen();
		}
		else if(pfad != null) {
			multiplayer = new MultiplayerGUI(pfad);
			multiplayer.setVisible(true);
			multiplayer.spielen();
		}
		else {
			lblPfad.setText("<- Bitte erst die Kategorien auswaehlen!");
			lblFiles.setText("<-");
		}	
	}
	
	/**
	 * Öffnet das Fenster fuer den Singleplayermode, falls Dateien oder ein Verzeichnis gewaehlt wurden
	 */
	private void singleplayerMode() {
		SingleplayerGUI singleplayer;
		if(dateien != null) {
			singleplayer = new SingleplayerGUI(dateien);
			singleplayer.setVisible(true);
			singleplayer.spielen();
		}
		else if(pfad != null) {
			singleplayer = new SingleplayerGUI(pfad);
			singleplayer.setVisible(true);
			singleplayer.spielen();
		}
		else {
			lblPfad.setText("<- Bitte erst die Kategorien auswaehlen!");
			lblFiles.setText("<-");	
		}
	}
	/**
	 * Öffnet das Editorfenster, falls Dateien oder ein Verzeichnis gewaehlt wurden
	 */
	private void editorMode() {
		EditorGUI editor;
		if(dateien != null) {
			editor = new EditorGUI(dateien);
			editor.setVisible(true);
		}
		else if(pfad != null) {
			editor = new EditorGUI(pfad);
			editor.setVisible(true);
		}
		else {
			lblPfad.setText("<- Bitte erst die Kategorien auswaehlen!");
			lblFiles.setText("<-");
		}
	}
	
	/**
	 * Öffnet ein Pop-Up um Datei/-en auszuwaehlen.
	 */
	private void chooseFiles() {
		JFileChooser chooser = new JFileChooser();
		File[] temp;
		String temp2 ="";
		pfad = null;
		chooser.setMultiSelectionEnabled(true);
		chooser.showOpenDialog(getParent());
		temp = chooser.getSelectedFiles();
		if(temp.length>0) {
			dateien = new Vector<File>();
			for(File a : temp) {
				dateien.add(a);
				temp2 += a.getName() + "; ";
			}
			lblFiles.setText(temp2);
			lblPfad.setText("");
		}
	}
	
	/**
	 * Öffnet ein Pop-Up um ein Verzeichnis/Ordner auszuwaehlen.
	 */
	private void chooseDir() {
		JFileChooser chooser = new JFileChooser();
		dateien = null;
		
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    if(chooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
	    	pfad = chooser.getSelectedFile();
	    	lblPfad.setText("Pfad: " + pfad);
	    	lblFiles.setText("");
	    }
	}
	
	/**
	 * Öffnet ein Pop-Up in dem die Spielregeln und eine Anleitung gezeigt wird.
	 * HTML erstellt mithilfe von https://wordtohtml.net/
	 */
	private void instructions() {
		String msg = "<html><h1>Anleitung</h1>\r\n" + 
				"<ol>\r\n" + 
				"    <li>Waehle zuerst deinen Datensatz aus. Falls du noch keinen hast, waehle einen Ordner in dem du einen erstellen moechtest.</li>" + 
				"    <li>Im Editor kannst du deinen Kategorien neue Fragen hinzufuegen, Fragen loeschen oder veraendern und neue Kategorien erstellen.</li>" + 
				"    <li>Willst du spielen, so waehle den gewuenschten Modus aus. Einzelspieler oder Mehrspieler (2 Spieler).<br>" + 
					"Beim Einzelspieler musst du eine Schwierigkeit fuer deinen Gegner (COM) auswaehlen. Falls du nichts auswaehlst, wird die schwierigste Stufe gewaehlt. <br>" +
					"Dein Gegner waehlt selbstaendig seine Kategorie und seine Antworten!</li>" + 
				"</ol>" + 
				"<h3>So wird gespielt</h3>" + 
				"<ol>" + 
				"    <li>Es werden 3 Runden gespielt</li>" + 
				"    <li>In jeder Runde darf erst Spieler 1(links), danach Spieler 2(rechts) eine Kategorie waehlen.</li>" + 
				"    <li>Pro Kategorie werden dann 3 Fragen gestellt, jede richtige Antwort gibt 1 Punkt!</li>" + 
				"    <li>Wenn beide Spieler ihre Antwort abgegeben haben, wird die Loesung praesentiert und dann die naechste Frage gezeigt.</li>" + 
				"</ol>" + 
				"<p><span style=\"color: rgb(209, 72, 65);\">Viel Spass!</span></p></html>";
		JLabel message = new JLabel(msg);
		JOptionPane.showMessageDialog(getParent(), message);
	}
}
