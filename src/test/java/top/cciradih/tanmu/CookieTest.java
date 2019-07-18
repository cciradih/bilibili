package top.cciradih.tanmu;

import org.junit.jupiter.api.Test;

class CookieTest {
    private static final Cookie COOKIE = new Cookie();

    @Test
    void writeToFile() {
        COOKIE.writeToFile("asdasdasd");
    }

    @Test
    void checkFile() {
        COOKIE.checkFile();
    }

    @Test
    void readFromFile() {
        String cookie = COOKIE.readFromFile();
        System.out.println(cookie);
    }
}