package aop_pruefung;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileFilter;
import java.util.Vector;
import java.util.function.Consumer;

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
	private JButton btnEinzelspieler, btnMehrspieler, btnPfad, btnEditor, btnChooseDir, btnInst;
	private JLabel lblPfad, lblFiles, lblPlatzhalter;
	private Consumer<Boolean> cons; //accept() dient als Info, dass Dateienvektor geaendert wurde
	private int size;
	private MultiplayerGUI multiplayer;
	private SingleplayerGUI singleplayer;
	private EditorGUI editor;
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
		
		lblPfad = new JLabel("Ordner: ");
		panelBottom.add(lblPfad);
		
		btnPfad = new JButton("Dateien waehlen");
		btnPfad.addActionListener(e -> chooseFiles());
		panelBottom.add(btnPfad);
		
		lblFiles = new JLabel("[   ]");
		panelBottom.add(lblFiles);
		
		panelTop = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelTop.getLayout();
		flowLayout.setHgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(panelTop, BorderLayout.NORTH);
		
		btnInst = new JButton("Anleitung");
		panelTop.add(btnInst);
		btnInst.addActionListener(e-> instructions());
		
		cons = new Consumer<Boolean>() {

			@Override
			public void accept(Boolean t) {
				if(t) {
					dateien = editor.getDateien();
					if(!lblFiles.getText().equals("[   ]")) {
						lblFiles.setText(lblFiles.getText()+ dateien.elementAt(dateien.size()-1) + "; ");
					}
				}
			}
			
		};
		
		this.setResizable(false);
	}
	
	/**
	 * Oeffnet das Fenster fuer den Multiplayermode, falls Dateien oder ein Verzeichnis gewaehlt wurden
	 */
	private void multiplayerMode() {
		
		if(dateien != null) {
			multiplayer = new MultiplayerGUI(dateien);
			multiplayer.setVisible(true);
			multiplayer.spielen();
		}
		else {
			lblPfad.setText("<- Bitte erst (nicht leere) Kategorien/Ordner auswaehlen!");
			lblFiles.setText("<-");
		}	
	}
	
	/**
	 * Oeffnet das Fenster fuer den Singleplayermode, falls Dateien oder ein Verzeichnis gewaehlt wurden
	 */
	private void singleplayerMode() {
		if(dateien != null) {
			singleplayer = new SingleplayerGUI(dateien);
			singleplayer.setVisible(true);
			singleplayer.spielen();
		}
		else {
			lblPfad.setText("<- Bitte erst (nicht leere) Kategorien/Ordner auswaehlen!");
			lblFiles.setText("<-");	
		}
	}
	
	/**
	 * Oeffnet das Editorfenster, falls Dateien oder ein Verzeichnis gewaehlt wurden
	 */
	private void editorMode() {
		if(dateien != null) {
			editor = new EditorGUI(dateien, cons);
			editor.setVisible(true);
		}
		else {
			lblPfad.setText("<- Bitte erst (nicht leere) Kategorien/Ordner auswaehlen!");
			lblFiles.setText("<-");
		}
	}
	
	/**
	 * Oeffnet ein Pop-Up um Datei/-en auszuwaehlen und speichert sie.
	 */
	private void chooseFiles() {
		JFileChooser chooser = new JFileChooser();
		File[] temp;
		String temp2 ="";
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.showOpenDialog(getParent());
		temp = chooser.getSelectedFiles();
		if(temp.length>0) {
			dateien = new Vector<File>();
			for(File a : temp) {
				dateien.add(a);
				temp2 += a.getName() + "; ";
			}
			lblFiles.setText(temp2);
			lblPfad.setText("Ordner: ");
			size = dateien.size();
		}
	}
	
	/**
	 * Oeffnet ein Pop-Up um ein Verzeichnis/Ordner auszuwaehlen und speichert den Inhalt dessen.
	 */
	private void chooseDir() {
		JFileChooser chooser = new JFileChooser();
		dateien = null;
		
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    if(chooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
	    	File pfad = chooser.getSelectedFile();
	    	dateien = new Vector<File>();
	    	lblPfad.setText("Ordner: " + pfad);
	    	lblFiles.setText("[   ]");
	    	File[] ls = pfad.listFiles(new FileFilter() {
				public boolean accept(File f) {
						return f.isFile()&&f.getName().endsWith(".txt");}});
			
			if (ls != null && ls.length != 0) 
				for(int i = 0; i< ls.length; i++) 
					dateien.add(ls[i]);
			
			size = dateien.size();
	    }
	    if(size==0) {
	    	dateien = null;
	    }
	}
	
	/**
	 * Oeffnet ein Pop-Up in dem die Spielregeln und eine Anleitung gezeigt wird.
	 * HTML erstellt mithilfe von https://wordtohtml.net/
	 */
	private void instructions() {
		String msg = "<html><h1>Anleitung</h1>\r\n" + 
				"<p>Zu Beginn musst du deine Kategoriedateien auswaehlen. Bei \"Ordner waehlen\" kannst du einen Ordner waehlen, in dem sich all deine Dateien befinden. <br>Falls du nur bestimmte Kategorien/Dateien in die Datenbank aufnehmen willst, "+
				"kannst du bei \"Dateien waehlen\" <br>einzelne aussuchen indem du die STRG-Taste gedrueckt haeltst und dann die gewuenschten Dateien auswaehlst. <br>Achtung! eine Kategorie muss mindestens 3 Fragen beinhalten!<br></p>"+
				"<p>Das Programm bietet 3 dann verschiedene Modi:</p>\r\n" + 
				"<ul>\r\n" + 
				"    <li><strong>Editormodus</strong>\r\n" + 
				"        <ul>\r\n" + 
				"            <li>Hier kannst du deine Kategorien bearbeiten oder neue erstellen.</li>\r\n" + 
				"            <li>Waehle eine Kategorie in dem Drop-Down Menue aus und waehle eine Frage aus, um sie anschliessend rechts bearbeiten zu koennen. <br>Bist du fertig, druecke auf den Button \"Bestaetigen\".</li>\r\n" + 
				"            <li>Um eine neue Frage hinzuzufuegen, druecke auf \"Frage hinzufuegen\". <br>Das naechste Mal, wenn du auf \"Bestaetigen\" drueckst, wird die Frage in deine Kategorie aufgenommen.</li>\r\n" + 
				"            <li>Moechtest du eine Frage entfernen, waehle sie einfach aus und druecke auf \"Frage Loeschen\".</li>\r\n" + 
				"            <li>Vergiss nicht, am Ende deine Aenderungen an einer Kategorie/Datei zu speichern, in dem du auf \"Datei speichern\" drueckst.</li>\r\n" + 
				"        </ul>\r\n" + 
				"    </li>\r\n" + 
				"    <li><strong>Spielen</strong>\r\n" + 
				"        <ul>\r\n" + 
				"            <li>Es werden 3 Runden gespielt. In jeder Runde darf erst der linke Spieler (1), danach der rechte Spieler (2) eine Kategorie waehlen. <br>Pro Kategorie werden nacheinander 3 Fragen gestellt und jede richtige Antwort gibt 1 Punkt. Wer am Ende mehr Punkte hat, gewinnt!</li>\r\n" + 
				"            <li><u>Singleplayer Modus</u>\r\n" + 
				"                <ul>\r\n" + 
				"                    <li>Hier spielst du gegen einen Bot. Am Anfang waehlst du aus, wie stark dieser sein soll. Aber pass auf! <br>Falls du nichts auswaehlst, wird die schwierigste Stufe gewaehlt! Der Bot waehlt automatisch seine Fragen und Kategorien und ist immer bereit.</li>\r\n" + 
				"                </ul>\r\n" + 
				"            </li>\r\n" + 
				"            <li><u>Multiplayer Modus</u>\r\n" + 
				"                <ul>\r\n" + 
				"                    <li>Hier trittst du gegen einen echten anderen Spieler an. Wenn ihr beide eure Antwort gewaehlt habt, <br>wird die Loesung praesentiert und die naechste Frage nach kurzer Zeit eingeblendet.</li>\r\n" + 
				"                </ul>\r\n" + 
				"            </li>\r\n" + 
				"        </ul>\r\n" + 
				"    </li>\r\n" + 
				"</ul>\r\n" + 
				"<p><span style=\"color: rgb(184, 49, 47);\"><strong><u>Viel Spass!</u></strong></p></html>";
		JOptionPane.showMessageDialog(getParent(), new JLabel(msg));
	}
}
