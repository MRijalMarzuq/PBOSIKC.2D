package report;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoanReport {
    public static void exportToPDF(DefaultTableModel tableModel) {
        Document document = new Document();
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
            String filename = "LoanReport_" + timestamp + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();

            // Judul
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Loan Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Tanggal pembuatan
            document.add(new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));
            document.add(new Paragraph(" "));

            // Tabel
            PdfPTable pdfTable = new PdfPTable(7); // 7 kolom sesuai tabel dengan email
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{1, 2, 2, 2, 1.5f, 1.5f, 1.5f});

            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            String[] headers = {"ID Pesanan", "Nama Peminjam", "Email Peminjam", "Nama Buku", "Loan Date", "Due Date", "Return Date"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

            // Isi tabel dari data yang ditampilkan (termasuk filter)
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    String value = tableModel.getValueAt(row, col).toString();
                    PdfPCell cell = new PdfPCell(new Phrase(value));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(null, "PDF exported successfully as " + filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error exporting PDF: " + e.getMessage());
        }
    }
}