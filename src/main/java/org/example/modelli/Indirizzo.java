package org.example.modelli;

public class Indirizzo {
    public String via;
    public String citta;
    public String nazione;
    public int cap;

    public String toString(){
        return "Nazione: " + nazione + ", città: " + citta + ", cap: " + cap + ", via " + via;
    }
}
