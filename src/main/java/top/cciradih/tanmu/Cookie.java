package top.cciradih.tanmu;

import java.io.*;

class Cookie {
    private Cookie() {
    }

    static Cookie getInstance() {
        return CookieHolder.COOKIE;
    }

    String checkFile() {
        writeToFile(readFromFile());
        return readFromFile();
    }

    private static final String COOKIE = "cookie";

    void writeToFile(String cookie) {
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

    String readFromFile() {
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

    private static class CookieHolder {
        private static final Cookie COOKIE = new Cookie();
    }
}
