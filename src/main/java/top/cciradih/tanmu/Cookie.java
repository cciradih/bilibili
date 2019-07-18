package top.cciradih.tanmu;

import java.io.*;

public class Cookie {
    private static final String COOKIE = "cookie";

    public String checkFile() {
        String cookie = readFromFile();
        writeToFile(cookie);
        return readFromFile();
    }

    public void writeToFile(String cookie) {
        try (
                FileWriter fileWriter = new FileWriter(COOKIE);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ) {
            bufferedWriter.write(cookie);
            bufferedWriter.flush();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public String readFromFile() {
        try (
                FileReader fileReader = new FileReader(COOKIE);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            StringBuilder stringBuilder = new StringBuilder();
            while (bufferedReader.ready()) {
                stringBuilder.append(bufferedReader.readLine());
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return "";
        }
    }
}
