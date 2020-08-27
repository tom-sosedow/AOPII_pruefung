package aop_pruefung;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.EventObject;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;

public class SpielerPanel extends JPanel{
	private Spiel spiel = new Spiel();
	private Semaphore bereit = new Semaphore(1, true);
	private JPanel panel = new JPanel();
	private JLabel lblA, lblB, lblC, lblD, lblStatus, lblFrage;
	private JRadioButton rdbtnA, rdbtnB, rdbtnC, rdbtnD;
	private JSplitPane splitPaneA, splitPaneB, splitPaneC, splitPaneD;
	private JButton btnAccept;
	private ButtonGroup bg;
	
	public SpielerPanel(Spiel spiel) {
		this.spiel = spiel;
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
		btnAccept.addActionListener(e-> accept());
		panel.add(btnAccept);
		
		lblStatus = new JLabel("");
		panel.add(lblStatus);
		
		splitPaneA.setEnabled(false);
		splitPaneB.setEnabled(false);
		splitPaneC.setEnabled(false);
		splitPaneD.setEnabled(false);
		
		bg.add(rdbtnA);
		bg.add(rdbtnB);
		bg.add(rdbtnC);
		bg.add(rdbtnD);
		try {
			bereit.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void accept(){
		bereit.release();
	}
	
	public void getAuswertung() throws InterruptedException {
		bereit.acquire();
		if(spieler.getAuswahl().equals("")) {
			lblStatus.setText("Bitte waehle zuerst eine Antwort!");
			return;
		}
		if(spieler.getAuswahl().equals(kategorie.get(actFrage)[4])) {
			spieler.setPunkte(spieler.getPunkte()+);
			lblScore.setText(spieler.getPunkte() + ":" + spieler.getPunkte());
			lblStatus.setText("<HTML><BODY BGCOLOR=#4EFF0>Richtig!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus.setText("");
		    }, 3, TimeUnit.SECONDS);
		}
		else {
			lblStatus.setText("<HTML><BODY BGCOLOR=#FFCCCC>Leider falsch!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus.setText("");
		    }, 3, TimeUnit.SECONDS);
		}
		if(spieler.getAuswahl().equals(kategorie.get(actFrage)[4])) {
			spieler.setPunkte(spieler.getPunkte()+);
			lblScore.setText(spieler.getPunkte() + ":" + spieler.getPunkte());
			lblStatus.setText("<HTML><BODY BGCOLOR=#4EFF0>Richtig!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus.setText("");
		    }, 3, TimeUnit.SECONDS);
		}
		else {
			lblStatus.setText("<HTML><BODY BGCOLOR=#FFCCCC>Leider falsch!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus.setText("");
		    }, 3, TimeUnit.SECONDS);
		}
		spieler.setAuswahl("");
	}
	public JPanel getPanel() {
		return panel;
	}

	public JLabel getLblA() {
		return lblA;
	}

	public void setLblA(JLabel lblA) {
		this.lblA = lblA;
	}

	public JLabel getLblB() {
		return lblB;
	}

	public void setLblB(JLabel lblB) {
		this.lblB = lblB;
	}

	public JLabel getLblC() {
		return lblC;
	}

	public void setLblC(JLabel lblC) {
		this.lblC = lblC;
	}

	public JLabel getLblD() {
		return lblD;
	}

	public void setLblD(JLabel lblD) {
		this.lblD = lblD;
	}

	public JLabel getLblStatus() {
		return lblStatus;
	}

	public void setLblStatus(JLabel lblStatus) {
		this.lblStatus = lblStatus;
	}

	public JLabel getLblFrage() {
		return lblFrage;
	}

	public void setLblFrage(JLabel lblFrage) {
		this.lblFrage = lblFrage;
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
	/*
	  if(spieler.getAuswahl().equals("")) {
			lblStatus.setText("Bitte waehle zuerst eine Antwort!");
			return;
		}
		if(spieler.getAuswahl().equals(kategorie.get(actFrage)[4])) {
			spieler.setPunkte(spieler.getPunkte()+);
			lblScore.setText(spieler.getPunkte() + ":" + spieler.getPunkte());
			lblStatus.setText("<HTML><BODY BGCOLOR=#4EFF0>Richtig!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus.setText("");
		    }, 3, TimeUnit.SECONDS);
		}
		else {
			lblStatus.setText("<HTML><BODY BGCOLOR=#FFCCCC>Leider falsch!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus.setText("");
		    }, 3, TimeUnit.SECONDS);
		}
		if(spieler.getAuswahl().equals(kategorie.get(actFrage)[4])) {
			spieler.setPunkte(spieler.getPunkte()+);
			lblScore.setText(spieler.getPunkte() + ":" + spieler.getPunkte());
			lblStatus.setText("<HTML><BODY BGCOLOR=#4EFF0>Richtig!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus.setText("");
		    }, 3, TimeUnit.SECONDS);
		}
		else {
			lblStatus.setText("<HTML><BODY BGCOLOR=#FFCCCC>Leider falsch!</BODY></HTML>");
			executor.schedule(() -> {
				lblStatus.setText("");
		    }, 3, TimeUnit.SECONDS);
		}
		spieler.setAuswahl("");
		bereit.release();
	 */
}
