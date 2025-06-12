package com.example.transaccional_mail_sender.service;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class SendGridEmailService {

    @Value("${spring.sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String sendGridFromEmail;

    public void sendEmail(String toEmail, String subject, String sendgridTemplateId ,Map<String, Object> dynamicData) {
        Email from = new Email(sendGridFromEmail);
        Email to = new Email(toEmail);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.setTemplateId(sendgridTemplateId);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        if (dynamicData != null) {
            for (Map.Entry<String, Object> dynamicEntry : dynamicData.entrySet()) {
                personalization.addDynamicTemplateData(dynamicEntry.getKey(), dynamicEntry.getValue());
            }
        }

        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("Email enviado exitosamente!: " + response.getStatusCode());

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
