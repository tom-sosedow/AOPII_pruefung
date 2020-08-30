package aop_pruefung;

import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;

public class SpielerPanel extends JPanel{
	private JPanel panel = new JPanel();
	private JLabel lblA, lblB, lblC, lblD, lblStatus, lblFrage;
	private JRadioButton rdbtnA, rdbtnB, rdbtnC, rdbtnD;
	private JSplitPane splitPaneA, splitPaneB, splitPaneC, splitPaneD;
	private JButton btnAccept;
	private Spieler spieler;
	private ButtonGroup bg;
	private Consumer<Integer> cons;
	
	public SpielerPanel(Consumer<Integer> consumer) {
		this.cons = consumer;
		spieler = new Spieler();
		panel.setLayout(new GridLayout(7, 1, 0, 0));
		
		lblFrage = new JLabel("Frage:");
		panel.add(lblFrage);
		
		splitPaneA = new JSplitPane();
		panel.add(splitPaneA);
		
		lblA = new JLabel("A:");
		splitPaneA.setLeftComponent(lblA);
		
		rdbtnA = new JRadioButton("");
		rdbtnA.addActionListener(e -> spieler.setAuswahl("A"));
		splitPaneA.setRightComponent(rdbtnA);
		
		splitPaneB = new JSplitPane();
		panel.add(splitPaneB);
		
		lblB = new JLabel("B:");
		splitPaneB.setLeftComponent(lblB);
		
		rdbtnB = new JRadioButton("");
		rdbtnB.addActionListener(e -> spieler.setAuswahl("B"));
		splitPaneB.setRightComponent(rdbtnB);
		
		splitPaneC = new JSplitPane();
		panel.add(splitPaneC);
		
		lblC = new JLabel("C:");
		splitPaneC.setLeftComponent(lblC);
		
		rdbtnC = new JRadioButton("");
		rdbtnC.addActionListener(e -> spieler.setAuswahl("C"));
		splitPaneC.setRightComponent(rdbtnC);
		
		splitPaneD = new JSplitPane();
		panel.add(splitPaneD);
		
		lblD = new JLabel("D:");
		splitPaneD.setLeftComponent(lblD);
		
		rdbtnD = new JRadioButton("");
		splitPaneD.setRightComponent(rdbtnD);
		rdbtnD.addActionListener(e -> spieler.setAuswahl("D"));
		
		btnAccept = new JButton("Bestaetigen");
		btnAccept.addActionListener(e-> cons.accept(spieler.getNummer()));
		panel.add(btnAccept);
		
		lblStatus = new JLabel("");
		panel.add(lblStatus);
		
		splitPaneA.setEnabled(false);
		splitPaneB.setEnabled(false);
		splitPaneC.setEnabled(false);
		splitPaneD.setEnabled(false);
		
		bg = new ButtonGroup();
		bg.add(rdbtnA);
		bg.add(rdbtnB);
		bg.add(rdbtnC);
		bg.add(rdbtnD);
	}
	
	public void setLblAntworten(String a, String b, String c, String d) {
		rdbtnA.setText(a);
		rdbtnB.setText(b);
		rdbtnC.setText(c);
		rdbtnD.setText(d);
	}
	
	public void setAntwort(String a) {
		switch(a) {
		case "A":
			rdbtnA.setSelected(true);
			spieler.setAuswahl("A");
			break;
		case "B":
			rdbtnB.setSelected(true);
			spieler.setAuswahl("B");
			break;
		case "C":
			rdbtnC.setSelected(true);
			spieler.setAuswahl("C");
			break;
		case "D":
			rdbtnD.setSelected(true);
			spieler.setAuswahl("D");
			break;
		}
	}
	
	/**
	 * Aktiviert(b=true) bzw. deaktiviert (b=false) die Moeglichkeit des Spielers, eine Auswahl in seiner Buttongroup zu taetigen
	 * @param b gewuenschter Status der Buttons (Boolean)
	 */
	public void changeRdbtnState(Boolean b) {
		rdbtnA.setEnabled(b);
		rdbtnB.setEnabled(b);
		rdbtnC.setEnabled(b);
		rdbtnD.setEnabled(b);
	}
	
	public JPanel getPanel() {
		return panel;
	}

	public JLabel getLblA() {
		return lblA;
	}

	public void setLblA(String a) {
		lblA.setText(a);
	}

	public JLabel getLblB() {
		return lblB;
	}

	public void setLblB(String a) {
		lblB.setText(a);
	}

	public JLabel getLblC() {
		return lblC;
	}

	public void setLblC(String a) {
		lblC.setText(a);
	}

	public JLabel getLblD() {
		return lblD;
	}

	public void setLblD(String a) {
		lblD.setText(a);
	}

	public JLabel getLblStatus() {
		return lblStatus;
	}

	public void setLblStatus(String a) {
		lblStatus.setText(a);
	}

	public JLabel getLblFrage() {
		return lblFrage;
	}

	public void setLblFrage(String a) {
		lblFrage.setText(a);
	}

	public JButton getBtnAccept() {
		return btnAccept;
	}

	public ButtonGroup getBg() {
		return bg;
	}

	public void setBg(ButtonGroup bg) {
		this.bg = bg;
	}
	
	public Spieler getSpieler() {
		return spieler;
	}
}
