package opencvdm2.zj.com.opencvdemo2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import opencvdm2.zj.com.caribration.CameraCalibrationActivity;
import opencvdm2.zj.com.javaopencvdm2.JavaNativeActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btCaribration;
    private Button btOpencvCamera;

    // Used to load the 'native-lib' library on application startup.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
     /*   TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());*/
        btCaribration= (Button) findViewById(R.id.bt_caribration);
        btOpencvCamera= (Button) findViewById(R.id.opencv_camera);
        btOpencvCamera.setOnClickListener(this);
        btCaribration.setOnClickListener(this);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        switch (view.getId()){
            case R.id.bt_caribration:

                intent.setClass(this,CameraCalibrationActivity.class);
                startActivity(intent );
                break;

            case R.id.opencv_camera:
                intent.setClass(this, JavaNativeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
