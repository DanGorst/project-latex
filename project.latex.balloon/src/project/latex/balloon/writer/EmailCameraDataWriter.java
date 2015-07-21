/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer;

import java.io.File;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 *
 * @author dgorst
 */
public class EmailCameraDataWriter implements CameraDataWriter {

    private static final Logger logger = Logger.getLogger(EmailCameraDataWriter.class);

    static final String EMAIL_BODY_TEXT = "Image attached below";

    private JavaMailSender mailSender;

    private String fromAddress;

    private String toAddress;

    private int maxNumberOfEmailAttempts;

    private int delayBetweenEmailAttemptsMs;

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public void setMaxNumberOfEmailAttempts(int maxNumberOfEmailAttempts) {
        this.maxNumberOfEmailAttempts = maxNumberOfEmailAttempts;
    }

    public void setDelayBetweenEmailAttemptsMs(int delayBetweenEmailAttemptsMs) {
        this.delayBetweenEmailAttemptsMs = delayBetweenEmailAttemptsMs;
    }

    void sendMail(File imageFile) {
        int attemptCount = 0;
        boolean success = false;
        while (!success && attemptCount < maxNumberOfEmailAttempts) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
                messageHelper.setTo(toAddress);
                messageHelper.setFrom(fromAddress);
                messageHelper.setSubject(imageFile.getName());
                messageHelper.setText(EMAIL_BODY_TEXT);
                
                FileSystemResource fileResource = new FileSystemResource(imageFile);
                messageHelper.addAttachment(imageFile.getName(), fileResource);
                
                mailSender.send(message);
                
                success = true;
            } catch (MailException | MessagingException e) {
                logger.error("Email attempt " + attemptCount + " failed: " + e.getMessage());
                try {
                    Thread.sleep(delayBetweenEmailAttemptsMs);
                } catch (InterruptedException ex) {
                    logger.error(ex.getMessage());
                }
            } finally {
                ++attemptCount;
            }
        }
        if (!success) {
            logger.error("Failed to send email containing " + imageFile.getName());
        }

    }

    @Override
    public void writeImageFiles(List<File> imageFiles) throws DataWriteFailedException {
        for (final File file : imageFiles) {
            // Sending the email with a large image attachment could take a long time,
            // so we put it on a background thread
            Runnable sendMailRunnable = new Runnable() {

                @Override
                public void run() {
                    sendMail(file);
                }

            };
            Thread mailThread = new Thread(sendMailRunnable);
            mailThread.start();
        }
    }

    public static void main(String... args) {
        ApplicationContext context
                = new FileSystemXmlApplicationContext("email.xml");

        EmailCameraDataWriter emailWriter = (EmailCameraDataWriter) context.getBean("emailCameraDataWriter");
        emailWriter.sendMail(new File("testImage.png"));
    }
}
