package com.cybussolutions.kluchit.Activities;

/**
 * Created by Aaybee on 6/25/2016.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.cybussolutions.kluchit.R;

public class SliderActivity extends Activity {

    public int currentimageindex=0;
    //    Timer timer;
//    TimerTask task;
    ImageView slidingimage;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider);
    }

}