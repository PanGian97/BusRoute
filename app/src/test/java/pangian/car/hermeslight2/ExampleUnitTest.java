package pangian.car.hermeslight2;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.InOrderImpl;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {
    private MapViewModel mapViewModel;
    private boolean state;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
     public Observer<Boolean> stateObserver;

    @Before
    public void init(){
        mapViewModel = new MapViewModel();
    }
    @Test
    public void perform_showHideWidgets() {
        // arrange - given
        mapViewModel.widgetVisibility().observeForever(stateObserver);
       InOrder inOrder = Mockito.inOrder(stateObserver);
        // act - when
        mapViewModel.showHideWidgets();
        mapViewModel.showHideWidgets();
        mapViewModel.showHideWidgets();
        inOrder.verify(stateObserver).onChanged(true);
        inOrder.verify(stateObserver).onChanged(false);
        inOrder.verify(stateObserver).onChanged(true);

    }

}