package aop_pruefung;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class MainGUI extends JFrame {
	Vector<File> dateien = null;
	private JPanel contentPane;
	JPanel panel;
	File pfad;
	JButton btnEinzelspieler, btnMehrspieler, btnPfad, btnEditor, btnChooseDir;
	JLabel lblPfad, lblFiles;
	private JButton btnInst;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI main = new MainGUI();
					main.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainGUI() {
		setTitle("Modusauswahl");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 589, 302);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		btnEinzelspieler = new JButton("Einzelspieler (vs. COM)");
		btnEinzelspieler.addActionListener(e-> singleplayerMode());
		btnEinzelspieler.setBounds(322, 56, 183, 30);
		panel.add(btnEinzelspieler);
		
		lblPfad = new JLabel("");
		lblPfad.setBounds(232, 167, 321, 14);
		panel.add(lblPfad);
		
		btnPfad = new JButton("Dateien wählen");
		btnPfad.addActionListener(e -> chooseFiles());
		btnPfad.setBounds(60, 205, 162, 23);
		panel.add(btnPfad);
		
		btnEditor = new JButton("Editor");
		btnEditor.addActionListener(e-> editorMode());
		btnEditor.setBounds(60, 56, 162, 71);
		panel.add(btnEditor);
		
		btnMehrspieler = new JButton("Mehrspieler");
		btnMehrspieler.addActionListener(e-> multiplayerMode());
		btnMehrspieler.setBounds(322, 97, 183, 30);
		panel.add(btnMehrspieler);
		
		btnChooseDir = new JButton("Ordner wählen");
		btnChooseDir.addActionListener(e-> chooseDir());
		btnChooseDir.setBounds(60, 163, 162, 23);
		panel.add(btnChooseDir);
		
		lblFiles = new JLabel("");
		lblFiles.setBounds(232, 209, 321, 14);
		panel.add(lblFiles);
		
		btnInst = new JButton("Anleitung");
		btnInst.addActionListener(e-> instructions());
		btnInst.setBounds(0, 0, 115, 23);
		panel.add(btnInst);
	}
	
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
			lblPfad.setText("Bitte erst die Kategorien auswählen!");	
		}	
	}
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
			lblPfad.setText("Bitte erst die Kategorien auswählen!");	
		}
	}
	
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
			lblPfad.setText("Bitte erst die Kategorien auswählen!");
		}
	}
	
	private void chooseFiles() {
		JFileChooser chooser = new JFileChooser();
		pfad = null;
		lblPfad.setText("");
		chooser.setMultiSelectionEnabled(true);
		chooser.showOpenDialog(getParent());
		File[] temp = chooser.getSelectedFiles();
		dateien = new Vector<File>();
		for(File a : temp) {
			dateien.add(a);
		}
		lblFiles.setText(dateien.toString());
	}
	private void chooseDir() {
		JFileChooser chooser = new JFileChooser();
		dateien = null;
		lblFiles.setText("");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    if(chooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
	    	lblPfad.setText("Pfad: " + chooser.getSelectedFile());
	    	pfad = chooser.getSelectedFile();
	    }
	}
	
	private void instructions() {
		String msg = "<html><h1>Anleitung</h1>\r\n" + 
				"<ol>\r\n" + 
				"    <li>Waehle zuerst deinen Datensatz aus. Falls du noch keinen hast, waehle einen Ordner, in dem du einen erstellen moechtest.&nbsp;</li>" + 
				"    <li>Im Editor kannst du deinen Kategorien neue Fragen hinzufuegen, Fragen loeschen oder veraendern und neue Kategorien erstellen.</li>" + 
				"    <li>Willst du spielen, so waehle den gewuenschten Modus aus. Einzelspieler oder Mehrspieler (2 Spieler).</li>" + 
				"</ol>" + 
				"<h3>So wird gespielt</h3>" + 
				"<ol>" + 
				"    <li>Es werden 3 Runden gespielt</li>" + 
				"    <li>In jeder Runde darf erst Spieler 1, danach Spieler 2 eine Kategorie waehlen.</li>" + 
				"    <li>Pro Kategorie werden dann 3 Fragen gestellt, jede richtige Antwort gibt 1 Punkt!</li>" + 
				"</ol>" + 
				"<p><span style=\"color: rgb(209, 72, 65);\">Viel Spass!</span></p></html>";
		JLabel message = new JLabel(msg);
		JOptionPane.showMessageDialog(getParent(), message);
	}
}
