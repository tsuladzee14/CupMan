package com.example.cupMan;

import java.io.*;

public class DataBaseConfig {

    private static final String FILE_NAME = "dbconfig.txt";

    public static void save(String user, String password) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));

        writer.write(user);
        writer.newLine();
        writer.write(password);

        writer.close();
    }

    public static String[] load() throws IOException {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));

        String user = reader.readLine();
        String password = reader.readLine();

        reader.close();

        return new String[]{user, password};
    }
}