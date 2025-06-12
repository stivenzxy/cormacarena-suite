package com.example.transaccional_mail_sender.controller;

import com.example.transaccional_mail_sender.DTO.EmailRequest;
import com.example.transaccional_mail_sender.service.SendGridEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/mail-sender")
@RequiredArgsConstructor
public class EmailController {

    private final SendGridEmailService emailService;

    @PostMapping("/notificar")
    public ResponseEntity<String> enviarEmail(@RequestBody EmailRequest emailRequest) {
        try {
            emailService.sendEmail(
                    emailRequest.getTo(),
                    emailRequest.getSubject(),
                    emailRequest.getSendgridTemplateId(),
                    emailRequest.getDynamicData()
            );

            return ResponseEntity.ok("Email enviado exitosamente al usuario: " + emailRequest.getTo());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar email: " + e.getMessage());
        }
    }
}
