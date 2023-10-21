package jenkins.plugins.googlechat.decisions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import hudson.model.AbstractBuild;
import hudson.model.Result;

public class ContextTest {
    
    @Mock
    private AbstractBuild<?, ?> previous;
    @Mock
    private AbstractBuild<?, ?> current;

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
    public void shouldReturnSuccessIfPreviousBuildNull() {
        Context context = new Context(current, null);
        Result expected = Result.SUCCESS;

        Result actual = context.previousResultOrSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnSuccessIfPreviousResultNull() {
        given(previous.getResult()).willReturn(null);
        Context context = new Context(current, previous);
        Result expected = Result.SUCCESS;

        Result actual = context.previousResultOrSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnPreviousResultIfPresent() {
        Result expected = Result.UNSTABLE;
        given(previous.getResult()).willReturn(expected);
        Context context = new Context(current, previous);

        Result actual = context.previousResultOrSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnCurrentResultIfPresent() {
        Result expected = Result.NOT_BUILT;
        given(current.getResult()).willReturn(expected);
        Context context = new Context(current, previous);

        Result actual = context.currentResult();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnNullIfCurrentBuildNull() {
        Context context = new Context(null, previous);

        Result actual = context.currentResult();

        assertNull(actual);
    }

    @Test
    public void shouldReturnNullIfCurrentResultNull() {
        given(current.getResult()).willReturn(null);
        Context context = new Context(current, previous);

        Result actual = context.currentResult();

        assertNull(actual);
    }
}
