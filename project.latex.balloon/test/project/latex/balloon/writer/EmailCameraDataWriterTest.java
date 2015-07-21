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
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

/**
 *
 * @author dgorst
 */
public class EmailCameraDataWriterTest {

    private EmailCameraDataWriter dataWriter;

    private JavaMailSender mockMailSender;

    private String fromAddress;

    private String toAddress;

    private File testImageFile;

    @Before
    public void setUp() {
        mockMailSender = mock(JavaMailSender.class);

        when(mockMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        fromAddress = "balloon@test.com";
        toAddress = "test@test.com";

        testImageFile = new File("testImage.png");

        dataWriter = new EmailCameraDataWriter();
        dataWriter.setMailSender(mockMailSender);
        dataWriter.setFromAddress(fromAddress);
        dataWriter.setToAddress(toAddress);
        dataWriter.setMaxNumberOfEmailAttempts(5);
        dataWriter.setDelayBetweenEmailAttemptsMs(1);
    }

    @Test
    public void testSendMail() throws MessagingException {
        dataWriter.sendMail(testImageFile);
        verify(mockMailSender).send(any(MimeMessage.class));
    }

    @Test
    public void testSendMailRetriesOperationIfSenderThrowsException() throws MessagingException {
        // The first time we try and send, the mail sender should throw an exception. The second time, it should pass
        doThrow(new MailSendException("Test exception")).doNothing().when(mockMailSender).send(any(MimeMessage.class));

        dataWriter.sendMail(testImageFile);

        verify(mockMailSender, times(2)).send(any(MimeMessage.class));
    }

    @Test
    public void testSendMailGivesUpAfterExhaustingRetryAttempts() throws MessagingException {
        // Mail sender should throw each time it is called
        doThrow(new MailSendException("Test exception")).when(mockMailSender).send(any(MimeMessage.class));

        dataWriter.sendMail(testImageFile);

        verify(mockMailSender, times(5)).send(any(MimeMessage.class));
    }

    @Test
    public void testWriteImageFiles() throws DataWriteFailedException, InterruptedException, MessagingException {
        List<File> imageFiles = new ArrayList<>();
        imageFiles.add(testImageFile);

        final CountDownLatch sendMailLatch = new CountDownLatch(1);
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                sendMailLatch.countDown();
                return null;
            }

        }).when(mockMailSender).send(any(MimeMessage.class));

        dataWriter.writeImageFiles(imageFiles);

        sendMailLatch.await(2, TimeUnit.SECONDS);

        verify(mockMailSender).send(any(MimeMessage.class));
    }
}
