package aop_pruefung;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Verwaltet das Fenster des Editormodus. Hier wird der zuvor ausgewaehlte Datensatz geaendert oder ein neuer erstellt.
 * @author Tom Sosedow
 *
 */

public class EditorGUI extends JFrame {

	private Map<String, String[]> kategorie = new HashMap<>(); //Beinhaltet zu jeder Frage die dazugehoerigen Antworten und die richtige Antwort
	private File actFile;
	private JLabel lblFragen, lblrAntwort, lblA, lblB, lblC, lblD;
	private JButton btnSave, btnAddQ, btnAccept, btnDelete, btnNewCategory;
	private JComboBox<File> cbFiles;
	private JComboBox<String> cbRAntwort;
	private String[] ABCD = {"A", "B", "C", "D"};
	private JTextArea textArea_B,textArea_A, textArea_C, textArea_D, textArea_Frage;
	private JScrollPane scrollPane;
	private Vector<File> dateien;
	private DefaultListModel<String> model = new DefaultListModel<String>();
	private JList<String> list = new JList<String>(model);
	private int modus = 0;
	
	/**
	 * Initialisiert das Fenster und nutzt dabei den uebergebenen Vektor (Files)
	 * @param files Vektor mit den File-Daten (Kategorien)
	 */
	public EditorGUI(Vector<File> dateien) {
		this.dateien = dateien;
		initGUI();
	}
	
	/**
	 * Speichert die Kategorie im richtigen Format in die aktuelle Datei
	 */
	private void saveFile() {
		try {
			OutputStream ostream = new FileOutputStream(actFile);
			PrintStream writer = new PrintStream(ostream,true,"ISO8859_1");
			for (Map.Entry<String, String[]> a : kategorie.entrySet()) {
				writer.println(a.getKey().replace("\n", ""));
				for(int i = 0; i<5; i++) {
					writer.println(a.getValue()[i].replace("\n", ""));
				}
				writer.println("");
			}
			writer.close();
			ostream.close();
		}
		catch(IOException e) {
			System.out.println("Fehler");
		}
	}
	
	/**
	 * Leert die Felder fuer Frage+Antworten+richtige Antwort. 
	 * Die naechste Speicherung einer Frage wird eine neue Frage zur Datenbank hinzufuegen
	 * 
	 */
	private void addQ(){
		if(modus == 0) {
			btnAddQ.setText("Abbrechen");
			list.clearSelection();
			textArea_Frage.setText("");
			textArea_A.setText("");
			textArea_B.setText("");
			textArea_C.setText("");
			textArea_D.setText("");
			cbRAntwort.setSelectedItem("A");
			modus = 1;
		}
		else {
			btnAddQ.setText("Frage hinzufuegen");
			aktualisieren();
			modus = 0;
			
		}
	}
	
	/**
	 * Die Frage/Antworten der aktuell gewaehlten Frage werden aktualisiert bzw. eine neue Frage hinzugefuegt, falls vorher entsprechender
	 * Button betaetigt wurde.
	 */
	private void modifyQ() {
		String[] temp = {textArea_A.getText(), textArea_B.getText(), textArea_C.getText(), textArea_D.getText(), (String) cbRAntwort.getSelectedItem()};
		String key = list.getSelectedValue();
		String frage = textArea_Frage.getText();
		if (modus == 0) {		
			model.removeElement(key);
			kategorie.remove(key);
			kategorie.put(frage, temp);
			model.addElement(frage);
			list.setSelectedValue(frage, true);
		}
		else if(kategorie.containsKey(frage) && modus == 1) {
			  kategorie.replace(frage, temp);
		}
		else if (modus == 1){
			 kategorie.put(frage, temp);
			 model.addElement(frage); 
			 list.setSelectedValue(frage, true);
		}
		modus = 0;
		btnAddQ.setText("Frage hinzufuegen");
	}
	
	/**
	 * Entfernt die gewaehlte Frage aus der Datenbank, sofern sie enthalten ist.
	 */
	private void deleteQ() {
		if(kategorie.containsKey(list.getSelectedValue())) {
			  kategorie.remove(textArea_Frage.getText());
			  model.removeElement(textArea_Frage.getText());
		  }
	}
	
