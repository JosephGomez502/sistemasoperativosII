package com.aeropuerto.airport.infrastructure.pdf;

import com.aeropuerto.airport.domain.model.Reservation;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class TicketPdfService {
  public byte[] render(Reservation r) {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      PdfWriter writer = new PdfWriter(out);
      PdfDocument pdf = new PdfDocument(writer);
      Document doc = new Document(pdf, new PageSize(226, 520));
      doc.setMargins(12, 12, 12, 12);
      doc.add(new Paragraph("AIRPORT PLATFORM").setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER));
      doc.add(new Paragraph("Boarding Ticket").setTextAlignment(TextAlignment.CENTER));
      doc.add(line("Reserva", r.getCode()));
      doc.add(line("Pasajero", r.getUser().getFullName()));
      doc.add(line("Documento", nullSafe(r.getUser().getDocumentId())));
      doc.add(line("Vuelo", r.getFlight().getOrigin().getIataCode() + " -> " + r.getFlight().getDestination().getIataCode()));
      doc.add(line("Salida", r.getFlight().getDepartureTime().toString()));
      doc.add(line("Asiento", r.getSeat().getSeatNumber()));
      doc.add(new Image(ImageDataFactory.create(qr(r.getCode()))).setWidth(120).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER));
      doc.add(new Paragraph("Presente este ticket en el aeropuerto").setFontSize(8).setTextAlignment(TextAlignment.CENTER));
      doc.close();
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("No se pudo generar PDF", e);
    }
  }
  private Paragraph line(String k, String v) { return new Paragraph().setFontSize(9).add(new Text(k + ": ").setBold()).add(v); }
  private String nullSafe(String v) { return v == null || v.isBlank() ? "N/A" : v; }
  private byte[] qr(String text) throws Exception {
    var matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 180, 180);
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      MatrixToImageWriter.writeToStream(matrix, "PNG", out);
      return out.toByteArray();
    }
  }
}
