package kapandaria.maluach2go;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Orr Dvori on 12/21/2017.
 */

public class CompassActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.compass_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((Compass)findViewById(R.id.compass)).start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        ((Compass)findViewById(R.id.compass)).stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((Compass)findViewById(R.id.compass)).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d(TAG, "stop compass");
        ((Compass)findViewById(R.id.compass)).stop();
    }




}
