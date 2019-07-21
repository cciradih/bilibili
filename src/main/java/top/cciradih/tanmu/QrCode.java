package top.cciradih.tanmu;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

class QrCode {
    private static final int WIDTH = 212;
    private static final int HEIGHT = 212;
    private static final String FORMAT = "PNG";
    private static final Path PATH = Paths.get("qrCode");
    private Map<EncodeHintType, Object> hints = new HashMap<>();

    // version: ( v - 1 ) * 4 + 21
    private QrCode() {
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 0);
    }

    static QrCode getInstance() {
        return QrCodeHolder.QR_CODE;
    }

    void generateQrCode(String contents) {
        try {
            MatrixToImageWriter.writeToPath(new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints), FORMAT, PATH);
        } catch (IOException | WriterException e) {
            System.err.println(e.getMessage());
        }
    }

    private static class QrCodeHolder {
        private static final QrCode QR_CODE = new QrCode();
    }
}
