package top.cciradih.bilibili.component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class QrCodeComponent {
    public BufferedImage generateQrCode(String contents) {
        var map = new HashMap<EncodeHintType, Object>();
        map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        map.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, 212, 212, map);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        assert bitMatrix != null;
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
