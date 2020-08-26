package aop_pruefung;

/**
 * Verwaltet die Informationen ueber die Spieler: Punkte, Auswahl, Nummer und ob se bereit sind.
 * @author Tom Sosedow
 *
 */
public class Spieler {
	static private int anzahl = 0;
	private int nummer;
	private int punkte;
	private String auswahl = "";
	private Boolean bereit = false;
	
	/**
	 * Legt einen weiteren Spieler mit fortlaufender Nummer (beginnend bei 1) an und setzt Punkte auf 0
	 */
	public Spieler() {
		anzahl++;
		this.nummer = anzahl; 
		this.punkte = 0;
	}
	
	//Getter und Setter
	public int getNummer() {
		return nummer;
	}
	public void setNummer(int nummer) {
		this.nummer = nummer;
	}
	public String getAuswahl() {
		return auswahl;
	}
	public void setAuswahl(String auswahl) {
		this.auswahl = auswahl;
	}
	public int getPunkte() {
		return punkte;
	}
	public void setPunkte(int punkte) {
		this.punkte = punkte;
	}
	public Boolean getBereit() {
		return bereit;
	}
	public void setBereit(Boolean b) {
		this.bereit = b;
	}
}
