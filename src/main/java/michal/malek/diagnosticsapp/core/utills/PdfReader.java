package michal.malek.diagnosticsapp.diagnostics_part.utills;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;



public class PdfReader {
    public static String pdfToString(){

        PDDocument document = PDDocument.load(new File(filePath));

        // Inicjalizuj PDFTextStripper, aby zczytać tekst z PDF
        PDFTextStripper pdfStripper = new PDFTextStripper();

        // Zczytaj tekst z PDF i zapisz do zmiennej
        pdfContent = pdfStripper.getText(document);

        // Zamknij dokument
        document.close();
    }
}
