package com.ecommerce.joias.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    public void sendTrackingEmail(String to, String name, String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.resend.com/emails";

        // Template HTML Moderno
        String htmlBody = """
            <div style="font-family: sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #eee; padding: 40px; border-radius: 15px; background-color: #ffffff;">
                <div style="text-align: center; margin-bottom: 30px;">
                    <h1 style="color: #1a1a1a; margin: 0;">💎 Pratas Do Sol</h1>
                    <p style="color: #666; font-size: 14px;">Administrativo</p>
                </div>
                <h2 style="color: #2c3e50; border-bottom: 2px solid #f8f9fa; padding-bottom: 10px;">Olá, %s!</h2>
                <p style="font-size: 16px; line-height: 1.6; color: #4a4a4a;">
                    Sua joia acaba de ser postada! Estamos tão ansiosos quanto você para que ela chegue.
                </p>
                <div style="background-color: #f4f7f6; padding: 25px; border-radius: 10px; text-align: center; margin: 30px 0;">
                    <p style="margin: 0; color: #7f8c8d; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">Seu código de rastreamento</p>
                    <h1 style="margin: 10px 0; color: #2980b9; letter-spacing: 3px; font-family: monospace;">%s</h1>
                </div>
                <p style="font-size: 14px; color: #666;">
                    Você pode acompanhar o trajeto diretamente no site dos Correios ou da transportadora parceira.
                </p>
                <div style="margin-top: 40px; padding-top: 20px; border-top: 1px solid #eee; text-align: center; color: #999; font-size: 12px;">
                    <p>© 2026 Pratas Do Sol Joias - Todos os direitos reservados.</p>
                </div>
            </div>
            """.formatted(name, code);

        Map<String, Object> body = new HashMap<>();
        body.put("from", "Pratas Do Sol <onboarding@resend.dev>");
        body.put("to", to);
        body.put("subject", "Boa notícia! Sua joia foi enviada 🚀");
        body.put("html", htmlBody);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + resendApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, entity, String.class);
    }
}