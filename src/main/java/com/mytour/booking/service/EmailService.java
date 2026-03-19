package com.mytour.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.from:MyTour <noreply@mytour.vn>}")
    private String fromAddress;

    // Format tien VND
    private String formatVND(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(amount);
    }

    /**
     * Gui email HTML. @Async de khong block API response.
     * Neu mailSender chua config (chua co SMTP) -> chi log, khong crash.
     */
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (mailSender == null) {
            System.out.println("[EMAIL-SKIP] Mail chua config. To: " + to + " | Subject: " + subject);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("[EMAIL-OK] Sent to: " + to);
        } catch (MessagingException e) {
            System.err.println("[EMAIL-ERR] " + e.getMessage());
        }
    }

    // =========================================================
    // 1. Email cho ADMIN khi co booking moi
    // =========================================================
    public void sendNewBookingToAdmin(String adminEmail, String customerName,
                                       String customerPhone, String tourName,
                                       double totalPrice, String bookingId) {
        String subject = "[MyTour] Booking moi #" + bookingId + " - " + customerName;
        String html = buildEmailTemplate(
                "Booking Moi Can Duyet!",
                "<p>Co mot booking moi tu khach hang:</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:16px 0;'>"
                + row("Ma booking", "#" + bookingId)
                + row("Khach hang", customerName)
                + row("SDT", customerPhone)
                + row("Tour", tourName)
                + row("Tong tien", formatVND(totalPrice))
                + "</table>"
                + button("Duyet ngay", "http://localhost:5173/admin/bookings")
        );
        sendHtmlEmail(adminEmail, subject, html);
    }

    // =========================================================
    // 2. Email cho USER khi booking duoc xac nhan
    // =========================================================
    public void sendBookingConfirmed(String userEmail, String customerName,
                                      String tourName, String startDate,
                                      double totalPrice, String bookingId) {
        String subject = "[MyTour] Booking #" + bookingId + " da duoc xac nhan!";
        String html = buildEmailTemplate(
                "Booking Da Xac Nhan!",
                "<p>Xin chao <strong>" + customerName + "</strong>,</p>"
                + "<p>Booking cua ban da duoc xac nhan thanh cong!</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:16px 0;'>"
                + row("Ma booking", "#" + bookingId)
                + row("Tour", tourName)
                + row("Ngay khoi hanh", startDate)
                + row("Tong tien", formatVND(totalPrice))
                + "</table>"
                + "<p style='color:#059669;font-weight:bold;'>Chuc ban co chuyen di vui ve!</p>"
                + button("Xem booking", "http://localhost:5173/my-bookings")
        );
        sendHtmlEmail(userEmail, subject, html);
    }

    // =========================================================
    // 3. Email cho TAI XE khi duoc gan tour
    // =========================================================
    public void sendDriverAssigned(String driverEmail, String driverName,
                                    String tourName, String startDate,
                                    String customerName, String customerPhone,
                                    int guests) {
        String subject = "[MyTour] Ban duoc gan tour: " + tourName;
        String html = buildEmailTemplate(
                "Ban Duoc Gan Tour Moi!",
                "<p>Xin chao <strong>" + driverName + "</strong>,</p>"
                + "<p>Ban duoc gan mot tour moi. Vui long kiem tra thong tin:</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:16px 0;'>"
                + row("Tour", tourName)
                + row("Ngay khoi hanh", startDate)
                + row("Khach hang", customerName)
                + row("SDT khach", customerPhone)
                + row("So nguoi", String.valueOf(guests))
                + "</table>"
                + "<p>Vui long lien he khach hang de xac nhan diem don.</p>"
        );
        sendHtmlEmail(driverEmail, subject, html);
    }

    // =========================================================
    // 4. Email cho USER khi booking bi huy
    // =========================================================
    public void sendBookingCancelled(String userEmail, String customerName,
                                      String tourName, String bookingId,
                                      String refundInfo) {
        String subject = "[MyTour] Booking #" + bookingId + " da bi huy";
        String html = buildEmailTemplate(
                "Booking Da Bi Huy",
                "<p>Xin chao <strong>" + customerName + "</strong>,</p>"
                + "<p>Booking cua ban da bi huy.</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:16px 0;'>"
                + row("Ma booking", "#" + bookingId)
                + row("Tour", tourName)
                + row("Hoan tien", refundInfo)
                + "</table>"
                + "<p>Neu can ho tro, vui long goi hotline: <strong>0338 739 493</strong></p>"
        );
        sendHtmlEmail(userEmail, subject, html);
    }

    // =========================================================
    // HELPER: Template email chung
    // =========================================================
    private String buildEmailTemplate(String heading, String bodyContent) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='margin:0;padding:0;font-family:Arial,sans-serif;'>"
            + "<div style='max-width:600px;margin:0 auto;'>"
            // Header gradient
            + "<div style='background:linear-gradient(135deg,#ec4899,#f97316);padding:30px;text-align:center;border-radius:12px 12px 0 0;'>"
            + "  <h1 style='color:white;margin:0;font-size:28px;'>MyTour</h1>"
            + "  <p style='color:rgba(255,255,255,0.9);margin:8px 0 0;'>He thong dat tour du lich</p>"
            + "</div>"
            // Body
            + "<div style='padding:30px;background:#ffffff;'>"
            + "  <h2 style='color:#1f2937;margin-top:0;'>" + heading + "</h2>"
            + bodyContent
            + "</div>"
            // Footer
            + "<div style='padding:20px;background:#f9fafb;text-align:center;border-radius:0 0 12px 12px;border-top:1px solid #e5e7eb;'>"
            + "  <p style='color:#9ca3af;font-size:12px;margin:0;'>MyTour - He thong dat tour du lich Viet Nam</p>"
            + "  <p style='color:#9ca3af;font-size:12px;margin:4px 0 0;'>Hotline: 0338 739 493</p>"
            + "</div>"
            + "</div>"
            + "</body></html>";
    }

    private String row(String label, String value) {
        return "<tr>"
            + "<td style='padding:10px 12px;border-bottom:1px solid #f3f4f6;font-weight:bold;color:#374151;width:140px;'>" + label + "</td>"
            + "<td style='padding:10px 12px;border-bottom:1px solid #f3f4f6;color:#1f2937;'>" + value + "</td>"
            + "</tr>";
    }

    private String button(String text, String url) {
        return "<div style='text-align:center;margin:24px 0;'>"
            + "<a href='" + url + "' style='display:inline-block;padding:14px 32px;"
            + "background:linear-gradient(135deg,#ec4899,#f97316);color:white;text-decoration:none;"
            + "border-radius:8px;font-weight:bold;font-size:16px;'>" + text + "</a>"
            + "</div>";
    }
}
