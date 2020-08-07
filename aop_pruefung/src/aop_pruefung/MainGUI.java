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
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class MainGUI extends JFrame {
	File pfad = null;
	private JPanel contentPane;
	JPanel panel;
	JButton btnEinzelspieler, btnMehrspieler, btnPfad, btnEditor;
	JLabel lblPfad;
	
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
		btnEinzelspieler.addActionListener(e-> multiplayerMode());
		btnEinzelspieler.setBounds(322, 56, 183, 30);
		panel.add(btnEinzelspieler);
		
		lblPfad = new JLabel("Pfad:");
		lblPfad.setBounds(232, 194, 273, 14);
		panel.add(lblPfad);
		
		btnPfad = new JButton("Pfad auswählen");
		btnPfad.addActionListener(e -> chooseFile());
		btnPfad.setBounds(60, 190, 162, 23);
		panel.add(btnPfad);
		
		btnEditor = new JButton("Editor");
		btnEditor.addActionListener(e-> editorMode());
		btnEditor.setBounds(60, 56, 162, 71);
		panel.add(btnEditor);
		
		btnMehrspieler = new JButton("Mehrspieler");
		btnMehrspieler.addActionListener(e-> multiplayerMode());
		btnMehrspieler.setBounds(322, 97, 183, 30);
		panel.add(btnMehrspieler);
	}
	
	private void multiplayerMode() {
		MultiplayerGUI multiplayer;
		if(pfad != null) {
			multiplayer = new MultiplayerGUI(pfad);
			multiplayer.setVisible(true);
			multiplayer.spielen();
		}
		else {
			lblPfad.setText("Bitte erst den Pfad zu den Kategorien auswählen!");	
		}	
	}
	private void singleplayerMode() {
		SingleplayerGUI singleplayer;
		if(pfad != null) {
			singleplayer = new SingleplayerGUI(pfad);
			singleplayer.setVisible(true);
			singleplayer.spielen();
		}
		else {
			lblPfad.setText("Bitte erst den Pfad zu den Kategorien auswählen!");	
		}
	}
	
	private void editorMode() {
		if(pfad != null) {
			EditorGUI editor = new EditorGUI(pfad);
			editor.setVisible(true);
		}
		else {
			lblPfad.setText("Bitte erst den Pfad zu den Kategorien auswählen!");
		}
	}
	
	private void chooseFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    //chooser.setAcceptAllFileFilterUsed(false);
	    if(chooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
	    	lblPfad.setText("Pfad: " + chooser.getSelectedFile());
	    	pfad = chooser.getSelectedFile();
	    }
	}
}
