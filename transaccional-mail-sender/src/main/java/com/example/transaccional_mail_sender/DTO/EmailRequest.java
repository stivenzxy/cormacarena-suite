package com.example.transaccional_mail_sender.DTO;

import lombok.Data;

import java.util.Map;

@Data
public class EmailRequest {
    private String to;
    private String subject;
    private String sendgridTemplateId;
    private Map<String, Object> dynamicData;
}