	/**
	 * Oeffnet ein Fenster, in dem der Name einer neuen Kategorie eingegeben werden kann.
	 * Wird im Verzeichnis/dem Verzeichnis der gewaehlten Dateien eine neue Datei mit diesem Namen erstellen.
	 */
	private void newCategory() {
		String name = JOptionPane.showInputDialog("Gib den Namen der neuen Kategorie ein:");
		File datei;
		if(name != null && name != "") {
			textArea_Frage.setText("");
			textArea_A.setText("");
			textArea_B.setText("");
			textArea_C.setText("");
			textArea_D.setText("");
			datei = new File(dateien.elementAt(0).getParent()+"\\"+name+".txt");
			try {
				datei.createNewFile();
				cbFiles.addItem(datei);
				cbFiles.setSelectedItem(datei);
				selectFile();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(getParent(), "Fehler beim Erstellen der Datei", "Fehler", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	/**
	 * Aktualisiert die Frage/Antwort-Felder mit den Informationen aus der Datenbank von der gewaehlten Frage
	 */
	private void aktualisieren() {
		try {
			textArea_Frage.setText((String) list.getSelectedValue());
			textArea_A.setText(kategorie.get(list.getSelectedValue())[0]);
			textArea_B.setText(kategorie.get(list.getSelectedValue())[1]);
			textArea_C.setText(kategorie.get(list.getSelectedValue())[2]);
			textArea_D.setText(kategorie.get(list.getSelectedValue())[3]);
			cbRAntwort.setSelectedItem(kategorie.get(list.getSelectedValue())[4]);
		}
		catch(NullPointerException e) {
			//Entsteht nur, wenn der Nutzer ins Feld klickt, wo kein Element ist -> muss nichts unternehmen
		}
		
	}
	
	/**
	 * Liest die Datei {@code datei} in die Datenbank ein
	 * @param datei Datei zum Einlesen
	 * @return true, falls Einlesen erfolgreich; false sonst
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
	 * Probiert, die gewaehlte Datei in die Datenbank einzulesen.
	 * Falls erfolgreich, wird die Anzeige der Fragen mit den neuen Fragen aktualisiert
	 */
	private void selectFile() {
		File temp = dateien.elementAt(cbFiles.getSelectedIndex());
		kategorie.clear();
		boolean i = readFile(temp);
		    if(i) {
		    	actFile = temp;
		    	model.clear();
			    for (Map.Entry<String, String[]> a : kategorie.entrySet()) {
			    	model.addElement(a.getKey());
				}
		    }
		    else {
		    	readFile(actFile);
		    }
		    textArea_Frage.setText("");
			textArea_A.setText("");
			textArea_B.setText("");
			textArea_C.setText("");
			textArea_D.setText("");
			cbRAntwort.setSelectedIndex(0);
	}
	
	/**
	 * Initialisiert die Oberflaeche:
	 * Erstellt alle Buttons, Textfelder, Label, das Frame und die Liste zur Anzeige der Fragen aus der Datenbank
	 */
	private void initGUI() {
		this.setTitle("Editor");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 986, 551);	
		getContentPane().setLayout(null);
		
		lblFragen = new JLabel("Fragen:");
		lblFragen.setBounds(27, 50, 121, 14);
		getContentPane().add(lblFragen);
		
		textArea_A = new JTextArea();
		textArea_A.setText("Antwort 1");
		textArea_A.setBounds(599, 75, 351, 65);
		textArea_A.setLineWrap(true);
		getContentPane().add(textArea_A);
		
		textArea_B = new JTextArea();
		textArea_B.setText("Antwort 2");
		textArea_B.setBounds(599, 151, 351, 65);
		textArea_B.setLineWrap(true);
		getContentPane().add(textArea_B);
		
		textArea_C = new JTextArea();
		textArea_C.setText("Antwort 3");
		textArea_C.setBounds(599, 227, 351, 65);
		textArea_C.setLineWrap(true);
		getContentPane().add(textArea_C);
		
		textArea_D = new JTextArea();
		textArea_D.setText("Antwort 4");
		textArea_D.setBounds(599, 303, 351, 65);
		textArea_D.setLineWrap(true);
		getContentPane().add(textArea_D);
		
		cbRAntwort = new JComboBox<String>(ABCD);
		cbRAntwort.setBounds(599, 400, 121, 22);
		getContentPane().add(cbRAntwort);
		
		lblrAntwort = new JLabel("Richtige Antwort:");
		lblrAntwort.setBounds(599, 379, 182, 14);
		getContentPane().add(lblrAntwort);
		
		lblA = new JLabel("A:");
		lblA.setBounds(558, 80, 31, 14);
		getContentPane().add(lblA);
		
		lblB = new JLabel("B:");
		lblB.setBounds(558, 156, 31, 14);
		getContentPane().add(lblB);
		
		lblC = new JLabel("C:");
		lblC.setBounds(558, 232, 31, 14);
		getContentPane().add(lblC);
		
		lblD = new JLabel("D:");
		lblD.setBounds(558, 308, 31, 14);
		getContentPane().add(lblD);
		
		textArea_Frage = new JTextArea();
		textArea_Frage.setBounds(558, 10, 392, 56);
		textArea_Frage.setText("Frage");
		textArea_Frage.setLineWrap(true);
		getContentPane().add(textArea_Frage);
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				aktualisieren();
			}
		});
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(27, 75, 500, 348);
		getContentPane().add(scrollPane);
		scrollPane.setViewportView(list);
		
		btnSave = new JButton("Datei speichern");
		btnSave.addActionListener(e -> saveFile());
		btnSave.setBounds(359, 11, 168, 23);
		getContentPane().add(btnSave);
		
		btnAddQ = new JButton("Frage hinzufuegen");
		btnAddQ.addActionListener(e -> addQ());
		btnAddQ.setBounds(27, 448, 168, 23);
		getContentPane().add(btnAddQ);

		btnAccept = new JButton("Bestaetigen");
		btnAccept.setBounds(739, 400, 121, 23);
		btnAccept.addActionListener(e -> modifyQ());
		getContentPane().add(btnAccept);
		
		btnDelete = new JButton("Frage loeschen");
		btnDelete.addActionListener(e -> deleteQ());
		btnDelete.setBounds(359, 448, 168, 23);
		getContentPane().add(btnDelete);
				
		cbFiles = new JComboBox<File>(dateien);
		cbFiles.addActionListener(e -> selectFile());
		cbFiles.setBounds(27, 11, 322, 22);
		cbFiles.setSelectedIndex(0);
		selectFile();
		getContentPane().add(cbFiles);
		
		btnNewCategory = new JButton("Neue Kategorie");
		btnNewCategory.addActionListener(e -> newCategory());
		btnNewCategory.setBounds(359, 46, 168, 23);
		getContentPane().add(btnNewCategory);
	}
}
