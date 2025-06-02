package report;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dao.BookDAO;
import dao.LoanDAO;
import dao.MemberDAO;
import java.io.FileNotFoundException;
import model.Book;
import model.Loan;
import model.Member;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LoanReportGenerator {
    private LoanDAO loanDAO;
    private MemberDAO memberDAO;
    private BookDAO bookDAO;
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static final Font TABLE_HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font TABLE_CELL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

    public LoanReportGenerator() {
        this.loanDAO = new LoanDAO();
        this.memberDAO = new MemberDAO();
        this.bookDAO = new BookDAO();
    }

    public void generateLoanReport(String outputPath) throws FileNotFoundException {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // Header Laporan
            Paragraph title = new Paragraph("Laporan Peminjaman Perpustakaan", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm");
            String currentDate = dateFormat.format(new Date());
            Paragraph date = new Paragraph("Tanggal: " + currentDate, SUBTITLE_FONT);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            document.add(new Paragraph(" "));

            // Tabel Data Peminjaman
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2, 2, 2, 2, 2});

            // Header Tabel
            String[] headers = {"ID", "Nama Anggota", "Judul Buku", "Tanggal Pinjam", "Tanggal Kembali", "Status"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, TABLE_HEADER_FONT));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Isi Tabel dengan Data Peminjaman
            List<Loan> loans = loanDAO.getAll();
            for (Loan loan : loans) {
                Member member = memberDAO.getById(loan.getMemberId());
                Book book = bookDAO.getById(loan.getBookId());

                table.addCell(new PdfPCell(new Phrase(String.valueOf(loan.getId()), TABLE_CELL_FONT)));
                table.addCell(new PdfPCell(new Phrase(member != null ? member.getName() : "Unknown", TABLE_CELL_FONT)));
                table.addCell(new PdfPCell(new Phrase(book != null ? book.getName() : "Unknown", TABLE_CELL_FONT)));
                table.addCell(new PdfPCell(new Phrase(loan.getLoanDate().toString(), TABLE_CELL_FONT)));
                table.addCell(new PdfPCell(new Phrase(loan.getReturnDate() != null ? loan.getReturnDate().toString() : "-", TABLE_CELL_FONT)));
                table.addCell(new PdfPCell(new Phrase(loan.getReturnDate() != null ? "Dikembalikan" : "Belum Dikembalikan", TABLE_CELL_FONT)));
            }

            document.add(table);
            document.close();
            System.out.println("Laporan peminjaman berhasil dibuat di: " + outputPath);
        } catch (DocumentException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal membuat laporan: " + e.getMessage());
        }
    }
}