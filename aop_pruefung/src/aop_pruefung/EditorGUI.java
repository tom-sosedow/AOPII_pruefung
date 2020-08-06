package aop_pruefung;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileFilter;
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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditorGUI extends JFrame {

	private JPanel contentPane;
	Map<String, String[]> kategorie = new HashMap<>();
	File pfad, actFile, datei;
	File[] ls = null;
	JPanel panel;
	JLabel lblFragen, lblrAntwort, lblA, lblB, lblC, lblD;
	JButton btnSave, btnAddQ, btnAccept, btnDelete, btnNewCategory;
	JComboBox<File> comboBox;
	JComboBox<?> comboBox_rAntwort;
	String[] ABCD = {"A", "B", "C", "D"};
	JTextArea textArea_B,textArea_A, textArea_C, textArea_D, textArea_Frage;
	JScrollPane scrollPane;
	Vector<File> dateien = new Vector<File>();
	DefaultListModel<String> model = new DefaultListModel<String>();
	JList<String> list = new JList<String>( model );
	int modus = 0;
	
	public EditorGUI(File pfad) {
		this.pfad = pfad;
		setTitle("Editor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 875, 551);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		lblFragen = new JLabel("Fragen:");
		lblFragen.setBounds(27, 50, 46, 14);
		panel.add(lblFragen);
		
		textArea_B = new JTextArea();
		textArea_B.setText("Antwort 2");
		textArea_B.setBounds(599, 134, 240, 48);
		panel.add(textArea_B);
		
		textArea_A = new JTextArea();
		textArea_A.setText("Antwort 1");
		textArea_A.setBounds(599, 75, 240, 48);
		panel.add(textArea_A);
		
		textArea_C = new JTextArea();
		textArea_C.setText("Antwort 3");
		textArea_C.setBounds(599, 193, 240, 48);
		panel.add(textArea_C);
		
		textArea_D = new JTextArea();
		textArea_D.setText("Antwort 4");
		textArea_D.setBounds(599, 252, 240, 48);
		panel.add(textArea_D);
		
		comboBox_rAntwort = new JComboBox<Object>(ABCD);
		comboBox_rAntwort.setBounds(558, 341, 121, 22);
		panel.add(comboBox_rAntwort);
		
		lblrAntwort = new JLabel("Richtige Antwort:");
		lblrAntwort.setBounds(558, 320, 121, 14);
		panel.add(lblrAntwort);
		
		lblA = new JLabel("A:");
		lblA.setBounds(558, 80, 31, 14);
		panel.add(lblA);
		
		lblB = new JLabel("B:");
		lblB.setBounds(558, 139, 31, 14);
		panel.add(lblB);
		
		lblC = new JLabel("C:");
		lblC.setBounds(558, 198, 31, 14);
		panel.add(lblC);
		
		lblD = new JLabel("D:");
		lblD.setBounds(558, 257, 31, 14);
		panel.add(lblD);
		
		textArea_Frage = new JTextArea();
		textArea_Frage.setBounds(558, 10, 281, 56);
		textArea_Frage.setText("(Frage)");
		panel.add(textArea_Frage);
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				aktualisieren();
			}
		});
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(27, 75, 500, 334);
		panel.add(scrollPane);
		scrollPane.setViewportView(list);
		
		btnSave = new JButton("Datei speichern");
		btnSave.addActionListener(e -> saveFile());
		btnSave.setBounds(390, 11, 137, 23);
		panel.add(btnSave);
		
		
		btnAddQ = new JButton("Frage hinzufügen");
		btnAddQ.addActionListener(e -> addQ());
		btnAddQ.setBounds(27, 448, 168, 23);
		panel.add(btnAddQ);

		btnAccept = new JButton("Bestätigen");
		btnAccept.setBounds(698, 341, 121, 23);
		btnAccept.addActionListener(e -> modifyQ());
		panel.add(btnAccept);
		
		btnDelete = new JButton("Frage löschen");
		btnDelete.addActionListener(e -> deleteQ());
		btnDelete.setBounds(359, 448, 168, 23);
		panel.add(btnDelete);
		
		ls = this.pfad.listFiles(new FileFilter() {
			public boolean accept(File f) {
					return f.isFile();}});
		
		if (ls != null && ls.length != 0) 
			for(int i = 0; i< ls.length; i++) 
				dateien.add(ls[i]);
				
		comboBox = new JComboBox<File>(dateien);
		comboBox.addActionListener(e -> selectFile());
		comboBox.setBounds(27, 11, 353, 22);
		panel.add(comboBox);
		
		btnNewCategory = new JButton("Neue Kategorie");
		btnNewCategory.addActionListener(e -> newCategory());
		btnNewCategory.setBounds(390, 46, 137, 23);
		panel.add(btnNewCategory);
		
	}
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
		catch(IOException e2) {
			System.out.println("Fehler");
		}
	}
	
	private void addQ(){
		if(modus == 0) {
			btnAddQ.setText("Abbrechen");
			textArea_Frage.setText("(Frage)");
			textArea_A.setText("");
			textArea_B.setText("");
			textArea_C.setText("");
			textArea_D.setText("");
			comboBox_rAntwort.setSelectedItem("A");
			modus = 1;
		}
		else {
			btnAddQ.setText("Frage hinzufuegen");
			aktualisieren();
			modus = 0;
			
		}
	}
	
	private void modifyQ() {
		String[] temp = {textArea_A.getText(), textArea_B.getText(), textArea_C.getText(), textArea_D.getText(), (String) comboBox_rAntwort.getSelectedItem()};
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
	
	private void deleteQ() {
		if(kategorie.containsKey(list.getSelectedValue())) {
			  kategorie.remove(textArea_Frage.getText());
			  model.removeElement(textArea_Frage.getText());
		  }
	}
	
	private void newCategory() {
		String path = JOptionPane.showInputDialog("Gib den Namen der neuen Kategorie ein:");
		String test = pfad.toString()+"\\"+path+".txt";
		if(path != null) {
			datei = new File(pfad.toString()+"\\"+path+".txt");
			try {
				datei.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			comboBox.addItem(datei);
			comboBox.setSelectedItem(datei);
			readFile(datei);
			selectFile();
		}
		
		
	}
	private void aktualisieren() {
		textArea_Frage.setText((String) list.getSelectedValue());
		textArea_A.setText(kategorie.get(list.getSelectedValue())[0]);
		textArea_B.setText(kategorie.get(list.getSelectedValue())[1]);
		textArea_C.setText(kategorie.get(list.getSelectedValue())[2]);
		textArea_D.setText(kategorie.get(list.getSelectedValue())[3]);
		comboBox_rAntwort.setSelectedItem(kategorie.get(list.getSelectedValue())[4]);
	}
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
	
	private void selectFile() {
		actFile = dateien.elementAt(comboBox.getSelectedIndex());
		kategorie.clear();
		boolean i = readFile(actFile);
		    if(i) {
		    	model.clear();
			    for (Map.Entry<String, String[]> a : kategorie.entrySet()) {
			    	model.addElement(a.getKey());
				}
		    }
		    i = false;
	}
}
