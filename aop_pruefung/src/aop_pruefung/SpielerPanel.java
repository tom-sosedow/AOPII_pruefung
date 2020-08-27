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
	private Spieler spieler = new Spieler();
	private Semaphore bereit = new Semaphore(1, true);
	private JPanel panel = new JPanel();
	private JLabel lblA, lblB, lblC, lblD, lblStatus, lblFrage;
	private JRadioButton rdbtnA, rdbtnB, rdbtnC, rdbtnD;
	private JSplitPane splitPaneA, splitPaneB, splitPaneC, splitPaneD;
	private JButton btnAccept;
	private ButtonGroup bg;
	
	public SpielerPanel() {
//		GridBagConstraints gbc_panel = new GridBagConstraints();
//		gbc_panel.fill = GridBagConstraints.BOTH;
//		gbc_panel.gridx = 0;
//		gbc_panel.gridy = 0;
//		gbc_panel.weightx = 0.5;
//		JPanel contentPane = new JPanel();
		
		//contentPane.add(panel, gbc_panel);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void accept(){
		bereit.release();
	}
	
	public void getAuswertung() throws InterruptedException {
		bereit.acquire();
		
	}
	public JPanel getPanel() {
		return panel;
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
