/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 *
 * @author dgorst
 */
public class EmailWriter {

    private static final String FROM_EMAIL_ADDRESS = "balloon@projectlatex.co.uk";

    private static final String TO_EMAIL_ADDRESS = "projectlatexuk@gmail.com";

    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL_ADDRESS);
        message.setTo(TO_EMAIL_ADDRESS);
        message.setSubject("Test");
        message.setText("Test email");
        mailSender.send(message);
    }

    public static void main(String... args) {
        ApplicationContext context
                = new FileSystemXmlApplicationContext("email.xml");

        EmailWriter emailWriter = (EmailWriter) context.getBean("emailWriter");
        emailWriter.sendMail();
    }
}
