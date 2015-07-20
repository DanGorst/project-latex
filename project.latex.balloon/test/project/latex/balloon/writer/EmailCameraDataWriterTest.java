/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.latex.balloon.writer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 *
 * @author dgorst
 */
public class EmailCameraDataWriterTest {

    private EmailCameraDataWriter dataWriter;
    
    private MailSender mockMailSender;
    
    private String fromAddress;
    
    private String toAddress;

    @Before
    public void setUp() {
        mockMailSender = mock(MailSender.class);
        
        fromAddress = "balloon@test.com";
        toAddress = "test@test.com";
        
        dataWriter = new EmailCameraDataWriter();
        dataWriter.setMailSender(mockMailSender);
        dataWriter.setFromAddress(fromAddress);
        dataWriter.setToAddress(toAddress);
        dataWriter.setMaxNumberOfEmailAttempts(5);
        dataWriter.setDelayBetweenEmailAttemptsMs(1);
    }
    
    private SimpleMailMessage createMailMessage(String subject) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toAddress);
        mailMessage.setFrom(fromAddress);
        mailMessage.setSubject(toAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(EmailCameraDataWriter.EMAIL_BODY_TEXT);
        return mailMessage;
    }

    /**
     * Test of sendMail method, of class EmailCameraDataWriter.
     */
    @Test
    public void testSendMail() {
        File imageFile = new File("testImage.jpg");
        dataWriter.sendMail(imageFile);
        
        SimpleMailMessage expectedMail = createMailMessage("testImage.jpg");
        
        verify(mockMailSender).send(expectedMail);
    }
    
    @Test
    public void testSendMailRetriesOperationIfSenderThrowsException() {
        File imageFile = new File("testImage.jpg");
        SimpleMailMessage expectedMail = createMailMessage("testImage.jpg");
        
        // The first time we try and send, the mail sender should throw an exception. The second time, it should pass
        doThrow(new MailSendException("Test exception")).doNothing().when(mockMailSender).send(expectedMail);
        
        dataWriter.sendMail(imageFile);
        
        verify(mockMailSender, times(2)).send(expectedMail);
    }
    
    @Test
    public void testSendMailGivesUpAfterExhaustingRetryAttempts() {
        File imageFile = new File("testImage.jpg");
        SimpleMailMessage expectedMail = createMailMessage("testImage.jpg");
        
        // Mail sender should throw each time it is called
        doThrow(new MailSendException("Test exception")).when(mockMailSender).send(expectedMail);
        
        dataWriter.sendMail(imageFile);
        
        verify(mockMailSender, times(5)).send(expectedMail);
    }

    @Test
    public void testWriteImageFiles() throws DataWriteFailedException, InterruptedException {
        List<File> imageFiles = new ArrayList<>();
        imageFiles.add(new File("image1.jpg"));
        imageFiles.add(new File("image2.jpg"));
        
        final CountDownLatch sendMailLatch = new CountDownLatch(2);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                sendMailLatch.countDown();
                return null;
            }
            
        }).when(mockMailSender).send((SimpleMailMessage) any());
        
        dataWriter.writeImageFiles(imageFiles);
        
        sendMailLatch.await(5, TimeUnit.SECONDS);
        
        SimpleMailMessage expectedMail1 = createMailMessage("image1.jpg");
        SimpleMailMessage expectedMail2 = createMailMessage("image2.jpg");
        verify(mockMailSender).send(expectedMail1);
        verify(mockMailSender).send(expectedMail2);
    }
}
