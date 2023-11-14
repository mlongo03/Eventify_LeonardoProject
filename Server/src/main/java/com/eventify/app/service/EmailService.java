package com.eventify.app.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSignInEmail(String email, Integer otp) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String dateStr = dateFormat.format(new Date());
        String timeStr = timeFormat.format(new Date());

        messageHelper.setTo(email);
        messageHelper.setSubject("Sign In");
        messageHelper.setText("You have made a new login on " + dateStr + " at " + timeStr + ". If it's not you, you may be at risk of hacking.\nPlease enter the following verification code for 2FA:\n\n" + otp + "\n\n\nHai effettuato un nuovo accesso il " + dateStr + " alle " + timeStr + ", se non sei tu potresti essere sotto esposto a rischi di hackeraggio.\nInserisci il seguente codice per la verifica 2FA\n\n" + otp);
        mailSender.send(mimeMessage);
    }

    public void sendAuthFailure(String email) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String dateStr = dateFormat.format(new Date());
        String timeStr = timeFormat.format(new Date());

        messageHelper.setTo(email);
        messageHelper.setSubject("Authentication Failure");
        messageHelper.setText("You attempted to make a new login on " + dateStr + " at " + timeStr + ". If it's not you, you may be at risk of hacking.\n\n\nHai tentato di effettuare un nuovo accesso il " + dateStr + " alle " + timeStr + ", se non sei tu potresti essere sotto esposto a rischi di hackeraggio.\n");
        mailSender.send(mimeMessage);
    }

    public void sendResetPassword(String email, Integer otp) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        messageHelper.setTo(email);
        messageHelper.setSubject("Password Reset");
        messageHelper.setText("You have requested a password reset. Use the following OTP code to reset your password: \n\n" + otp + "Hai richiesto il ripristino della password. Utilizza il seguente codice OTP per resettare la tua password: \n\n" + otp);
        mailSender.send(mimeMessage);
    }

    public void sendRefresh2fa(String email, Integer otp) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        messageHelper.setTo(email);
        messageHelper.setSubject("Refreh auth 2FA");
        messageHelper.setText("Use the following OTP code to reset your password: \n\n" + otp + "Utilizza il seguente codice OTP per resettare la tua password: \n\n" + otp);
        mailSender.send(mimeMessage);
    }

    public void sendCreationEventConfirm(String email, String title) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String dateStr = dateFormat.format(new Date());
        String timeStr = timeFormat.format(new Date());

        messageHelper.setTo(email);
        messageHelper.setSubject("Event Created");
        messageHelper.setText("You have successfully created the event " + title + " on " + dateStr + " at " + timeStr + "Good luck.\n\n\nHai creato con successo l'evento " + title + " il " + dateStr + " alle " + timeStr + ", buonafortuna.\n");
        mailSender.send(mimeMessage);
    }

    public void sendRegisterEventConfirm(String email, String title) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String dateStr = dateFormat.format(new Date());
        String timeStr = timeFormat.format(new Date());

        messageHelper.setTo(email);
        messageHelper.setSubject("Registered succesfully");
        messageHelper.setText("You have successfully registered for the event " + title + " on " + dateStr + " at " + timeStr + " We will keep you updated with a reminder or for any changes regarding it.\n\n\nHai effettuato con successo la registrazione all evento " + title + " il " + dateStr + " alle " + timeStr + ", ti terremo aggiornato con un promemoria o per qualsiasi cambiamento a riguardo\n");
        mailSender.send(mimeMessage);
    }

    public void sendUnregisterEventConfirm(String email, String title) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String dateStr = dateFormat.format(new Date());
        String timeStr = timeFormat.format(new Date());

        messageHelper.setTo(email);
        messageHelper.setSubject("Unregistered succesfully");
        messageHelper.setText("You have successfully unregistered from the event " + title + " on " + dateStr + " at " + timeStr + " We will keep you updated with a reminder or for any changes regarding it.\n\n\nHai effettuato con successo la tua unregistrazione all evento " + title + " il " + dateStr + " alle " + timeStr + ", ti terremo aggiornato con un promemoria o per qualsiasi cambiamento a riguardo\n");
        mailSender.send(mimeMessage);
    }

    public void sendChangesAdviseAboutEvent(String email, String title) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String dateStr = dateFormat.format(new Date());
        String timeStr = timeFormat.format(new Date());

        messageHelper.setTo(email);
        messageHelper.setSubject("Event updated");
        messageHelper.setText("The event " + title + " that you were registered for on " + dateStr + " at " + timeStr + " has undergone some changes. Please visit the page to check.\n\n\nL'evento  " + title + " a cui eri registrato il " + dateStr + " alle " + timeStr + " ha apportato delle modifiche, vai sulla pagina per controllare\n");
        mailSender.send(mimeMessage);
    }
    public void sendChangesAdviseAboutProfile(String email) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String dateStr = dateFormat.format(new Date());
        String timeStr = timeFormat.format(new Date());

        messageHelper.setTo(email);
        messageHelper.setSubject("Profile updated");
        messageHelper.setText("You have successfully updated your personal data.\nOn " + dateStr + " at " + timeStr + ".\n\n\nHai correttamnte aggiornato i tuoi dati personali.\nIl " + dateStr + " alle " + timeStr + ".\n");
        mailSender.send(mimeMessage);
    }

    public void sendEventAbortedAdvice(String email, String title) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String dateStr = dateFormat.format(new Date());
        String timeStr = timeFormat.format(new Date());

        messageHelper.setTo(email);
        messageHelper.setSubject("Event cancellation");
        messageHelper.setText("You have successfully canceled your event: " + title + " on " + dateStr + " at " + timeStr + " We will keep you updated with a reminder or for any changes regarding it.\n\n\nHai effettuato con successo la cancellazione del tuo evento : " + title + " il " + dateStr + " alle " + timeStr + ", ti terremo aggiornato con un promemoria o per qualsiasi cambiamento a riguardo\n");
        mailSender.send(mimeMessage);
    }

    public void sendEventReminder(String email, String title) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        messageHelper.setTo(email);
        messageHelper.setSubject("Reminder start Event");
        messageHelper.setText("Eventify reminds you that the event you are registered for, " + title + ", will start in 30 minutes.\nDon't miss it!\n\n\nEventify ti ricorda che l'evento a cui sei iscritto " + title + ", iniziera tra 30 minuti.\nNon perdertelo !\n");
        mailSender.send(mimeMessage);
    }

    public void sendConfirmChangeEmail(String email, Integer otp) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

        messageHelper.setTo(email);
        messageHelper.setSubject("Email Change");
        messageHelper.setText("Use the following OTP code to change your email: \n\n" + otp + "\n\n\nUtilizza il seguente codice OTP per cambiare la tua email: \n\n" + otp);
        mailSender.send(mimeMessage);
    }
}
