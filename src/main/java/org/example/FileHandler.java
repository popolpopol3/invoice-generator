package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {

    public FileHandler(){
    }

    int readFile() throws IOException {
        InputStream inputStream = Files.newInputStream(Paths.get("counter"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int counter = Integer.parseInt(reader.readLine());
        reader.close();
        return counter;
    }

    void writeFile(int counter) throws IOException {
        OutputStream outputStream = Files.newOutputStream(Paths.get("counter"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(String.valueOf(counter + 1));
        writer.close();
    }
}
