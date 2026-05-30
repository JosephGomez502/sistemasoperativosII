package com.aeropuerto.airport.infrastructure.pdf;

import com.aeropuerto.airport.domain.model.Reservation;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.MultiFormatWriter;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class TicketPdfService {
  public byte[] render(Reservation r) {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      PdfWriter writer = new PdfWriter(out);
      PdfDocument pdf = new PdfDocument(writer);
      Document doc = new Document(pdf, new PageSize(320, 205));
      doc.setMargins(8, 8, 8, 8);

      DeviceRgb navy = new DeviceRgb(18, 31, 76);
      DeviceRgb sky = new DeviceRgb(31, 181, 222);
      Table header = new Table(UnitValue.createPercentArray(new float[] {70, 30})).useAllAvailableWidth();
      header.addCell(cell("TagAirlines", 16, true).setFontColor(ColorConstants.WHITE).setBackgroundColor(navy).setBorder(null));
      header.addCell(cell("BOARDING PASS", 8, true).setFontColor(ColorConstants.WHITE).setBackgroundColor(sky).setTextAlignment(TextAlignment.RIGHT).setBorder(null));
      doc.add(header);

      Table route = new Table(UnitValue.createPercentArray(new float[] {30, 10, 30, 30})).useAllAvailableWidth().setMarginTop(6);
      route.addCell(big(r.getFlight().getOrigin().getIataCode(), r.getFlight().getOrigin().getCity()));
      route.addCell(cell(">", 18, true).setTextAlignment(TextAlignment.CENTER).setBorder(null));
      route.addCell(big(r.getFlight().getDestination().getIataCode(), r.getFlight().getDestination().getCity()));
      route.addCell(cell("SEAT\n" + r.getSeat().getSeatNumber(), 14, true).setTextAlignment(TextAlignment.CENTER).setBorder(null));
      doc.add(route);

      Table detail = new Table(UnitValue.createPercentArray(new float[] {50, 50})).useAllAvailableWidth();
      detail.addCell(lineCell("PASAJERO", r.getUser().getFullName()));
      detail.addCell(lineCell("RESERVA", r.getCode()));
      detail.addCell(lineCell("DOCUMENTO", nullSafe(r.getUser().getDocumentId())));
      detail.addCell(lineCell("SALIDA", r.getFlight().getDepartureTime().toString()));
      doc.add(detail);

      Image barcode = new Image(ImageDataFactory.create(code128(r.getCode()))).setWidth(170).setHeight(32);
      doc.add(barcode.setHorizontalAlignment(HorizontalAlignment.CENTER));
      doc.add(new Paragraph("Ticket compacto para impresion termica. Presenta este codigo en abordaje.")
          .setFontSize(6).setTextAlignment(TextAlignment.CENTER).setMarginTop(0));
      doc.close();
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo generar PDF", e);
    }
  }
  private Cell big(String code, String city) {
    return new Cell().setBorder(null).add(new Paragraph(code).setBold().setFontSize(18).setMargin(0))
        .add(new Paragraph(city).setFontSize(6).setMargin(0));
  }
  private Cell lineCell(String k, String v) {
    return new Cell().setBorder(null).add(new Paragraph(k).setBold().setFontSize(5).setFontColor(ColorConstants.GRAY).setMargin(0))
        .add(new Paragraph(v).setFontSize(7).setMargin(0));
  }
  private Cell cell(String text, int size, boolean bold) {
    Paragraph p = new Paragraph(text).setFontSize(size).setMargin(0);
    if (bold) p.setBold();
    return new Cell().add(p);
  }
  private String nullSafe(String v) { return v == null || v.isBlank() ? "N/A" : v; }
  private byte[] code128(String text) throws Exception {
    var matrix = new MultiFormatWriter().encode(text, BarcodeFormat.CODE_128, 420, 80);
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      MatrixToImageWriter.writeToStream(matrix, "PNG", out);
      return out.toByteArray();
    }
  }
}
