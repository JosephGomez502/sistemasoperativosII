package com.aeropuerto.airport.infrastructure.mail;

import com.aeropuerto.airport.domain.model.Reservation;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class TicketEmailService {
  private final JavaMailSender mailSender;
  private final String from;

  public TicketEmailService(JavaMailSender mailSender, @Value("${app.mail.from}") String from) {
    this.mailSender = mailSender;
    this.from = from;
  }

  public boolean sendTicket(Reservation reservation, byte[] pdf) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(from);
      helper.setTo(reservation.getUser().getEmail());
      helper.setSubject("Tu ticket TagAirlines " + reservation.getCode());
      helper.setText(html(reservation), true);
      helper.addAttachment("ticket-" + reservation.getCode() + ".pdf", new ByteArrayResource(pdf), "application/pdf");
      mailSender.send(message);
      return true;
    } catch (Exception ignored) {
      return false;
    }
  }

  private String html(Reservation r) {
    return """
        <div style="font-family:Arial,sans-serif;color:#10204a">
          <h2>Tu reserva fue confirmada</h2>
          <p>Hola <strong>%s</strong>, adjuntamos tu ticket PDF listo para imprimir.</p>
          <table style="border-collapse:collapse;width:100%%;max-width:560px">
            <tr><td><strong>Reserva</strong></td><td>%s</td></tr>
            <tr><td><strong>Ruta</strong></td><td>%s - %s</td></tr>
            <tr><td><strong>Salida</strong></td><td>%s</td></tr>
            <tr><td><strong>Asiento</strong></td><td>%s</td></tr>
            <tr><td><strong>Pasajero</strong></td><td>%s</td></tr>
          </table>
          <p>Presenta el PDF o el codigo QR en el aeropuerto.</p>
        </div>
        """.formatted(r.getUser().getFullName(), r.getCode(), r.getFlight().getOrigin().getIataCode(),
        r.getFlight().getDestination().getIataCode(), r.getFlight().getDepartureTime(), r.getSeat().getSeatNumber(),
        r.getUser().getDocumentId());
  }
}
