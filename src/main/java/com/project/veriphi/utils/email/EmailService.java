package com.project.veriphi.utils.email;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import com.project.veriphi.utils.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    MailjetClient mailjetClient;
    @Value("${spring.mail.username}")
    String senderMail;

    public void confirmBookingMail(String eventName, String venue, String date, String email, String name,
                                   String bookingId) {
        try {
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", senderMail))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", email)))
                                    .put(Emailv31.Message.SUBJECT, AppConstants.MAIL_BOOKING_SUBJECT)
                                    .put(Emailv31.Message.TEXTPART, String.format(AppConstants.MAIL_BOOKING_BODY,name,eventName,venue,date,bookingId))
                            )
                    );
            MailjetResponse response = mailjetClient.post(request);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed to send email via Mailjet: status=" + response.getStatus());
            }
            log.info("Booking email successfully sent to {}", email);
        } catch (Exception e) {
            log.error("Error occurred while sending booking email to {} : {}", email, e.getMessage());
        }
    }

    public void ticketMail(String email, String user, String bookingId) {
        try {
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", senderMail))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", email)))
                                    .put(Emailv31.Message.SUBJECT, AppConstants.MAIL_TICKET_SUBJECT)
                                    .put(Emailv31.Message.TEXTPART, String.format(AppConstants.MAIL_TICKET_BODY, user
                                            , bookingId))
                            )
                    );
            MailjetResponse response = mailjetClient.post(request);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed to send email via Mailjet: status=" + response.getStatus());
            }
            log.info("Ticket email successfully sent to {}", email);
        } catch (Exception e) {
            log.error("Error occurred while sending ticket email to {} : {}", email, e.getMessage());
        }
    }

    public void random(String eventName, String venue, String date, String email, String name,
                                   String bookingId) {
        try {
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", senderMail))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", email)))
                                    .put(Emailv31.Message.SUBJECT, AppConstants.MAIL_BOOKING_SUBJECT)
                                    .put(Emailv31.Message.TEXTPART, String.format(AppConstants.MAIL_BOOKING_BODY,name,eventName,venue,date,bookingId))
                            )
                    );
            MailjetResponse response = mailjetClient.post(request);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed to send email via Mailjet: status=" + response.getStatus());
            }
            log.info("Booking email successfully sent to {}", email);
        } catch (Exception e) {
            log.error("Error occurred while sending booking email to {} : {}", email, e.getMessage());
        }
    }
}
