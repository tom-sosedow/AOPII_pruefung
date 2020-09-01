package aop_pruefung;

/**
 * Verwaltet die Informationen ueber die Spieler: Punkte, Auswahl, Nummer und ob sie bereit sind.
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
	
	/**
	 * @return Die Nummer des Spielers
	 */
	public int getNummer() {
		return nummer;
	}
	
	/**
	 * @return Die Auswahl des Spielers
	 */
	public String getAuswahl() {
		return auswahl;
	}
	
	/**
	 * Setzt die Auswahl (A-D) des Spielers.
	 * @param auswahl Die zu setzende Auswahl des zueghoerigen Spielers
	 */
	public void setAuswahl(String auswahl) {
		this.auswahl = auswahl;
	}
	
	/**
	 * @return Die Punkte des Spielers
	 */
	public int getPunkte() {
		return punkte;
	}
	
	/**
	 * Setzt die Punkte des Spielers auf den Wert punkte
	 * @param punkte Die neue Punktzahl des Spielers
	 */
	public void setPunkte(int punkte) {
		this.punkte = punkte;
	}
	
	/**
	 * @return Status, ob der Spieler bereit (seine Antwort abgegeben) hat
	 */
	public Boolean getBereit() {
		return bereit;
	}
	
	/**
	 * Setzt den bereit-Status des Spielers
	 * @param b Status des Spielers
	 */
	public void setBereit(Boolean b) {
		this.bereit = b;
	}
}
