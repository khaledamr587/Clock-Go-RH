// src/main/java/Services/QRCodeGenerator.java
package Services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

/** Utilitaire pour générer un QR Code en Image JavaFX */
public class QRCodeGenerator {
    public static Image generateQRCodeImage(String text, int width, int height) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
        BufferedImage buffered = MatrixToImageWriter.toBufferedImage(matrix);
        return SwingFXUtils.toFXImage(buffered, null);
    }
}
