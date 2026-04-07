package nhom13.vn.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import nhom13.vn.entity.LeaveRequest;

public class PDFReportGenerator {

    public static void generateLeaveReport(List<LeaveRequest> list, OutputStream out) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            float margin = 50;
            float pageWidth = page.getMediaBox().getWidth();
            float tableWidth = pageWidth - 2 * margin;
            float yStart = page.getMediaBox().getHeight() - margin - 20; // leave space for title
            float y = yStart;

            // Title
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
            cs.newLineAtOffset(margin, page.getMediaBox().getHeight() - margin);
            cs.showText("Leave Report");
            cs.endText();

            y -= 20;

            // Define columns (points)
            float[] colWidths = new float[] {40, 220, 80, 80, 80};
            float tableX = margin;
            float rowHeight = 18;

            // Header background & text
            cs.setStrokingColor(java.awt.Color.BLACK);
            cs.setLineWidth(0.5f);


            // Draw header + rows with full-column vertical separators, paginated
            String[] headers = new String[] {"ID", "User", "Start", "End", "Status"};
            cs.setFont(PDType1Font.HELVETICA_BOLD, 10);

            int totalRows = list == null ? 0 : list.size();
            float usableHeight = y - margin; // space for rows
            int maxRowsPerPage = Math.max(1, (int) Math.floor(usableHeight / rowHeight));

            int rowIndex = 0;
            while (rowIndex < totalRows) {
                int rowsThisPage = Math.min(maxRowsPerPage, totalRows - rowIndex);

                // Coordinates for this page's table block
                float headerTopY = y;
                float headerBottomY = headerTopY - rowHeight;
                float bodyBottomY = headerBottomY - (rowsThisPage * rowHeight);

                // draw outer rectangle (top and bottom)
                cs.moveTo(tableX, headerTopY);
                cs.lineTo(tableX + tableWidth, headerTopY);
                cs.moveTo(tableX, bodyBottomY);
                cs.lineTo(tableX + tableWidth, bodyBottomY);

                // draw vertical separators spanning headerTopY -> bodyBottomY
                float vx = tableX;
                for (int i = 0; i < colWidths.length; i++) {
                    cs.moveTo(vx, headerTopY);
                    cs.lineTo(vx, bodyBottomY);
                    vx += colWidths[i];
                }
                // last vertical
                cs.moveTo(vx, headerTopY);
                cs.lineTo(vx, bodyBottomY);

                // draw horizontal separators (header bottom + each row bottom)
                cs.moveTo(tableX, headerBottomY);
                cs.lineTo(tableX + tableWidth, headerBottomY);
                for (int r = 0; r < rowsThisPage; r++) {
                    float rowY = headerBottomY - (r * rowHeight);
                    cs.moveTo(tableX, rowY);
                    cs.lineTo(tableX + tableWidth, rowY);
                }

                cs.stroke();

                // Draw header text
                float textY = headerTopY - 12;
                float textX = tableX + 2;
                for (int i = 0; i < headers.length; i++) {
                    cs.beginText();
                    cs.newLineAtOffset(textX, textY);
                    cs.showText(headers[i]);
                    cs.endText();
                    textX += colWidths[i];
                }

                // Draw rows for this page
                cs.setFont(PDType1Font.HELVETICA, 10);
                for (int r = 0; r < rowsThisPage; r++) {
                    LeaveRequest lr = list.get(rowIndex + r);
                    float rowTextY = headerBottomY - (r * rowHeight) - 14;
                    float tx = tableX + 2;

                    String id = String.valueOf(lr.getId());
                    String user = lr.getUser() != null && lr.getUser().getFullName() != null ? lr.getUser().getFullName() : "-";
                    String start = lr.getStartDate() != null ? sdf.format(lr.getStartDate()) : "-";
                    String end = lr.getEndDate() != null ? sdf.format(lr.getEndDate()) : "-";
                    String status = lr.getStatus() != null ? lr.getStatus() : "-";

                    // ID
                    cs.beginText(); cs.newLineAtOffset(tx, rowTextY); cs.showText(id); cs.endText();
                    tx += colWidths[0];

                    // User
                    cs.beginText(); cs.newLineAtOffset(tx, rowTextY); cs.showText(truncate(user, (int)colWidths[1] / 6)); cs.endText();
                    tx += colWidths[1];

                    // Start
                    cs.beginText(); cs.newLineAtOffset(tx, rowTextY); cs.showText(start); cs.endText();
                    tx += colWidths[2];

                    // End
                    cs.beginText(); cs.newLineAtOffset(tx, rowTextY); cs.showText(end); cs.endText();
                    tx += colWidths[3];

                    // Status
                    cs.beginText(); cs.newLineAtOffset(tx, rowTextY); cs.showText(status); cs.endText();
                }

                // advance
                rowIndex += rowsThisPage;

                if (rowIndex < totalRows) {
                    // start a new page for remaining rows
                    cs.close();
                    page = new PDPage(PDRectangle.LETTER);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    y = yStart;
                } else {
                    // move y below the table for footer if any
                    y = bodyBottomY - 10;
                }
            }

            cs.close();

            doc.save(out);
        }
    }

    private static String truncate(String s, int maxChars) {
        if (s == null) return "";
        if (s.length() <= maxChars) return s;
        return s.substring(0, Math.max(0, maxChars - 3)) + "...";
    }
}
