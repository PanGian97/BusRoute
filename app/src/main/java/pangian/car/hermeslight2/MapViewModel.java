package pangian.car.hermeslight2;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {

    MutableLiveData<Boolean> widgetVisibility;
    private boolean state = false;

    public MapViewModel()
    {
        widgetVisibility = new MutableLiveData<>();
        widgetVisibility.setValue(false);
    }

    public void showHideWidgets() {
        if (state) {
           state=false;
        }
            else{
               state=true;
            }
        widgetVisibility.setValue(state);
        }

        LiveData<Boolean> widgetVisibility(){
        return widgetVisibility;
        }

}
