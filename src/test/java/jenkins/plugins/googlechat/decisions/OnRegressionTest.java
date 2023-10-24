package jenkins.plugins.googlechat.decisions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import hudson.model.Result;
import hudson.tasks.junit.TestResultAction;
import jenkins.plugins.googlechat.logging.BuildAwareLogger;

public class OnRegressionTest {

    @Mock
    private Context context;
    @Mock
    private BuildAwareLogger log;
    @Mock
    private TestResultAction previousTestResult;
    @Mock
    private TestResultAction currentTestResult;

    private OnRegression condition = new OnRegression(null, log);

    private AutoCloseable mockito;

    @Before
    public void setup() {
        mockito = MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mockito.close();
    }

    @Test
    public void shouldMeetConditionIfCurrentIsWorseThanPrevious() {
        given(context.currentResultOrSuccess()).willReturn(Result.FAILURE);
        given(context.previousResultOrSuccess()).willReturn(Result.SUCCESS);

        boolean actual = condition.isMetBy(context);

        assertTrue(actual);
    }

    @Test
    public void shouldNotMeetConditionIfCurrentIsBetterThanPrevious() {
        given(context.currentResultOrSuccess()).willReturn(Result.SUCCESS);
        given(context.previousResultOrSuccess()).willReturn(Result.FAILURE);

        boolean actual = condition.isMetBy(context);

        assertFalse(actual);
    }

    @Test
    public void shouldMeetConditionIfCurrentIsHasMoreTestFailuresThanPrevious() {
        given(context.currentResultOrSuccess()).willReturn(Result.UNSTABLE);
        given(context.previousResultOrSuccess()).willReturn(Result.UNSTABLE);
        given(context.getPreviousTestResult()).willReturn(previousTestResult);
        given(context.getCurrentTestResult()).willReturn(currentTestResult);
        given(previousTestResult.getFailCount()).willReturn(1);
        given(currentTestResult.getFailCount()).willReturn(10);

        boolean actual = condition.isMetBy(context);

        assertTrue(actual);
    }

    @Test
    public void shouldNotMeetConditionIfCurrentIsHasFewerTestFailuresThanPrevious() {
        given(context.currentResultOrSuccess()).willReturn(Result.UNSTABLE);
        given(context.previousResultOrSuccess()).willReturn(Result.UNSTABLE);
        given(context.getPreviousTestResult()).willReturn(previousTestResult);
        given(context.getCurrentTestResult()).willReturn(currentTestResult);
        given(previousTestResult.getFailCount()).willReturn(10);
        given(currentTestResult.getFailCount()).willReturn(1);

        boolean actual = condition.isMetBy(context);

        assertFalse(actual);
    }
}
