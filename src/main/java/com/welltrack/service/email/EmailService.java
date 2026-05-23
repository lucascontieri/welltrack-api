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
      log.warn(
          "JavaMailSender não configurado (defina spring.mail.* com o SMTP da AWS SES). E-mail não enviado para {} - assunto: {}",
          destinatario, assunto);
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
            <div style="text-align:center;margin-bottom:36px">
              <img src="cid:%s" alt="WellTrack" width="160" style="max-width:100%%;height:auto;border:0;outline:none;text-decoration:none;display:inline-block"/>
            </div>
            """.formatted(CID_LOGO)
        : "";

    return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="utf-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body style="margin:0;padding:40px 16px;background-color:#f8fafc;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;-webkit-font-smoothing:antialiased;color:#1e293b">
          <div style="max-width:580px;margin:0 auto;background-color:#ffffff;border:1px solid #e2e8f0;border-radius:16px;box-shadow:0 4px 6px -1px rgba(0,0,0,0.05),0 2px 4px -1px rgba(0,0,0,0.03);overflow:hidden">
            <div style="height:6px;background-color:#0f766e"></div>
            <div style="padding:40px 32px 32px 32px;box-sizing:border-box">
              %s
              <div style="font-size:16px;line-height:1.8;color:#334155">
                %s
              </div>
              <div style="margin-top:40px;padding-top:24px;border-top:1px solid #f1f5f9;text-align:center;font-size:12px;line-height:1.6;color:#94a3b8">
                <p style="margin:0 0 8px 0">Este e-mail foi enviado de forma automática pelo WellTrack.</p>
                <p style="margin:0">&copy; 2026 WellTrack. Todos os direitos reservados.</p>
              </div>
            </div>
          </div>
        </body>
        </html>
        """
        .formatted(header, corpoHtmlFragmento);
  }
}
