package org.example;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.example.modelli.Persona;
import org.example.modelli.Persone;
import org.example.modelli.Telefono;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);

        String pathSchema = "src/main/resources/persone.schema.json";
        String pathDati   = "src/main/resources/elencoPersone.json";

        try (InputStream schemaStream = new FileInputStream(pathSchema);
             InputStream dataStream = new FileInputStream(pathDati)) {

            JsonSchema schema = factory.getSchema(schemaStream);
            JsonNode JsonToValidate = mapper.readTree(dataStream);

            Set<ValidationMessage> errors = schema.validate(JsonToValidate);

            if(errors.isEmpty())
                System.out.println("Successo, il file JSON rispetta lo schema");
            else {
                System.out.println("Errore, trovati " + errors.size() + " errori:");
                errors.forEach(err -> System.out.println("   -> " + err.getMessage()));
            }


            Gson gson = new Gson();
            String json = readStringFromFile(pathDati);

            Persone persone = gson.fromJson(json, Persone.class);
            for(Persona p : persone.persone)
                System.out.println(p.toString());


            String url = "jdbc:mysql://127.0.0.1:3306/";
            String database="db_Json_Persone";
            String user="root";
            String password="";

            Connection con;
            con = DriverManager.getConnection(url+database,user,password);
            System.out.println("Connesso al DB");

            //INSERT persona
            String queryPersona = "INSERT INTO persona(nome, cognome, eta) VALUES (?, ?, ?)";
            PreparedStatement stmtPersone = con.prepareStatement(queryPersona, Statement.RETURN_GENERATED_KEYS);    //Chiede l'ID che è stato generato con l'insert

            //INSERT indirizzo
            String queryIndirizzo = "INSERT INTO indirizzo(persona_id, via, citta, cap, nazione) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtIndirizzo = con.prepareStatement(queryIndirizzo);

            //INSERT telefono
            String queryTelefono = "INSERT INTO telefono(persona_id, tipo, numero) VALUES (?, ?, ?)";
            PreparedStatement stmtTelefono = con.prepareStatement(queryTelefono);

            int righeInserite = 0;

            for (Persona p : persone.persone) {
                //---- PERSONA ----
                stmtPersone.setString(1, p.nome);
                stmtPersone.setString(2, p.cognome);
                stmtPersone.setInt(3, p.eta);
                stmtPersone.executeUpdate();

                //recupero ID autogenerato di persona
                ResultSet rs = stmtPersone.getGeneratedKeys();
                rs.next();
                int personaId = rs.getInt(1);

                //---- INDIRIZZO ----
                stmtIndirizzo.setInt(1, personaId);
                stmtIndirizzo.setString(2, p.indirizzo.via);
                stmtIndirizzo.setString(3, p.indirizzo.citta);
                stmtIndirizzo.setInt(4, p.indirizzo.cap);
                stmtIndirizzo.setString(5, p.indirizzo.nazione);
                stmtIndirizzo.executeUpdate();

                //---- TELEFONI ----
                for (Telefono t : p.telefoni) {
                    stmtTelefono.setInt(1, personaId);
                    stmtTelefono.setString(2, t.type);
                    stmtTelefono.setString(3, t.number);
                    stmtTelefono.executeUpdate();
                }

                righeInserite++;
            }

            con.close();
            System.out.println("Inserite correttamente " + righeInserite + " persone");
        } catch (java.io.FileNotFoundException e) {
            System.err.println("ERRORE, file non trovati");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String readStringFromFile(String filePath) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(filePath));
        return new String(content);
    }
}