package opencvdm2.zj.com.javaopencvdm2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;

import opencvdm2.zj.com.opencvdemo2.R;

/**
 * Created by kevin on 2017/10/30.
 */


    public class JavaNativeActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{

        private static final String TAG = "OCVSample::Activity";
        private CameraBridgeViewBase _cameraBridgeViewBase;
        private boolean              mIsFrontCamera = false;//是否是前置摄像头
        private Mat mRgba;//彩色通道数据集
        private Mat mRgbaT;
        private Mat mRgbaF;
        private CascadeClassifier haarCascade;
        static int REQUEST_CAMERA=0;
        static boolean is_read_external_storage_granted=false;

        private BaseLoaderCallback _baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                      /*  Log.i(TAG, "OpenCV loaded successfully");
                        // Load ndk built module, as specified in moduleName in build.gradle
                        // after opencv initialization

                        _cameraBridgeViewBase.enableView();*/
                        Log.i(TAG, "OpenCV loaded successfully");
                        System.loadLibrary("native-lib");
                        try {
                            InputStream is=getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                            File cascadeDir=getDir("cascade", Context.MODE_PRIVATE);
                            File cascadeFile=new File(cascadeDir,"cascade.xml");
                            FileOutputStream os=new FileOutputStream(cascadeFile);
                            byte[] buffer=new byte[4096];
                            int readByte;
                            while ((readByte=is.read(buffer)) !=-1){
                                os.write(buffer,0,readByte);
                            }
                            is.close();
                            os.close();
                            haarCascade=new CascadeClassifier(cascadeFile.getAbsolutePath());
                            if (haarCascade.empty()){
                                Log.i(TAG,"分类器为空");
                                haarCascade=null;
                            }
                        }catch (Exception e){
                            Log.i(TAG,"分类器为空");
                        }
                        _cameraBridgeViewBase.enableView();

                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                }
            }
        };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setContentView(R.layout.java_native_layout);

            // Permissions for Android 6+
            ActivityCompat.requestPermissions(JavaNativeActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    1);

            _cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.main_surface);
            _cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
            _cameraBridgeViewBase.setCvCameraViewListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            disableCamera();
        }

        @Override
        public void onResume() {
            super.onResume();
            if (!OpenCVLoader.initDebug()) {
                Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, _baseLoaderCallback);
            } else {
                Log.d(TAG, "OpenCV library found inside package. Using it!");
                _baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            switch (requestCode) {
                case 1: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "permission was granted, yay!");
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                    } else {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(JavaNativeActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                // other 'case' lines to check for other
                // permissions this app might request
            }
        }

        public void onDestroy() {
            super.onDestroy();
            disableCamera();
        }

        public void disableCamera() {
            if (_cameraBridgeViewBase != null)
                _cameraBridgeViewBase.disableView();
        }
        private float scale=0;
        public void onCameraViewStarted(int width, int height) {
            Log.d(TAG, "onCameraViewStarted()");

            mRgba=new Mat(height, width, CvType.CV_8UC4);
            this.mRgbaF = new Mat(height, width, CvType.CV_8UC4);//height, width, CvType.CV_8UC4
            this.mRgbaT = new Mat(height, width, CvType.CV_8UC4);
        }

        public void onCameraViewStopped() {
            mRgba.release();
        }

        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
           mRgba=inputFrame.rgba();

            Core.transpose(this.mRgba, this.mRgbaT);
            Imgproc.resize(this.mRgbaT, this.mRgba,mRgba.size() );
            Core.flip(this.mRgba, this.mRgba, 1);
           /* Mat matGray = inputFrame.gray();
            salt(matGray.getNativeObjAddr(), 2000);
            return matGray;*/
//            return fsPictrue(inputFrame);
//            return saltMat(inputFrame);
//            return mhPictrue(inputFrame);
//            return cannyPictrue(inputFrame);
//            return sobelPictrue(mRgba);
//            return laplacianPictrue(mRgba);
            return scharrPitrue(mRgba);
//            Mat matshow=inputFrame.rgba();
//            return matshow;
        }

        private Mat saltMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
            Mat matGray = inputFrame.gray();
            salt(matGray.getNativeObjAddr(), 2000);
            return matGray;
        }

        private Mat faceMat(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
            Mat grayMat=inputFrame.gray();
            mRgba=inputFrame.rgba();
            MatOfRect face=new MatOfRect();
            if (haarCascade !=null){
                haarCascade.detectMultiScale(grayMat,face,1.1,2,2,new Size(200,200),new Size());
            }
            Rect[] faceArray=face.toArray();
            for (int i=0;i<faceArray.length;i++){
                Imgproc.rectangle(mRgba,faceArray[i].tl(),faceArray[i].br(),new Scalar(100),3);
            }
            return mRgba;
        }

        private Mat fsPictrue(Mat matRGB){
            fsMat(matRGB.getNativeObjAddr());
            return matRGB;
        }

        private Mat mhPictrue(Mat matRGB){

            mohuMat(matRGB.getNativeObjAddr());
            return matRGB;
        }

        private Mat cannyPictrue(Mat matRGB){
            canny(matRGB.getNativeObjAddr());
            return matRGB;
        }
        private Mat sobelPictrue(Mat matRGB){
            sobel(matRGB.getNativeObjAddr());
            return matRGB;
        }

        private Mat laplacianPictrue(Mat matRGB){

            laplacian(matRGB.getNativeObjAddr());
            return matRGB;
        }
        private Mat scharrPitrue(Mat matRGB){
            scharr(matRGB.getNativeObjAddr());
            return matRGB;
        }

        public native void salt(long matAddrGray, int nbrElem);
        public native void fsMat(long matArrayGray);
        public native void mohuMat(long matAddrGray);
        public native void canny(long matAddrGray);
        public native void sobel(long matAddrGray);
        public native void laplacian(long matAddrGray);
        public native void scharr(long matAddrGray);



}
