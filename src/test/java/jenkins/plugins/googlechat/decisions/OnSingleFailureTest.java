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
import jenkins.plugins.googlechat.logging.BuildAwareLogger;

public class OnSingleFailureTest {

    @Mock
    private Context context;
    @Mock
    private BuildAwareLogger log;

    private OnSingleFailure condition = new OnSingleFailure(null, log);

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
    public void shouldMeetConditionIfCurrentIsFailureAndPreviousIsUnstable() {
        given(context.previousResultOrSuccess()).willReturn(Result.UNSTABLE);
        given(context.currentResult()).willReturn(Result.FAILURE);

        boolean actual = condition.isMetBy(context);

        assertTrue(actual);
    }

    @Test
    public void shouldNotMeetConditionIfCurrentIsFailureAndPreviousIsFailure() {
        given(context.previousResultOrSuccess()).willReturn(Result.FAILURE);
        given(context.currentResult()).willReturn(Result.FAILURE);

        boolean actual = condition.isMetBy(context);

        assertFalse(actual);
    }

    @Test
    public void shouldNotMeetConditionIfCurrentIsSuccess() {
        given(context.previousResultOrSuccess()).willReturn(Result.FAILURE);
        given(context.currentResult()).willReturn(Result.SUCCESS);

        boolean actual = condition.isMetBy(context);

        assertFalse(actual);
    }
}
