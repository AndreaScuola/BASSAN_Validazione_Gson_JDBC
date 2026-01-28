package org.example.modelli;
import java.util.List;

public class Persona {
    public String cognome;
    public int eta;
    public Indirizzo indirizzo;
    public String nome;
    public List<Telefono> telefoni;

    public String toString(){
        return "Cognome e nome: " + cognome + " " + nome + ", eta: " + eta + "\n\t- Indirizzo: " + indirizzo.toString() + "\n\t- Telefono: " + telefoni.toString();
    }
}
