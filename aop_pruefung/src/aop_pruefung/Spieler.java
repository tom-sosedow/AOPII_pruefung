package aop_pruefung;

public class Spieler {
	private int nummer, punkte;
	private String auswahl;
	private Boolean bereit = false;
	public Spieler(int nummer) {
		this.nummer = nummer;
		this.punkte = 0;
	}
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
