package com.welltrack.service.email;

import com.welltrack.exception.ValidacaoException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final String CID_LOGO = "welltrackLogo";
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@localhost}")
    private String mailFrom;

    @Value("${app.mail.logo-classpath:templates/images/welltrack-logo.png}")
    private String logoClasspath;

    public void enviarHtml(String destinatario, String assunto, String corpoHtmlFragmento) {
        if (mailSender == null) {
            log.warn("JavaMailSender não configurado (defina spring.mail.* com o SMTP da AWS SES). E-mail não enviado para {} - assunto: {}", destinatario, assunto);
            return;
        }

        try {
            Resource logo = new ClassPathResource(logoClasspath);
            boolean comLogo = logo.exists() && logo.isReadable();

            if (!comLogo) {
                log.debug("Logo não encontrado em classpath:{} - e-mail será enviado sem imagem.", logoClasspath);
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, comLogo, StandardCharsets.UTF_8.name());
            helper.setFrom(mailFrom);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(montarHtml(corpoHtmlFragmento, comLogo), true);

            if (comLogo) {
                helper.addInline(CID_LOGO, logo);
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new ValidacaoException("Falha ao enviar e-mail.");
        }
    }

    private String montarHtml(String corpoHtmlFragmento, boolean comLogo) {
        String header = comLogo
                ? """
                <div style="text-align:center;margin-bottom:32px">
                  <img src="cid:%s" alt="WellTrack" width="180" style="max-width:100%%;height:auto;border:0"/>
                </div>
                """.formatted(CID_LOGO)
                : "";

        return """
                <html>
                <body style="margin:0;padding:32px 16px;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;color:#1f2937">
                  <div style="max-width:640px;margin:0 auto;background:#ffffff;border:1px solid #e5e7eb;border-radius:24px;padding:32px 28px;box-sizing:border-box">
                    %s
                    <div style="font-size:15px;line-height:1.7;color:#374151">
                      %s
                    </div>
                    <div style="margin-top:32px;padding-top:20px;border-top:1px solid #e5e7eb;font-size:12px;line-height:1.6;color:#6b7280">
                      Este e-mail foi enviado automaticamente pelo WellTrack.
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(header, corpoHtmlFragmento);
    }
}
