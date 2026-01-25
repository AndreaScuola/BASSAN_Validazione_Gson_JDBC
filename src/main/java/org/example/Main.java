package org.example;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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


            //AGGIUNGI PARSING CON GSON

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