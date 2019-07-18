package top.cciradih.tanmu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QrCodeTest {
    private static final QrCode QR_CODE = new QrCode();

    @Test
    void generateQrCode() {
        QR_CODE.generateQrCode("https://passport.bilibili.com/qrcode/h5/login?oauthKey=7381670f4f89a683fa63cf41a4a308a8");
    }
}