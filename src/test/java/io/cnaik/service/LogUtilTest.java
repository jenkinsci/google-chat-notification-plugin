package io.cnaik.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import hudson.model.TaskListener;
import io.cnaik.GoogleChatNotification;

class LogUtilTest {

    private LogUtil log;

    @Mock
    private GoogleChatNotification googleChatNotification;

    private AutoCloseable autoCloseable;
    private ByteArrayOutputStream loggerOutput;
    private PrintStream loggerStream;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        loggerOutput = new ByteArrayOutputStream();
        loggerStream = new PrintStream(loggerOutput);

        var taskListener = mock(TaskListener.class);

        when(taskListener.getLogger()).thenReturn(loggerStream);

        when(googleChatNotification.getTaskListener()).thenReturn(taskListener);

        log = new LogUtil(googleChatNotification);
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
        loggerStream.close();
        loggerOutput.close();
    }

    @Test
    void shouldPrintLogMessage() {
        log.printLog("Test");
        
        assertThat(loggerOutput.toString()).contains("Test");
    }
    
    @Test
    void shouldPrintLogMessageWithPercent() {
        log.printLog("Test %With percent");
        
        assertThat(loggerOutput.toString()).contains("Test %With percent");
    }
    
    @Test
    void shouldPrintLogMessageWithArgs() {
        log.printLog("Test %sithout percent and with args", "W");
        
        assertThat(loggerOutput.toString()).contains("Test Without percent and with args");
    }
}
