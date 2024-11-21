package murach.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import murach.business.Cart;
import murach.business.LineItem;

import java.util.Properties;

public class MailUtilGmail {

    public static void sendMail(String to, String from, String subject, String body, boolean isBodyHTML) throws MessagingException {
        // Configure SMTP properties for Gmail
        Properties properties = System.getProperties();
        properties.put("mail.transport.protocol", "smtps");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        System.out.println("Creating mail session..."); // Log thông tin
        Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                System.out.println("Authenticating with email: " + from); // Log thông tin
                return new PasswordAuthentication(from, "qtmq yugs dsrt wtuj"); // Thay thế bằng mật khẩu ứng dụng
            }
        });

        session.setDebug(true); // Đặt chế độ debug

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setContent(body, isBodyHTML ? "text/html" : "text/plain");

        System.out.println("Sending email to: " + to); // Log thông tin
        Transport.send(message);
        System.out.println("Email sent successfully!");
    }

    /**
     * Builds order details email content in HTML format.
     */
    public static String buildOrderDetailsEmail(Cart cart, double total) {
        StringBuilder emailContent = new StringBuilder();

        // Start the HTML content
        emailContent.append("<h1>Order Confirmation</h1>");
        emailContent.append("<p>Thank you for your order! Here are the details:</p>");

        // Add table with inline CSS styles for better presentation
        emailContent.append("<table border='1' cellpadding='5' cellspacing='0' style='width: 100%; border-collapse: collapse;'>");
        emailContent.append("<tr style='background-color: #f2f2f2;'><th style='text-align: left; padding: 8px;'>Product</th><th style='text-align: left; padding: 8px;'>Price</th><th style='text-align: left; padding: 8px;'>Quantity</th><th style='text-align: left; padding: 8px;'>Subtotal</th></tr>");

        // Loop through cart items to add rows for each product
        for (LineItem item : cart.getItems()) {
            emailContent.append("<tr>");
            emailContent.append("<td style='padding: 8px;'>").append(item.getProduct().getDescription()).append("</td>");
            emailContent.append("<td style='padding: 8px;'>").append(String.format("$%.2f", item.getProduct().getPrice())).append("</td>");
            emailContent.append("<td style='padding: 8px;'>").append(item.getQuantity()).append("</td>");
            emailContent.append("<td style='padding: 8px;'>").append(String.format("$%.2f", item.getTotal())).append("</td>");
            emailContent.append("</tr>");
        }

        // End the table and add the total amount
        emailContent.append("</table>");
        emailContent.append("<p><strong>Total Amount:</strong> ").append(String.format("$%.2f", total)).append("</p>");
        emailContent.append("<p>We hope to serve you again soon!</p>");

        return emailContent.toString();
    }

}
