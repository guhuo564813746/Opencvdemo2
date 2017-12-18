package opencvdm2.zj.com.camera;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.BitmapFactory;
import android.hardware.Camera.Area;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import opencvdm2.zj.com.camera.control.CameraControl;
import opencvdm2.zj.com.camera.control.CameraControlCallback;
import opencvdm2.zj.com.camera.control.ColorEffectString;
import opencvdm2.zj.com.camera.control.SetParametersException;
import opencvdm2.zj.com.camera.control.WhiteBalancesString;
import opencvdm2.zj.com.opencvdemo2.R;
import opencvdm2.zj.com.tools.BitmapWork;
import opencvdm2.zj.com.tools.MyMath;

public class CameraFragment extends Fragment implements OnClickListener, SensorEventListener 
						, PictureCallback ,CameraControlCallback,OnItemSelectedListener
{
	private final static String TAG="CameraFragment";
	private RelativeLayout mFaceLayout=null;
	private CameraControl mCameraControl=null;
	private SurfaceView mCameraPreView=null;
	private ImageButton mButtonTakePicture=null;
	private ImageButton mButtonChangeCamera=null;
	private ImageButton mButtonPicture=null;
	private ImageButton mButtonMore=null;
	private ImageButton mButtonFlashSelect=null;
	private ImageButton mButtonVedioCamera=null;
	private ImageView 	mImageFocus=null;
	private ScrollView	mScrollSetting=null;
	private Spinner 	mSpinnerResolution=null;
	private Spinner 	mSpinnerWhiteBalances=null;
	private Spinner 	mSpinnerColorEffect=null;
	private int 		mOrientation=0;//���� 
	private Sensor 		mSensor=null;
	private Uri 		mImagesUri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	private boolean mIsCamera=true;
	
	@SuppressLint({ "InflateParams", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//ΪͼƬ��ť��Ӱ���Ч��
		OnTouchListener imageButtonTouch=new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					v.setAlpha(0.4f);
				}
				else
				if( event.getAction()==MotionEvent.ACTION_CANCEL ||
					event.getAction()==MotionEvent.ACTION_OUTSIDE ||
					event.getAction()==MotionEvent.ACTION_UP  )
				{
					v.setAlpha(1f);
				}
				return false;
			}
		};
		RelativeLayout cameraView;
		cameraView=(RelativeLayout)inflater.inflate(R.layout.fragment_camera, null);
		mFaceLayout=(RelativeLayout)cameraView.findViewById(R.id.face);
		
		mCameraPreView=(SurfaceView)cameraView.findViewById(R.id.camera_preview);
		mButtonTakePicture=(ImageButton)cameraView.findViewById(R.id.take_picture);
		mButtonTakePicture.setOnTouchListener(imageButtonTouch);
		mButtonTakePicture.setOnClickListener(this);
		
		mButtonChangeCamera=(ImageButton)cameraView.findViewById(R.id.change_camera);
		mButtonChangeCamera.setOnTouchListener(imageButtonTouch);
		mButtonChangeCamera.setOnClickListener(this);
		
		mButtonPicture=(ImageButton)cameraView.findViewById(R.id.picture);
		mButtonPicture.setOnTouchListener(imageButtonTouch);
		mButtonPicture.setOnClickListener(this);
		
		mButtonMore=(ImageButton)cameraView.findViewById(R.id.more);
		mButtonMore.setOnTouchListener(imageButtonTouch);
		mButtonMore.setOnClickListener(this);
		
		mButtonFlashSelect=(ImageButton)cameraView.findViewById(R.id.camera_flash_select);
		mButtonFlashSelect.setOnTouchListener(imageButtonTouch);
		mButtonFlashSelect.setOnClickListener(this);
		
		mButtonVedioCamera=(ImageButton)cameraView.findViewById(R.id.vedio_camera);
		mButtonVedioCamera.setOnTouchListener(imageButtonTouch);
		mButtonVedioCamera.setOnClickListener(this);
		
		mImageFocus=(ImageView)cameraView.findViewById(R.id.focus);
		mScrollSetting=(ScrollView)cameraView.findViewById(R.id.setting);
		mSpinnerResolution=(Spinner)cameraView.findViewById(R.id.resolution);
		mSpinnerWhiteBalances=(Spinner)cameraView.findViewById(R.id.white_balances);
		mSpinnerColorEffect=(Spinner)cameraView.findViewById(R.id.color_effect);
		
		ArrayAdapter<String> arrayAdapter;
		arrayAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item); 
        //Ϊ���������������б�����ʱ�Ĳ˵���ʽ��    
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerResolution.setAdapter(arrayAdapter);
		mSpinnerResolution.setOnItemSelectedListener(this);
		
		arrayAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);    
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerWhiteBalances.setAdapter(arrayAdapter);
		mSpinnerWhiteBalances.setOnItemSelectedListener(this);

		arrayAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);    
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerColorEffect.setAdapter(arrayAdapter);
		mSpinnerColorEffect.setOnItemSelectedListener(this);
		
		mCameraPreView.setOnTouchListener(new OnTouchListener() {
			
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					mImageFocus.setX(event.getX()-mImageFocus.getWidth()/2);
					mImageFocus.setY(event.getY()-mImageFocus.getHeight()/2);
					mImageFocus.setVisibility(View.VISIBLE);
					mImageFocus.invalidate();
					try {
						mCameraControl.focusOn(event.getY()/v.getHeight(),1-event.getX()/v.getWidth());
					} catch (Exception e) {
						// TODO: handle exception
					}
					if(mButtonMore.getRotation()>90)
					{//��������
						mButtonMore.setRotation(0);
						mScrollSetting.setVisibility(View.INVISIBLE);
					}
				}
				return false;
			}
		});
		requestNeedPermissions();
		
		mCameraControl=new CameraControl(this);
		mCameraPreView.getHolder().addCallback(mCameraControl);
		mCameraPreView.getHolder().addCallback(new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
			}
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				updatePicturePreView();
			}
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
			}
		});
		openSensor();
		Log.i(TAG, "oncreateview");
		return cameraView;
	}
	
	/**
	 * �ı䷽��������Ч�� 
	 * @param orientation [0 , 7] ����ֱ�Ϊ 0���泯��  1�����泯�� 2���泯�� 3�����泯�� ��������������
	 */
	private void changeOrientation(int orientation)
	{
		RotateAnimation rotateAnimationTakePicture;
		RotateAnimation rotateAnimationBase;
		RotateAnimation rotateAnimationPicture;
		RotateAnimation rotateAnimationFocus;
		RotateAnimation rotateAnimationSetting;
		float degress=0.0f;
		float predegress=0.0f;
		predegress= MyMath.orientationToDegress(mOrientation);
		predegress=MyMath.doDegress(predegress);
		mOrientation=orientation;
		degress= MyMath.orientationToDegress(mOrientation);
		degress=MyMath.doDegress(degress);
		if(Math.abs(predegress-degress)>180)
		{
			if(predegress>degress)
			{
				degress+=360;
			}
			else
			{
				predegress+=360;
			}
		}
		//Log.i(TAG, "ori"+orientation+"pre:"+predegress+" to:"+degress);
		rotateAnimationTakePicture=new RotateAnimation(predegress,degress,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		rotateAnimationTakePicture.setDuration(240);
		rotateAnimationTakePicture.setFillAfter(true);//ͣ����ִ�����״̬
		rotateAnimationBase=new RotateAnimation(predegress,degress,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		rotateAnimationBase.setDuration(240);
		rotateAnimationBase.setFillAfter(true);//ͣ����ִ�����״̬
		rotateAnimationPicture=new RotateAnimation(predegress,degress,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		rotateAnimationPicture.setDuration(240);
		rotateAnimationPicture.setFillAfter(true);//ͣ����ִ�����״̬
		rotateAnimationFocus=new RotateAnimation(predegress,degress,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		rotateAnimationFocus.setDuration(240);
		rotateAnimationFocus.setFillAfter(true);//ͣ����ִ�����״̬
		rotateAnimationSetting=new RotateAnimation(predegress,degress,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		rotateAnimationSetting.setDuration(240);
		rotateAnimationSetting.setFillAfter(true);//ͣ����ִ�����״̬
		
		mButtonChangeCamera.clearAnimation();
		mButtonChangeCamera.setAnimation(rotateAnimationBase);
		mButtonFlashSelect.clearAnimation();
		mButtonFlashSelect.setAnimation(rotateAnimationBase);
		mButtonMore.clearAnimation();
		mButtonMore.setAnimation(rotateAnimationBase);
		mButtonVedioCamera.clearAnimation();
		mButtonVedioCamera.setAnimation(rotateAnimationBase);
		rotateAnimationBase.startNow();
		
		mButtonTakePicture.clearAnimation();
		mButtonTakePicture.setAnimation(rotateAnimationTakePicture);
		rotateAnimationTakePicture.startNow();
		
		mButtonPicture.clearAnimation();
		mButtonPicture.setAnimation(rotateAnimationPicture);
		rotateAnimationPicture.startNow();
	}
	
	
	private void updatePicturePreView()
	{
		try {
			Cursor cursor=getActivity().getContentResolver().query(mImagesUri, null, null, null, null);
			Log.i(TAG, ""+cursor.getColumnCount());
			//_data : 1
			if(cursor!=null&&cursor.getCount()>0)
			{
				cursor.moveToLast();
				try 
				{
					String picturePath=null;
					picturePath=cursor.getString(1);
					if(picturePath!=null)
					{
						Log.i(TAG, ""+picturePath);
						Bitmap resbmp=BitmapFactory.decodeResource(getResources(), R.drawable.picture);
						Bitmap filebmp=BitmapFactory.decodeFile(picturePath);
						Bitmap bmp= BitmapWork.getMainBitmap(filebmp, resbmp.getWidth(), resbmp.getHeight());
						bmp=BitmapWork.roundBitmap(bmp,bmp.getWidth()*0.16f,bmp.getHeight()*0.16f,(bmp.getWidth()+bmp.getHeight())*0.04f,Color.GRAY);
						mButtonPicture.setImageBitmap(bmp);
						resbmp=null;
						filebmp=null;
						bmp=null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				cursor.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void openPictures()
	{
		try {
			Cursor cursor=getActivity().getContentResolver().query(mImagesUri, null, null, null, null);
			Log.i(TAG, ""+cursor.getColumnCount());
			//_data : 1
			if(cursor.getCount()>0)
			{
				cursor.moveToLast();
				try 
				{
					String picturePath=null;
					picturePath=cursor.getString(1);
					if(picturePath!=null)
					{
				        Intent intent = new Intent(Intent.ACTION_VIEW);
				        intent.setDataAndType(Uri.fromFile( (new File(picturePath))) ,"image/*");
				        getActivity().startActivity(intent);
					}
				}catch (Exception e)
				{
					
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void openSensor()
	{
		//���ڹ���������Ӧ�豸 
		SensorManager sensorMgr; 
		sensorMgr = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
		mSensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);  
		if(mSensor!=null)
			sensorMgr.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_GAME);
	}
	
	private void CloseSensor()
	{
		SensorManager sensorMgr; 
		sensorMgr = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensorMgr.unregisterListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSensorChanged(SensorEvent event) {
		int orientation=0;
		float x;
		float y;
		float z;
		x=event.values[SensorManager.DATA_X];
		y=event.values[SensorManager.DATA_Y];
		z=event.values[SensorManager.DATA_Z];
		//System.out.println(""+z);
		orientation=MyMath.getOrientation(MyMath.getAngle(x, y));
		if(
				(z>=8.0f||z<=-8.0f) ||
				(orientation==mOrientation) ||
				(orientation==1) ||
				(orientation==3) ||
				(orientation==5) ||
				(orientation==7)	)
			return;
		changeOrientation(orientation);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	@Override
	public void onClick(View v) {
		try {
		switch(v.getId())
		{
		case R.id.take_picture:
			if(mIsCamera)
			{
				try 
				{
					mCameraControl.takePicture(this,(int)MyMath.doDegress(90-MyMath.orientationToDegress(mOrientation)));
				} catch (SetParametersException e) {
					e.printStackTrace();
				}
			}
			else
			{
				if(mCameraControl.isTakingVedio())
				{
					mCameraControl.stopTakeVedio();
					mButtonTakePicture.setImageResource(R.drawable.start);
				}
				else
				{
					mCameraControl.getCamera().stopFaceDetection();
					mCameraControl.getCamera().setFaceDetectionListener(null);
					clearFaceView();
					mCameraControl.takeVedio((int)MyMath.doDegress(90-MyMath.orientationToDegress(mOrientation)));
					if(mCameraControl.isTakingVedio())
					{
						mButtonTakePicture.setImageResource(R.drawable.stop);
					}
					else
					{//failed
						
					}
				}
					
			}
			break;
		case R.id.change_camera:
			mCameraControl.changeCamera();
			break;
		case R.id.picture:
			openPictures();
			break;
		case R.id.more:
			if(mButtonMore.getRotation()>90)
			{
				mButtonMore.setRotation(0);
				mScrollSetting.setVisibility(View.INVISIBLE);
			}
			else
			{
				mButtonMore.setRotation(180);
				mScrollSetting.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.camera_flash_select:
			try {
				Parameters parameters=mCameraControl.getCamera().getParameters();
				String flashMode=parameters.getFlashMode();
				if(flashMode.endsWith(Parameters.FLASH_MODE_AUTO))
				{
					mButtonFlashSelect.setImageResource(R.drawable.camera_flash_selected);
					parameters.setFlashMode(Parameters.FLASH_MODE_ON);
				}
				else
				if(flashMode.endsWith(Parameters.FLASH_MODE_ON))
				{
					mButtonFlashSelect.setImageResource(R.drawable.camera_flash_off_selected);
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				}
				else
				{
					mButtonFlashSelect.setImageResource(R.drawable.camera_flash_auto_selected);
					parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
				}
				mCameraControl.getCamera().setParameters(parameters);
			} catch (Exception e) {
			}
			break;
		case R.id.vedio_camera:
			changeToVedioOrCamera();
			break;
		}
		
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void changeToVedioOrCamera()
	{
		if(mIsCamera)
		{//�л���¼��ģʽ
			mButtonVedioCamera.setImageResource(R.drawable.camera_mini);
			mButtonTakePicture.setImageResource(R.drawable.start);
			mIsCamera=!mIsCamera;
		}
		else
		{
			mButtonVedioCamera.setImageResource(R.drawable.video);
			mButtonTakePicture.setImageResource(R.drawable.camera);
			mIsCamera=!mIsCamera;
		}
	}
	
	public void onPictureTaken(byte[] data, Camera camera) {
		try {
			Uri imageUri = getActivity().getContentResolver().insert(mImagesUri, new ContentValues());
			OutputStream os = getActivity().getContentResolver().openOutputStream(imageUri);
			os.write(data);
			os.flush();
			os.close();
		} catch (Exception e) {
			Toast.makeText(getActivity(), "������Ƭʧ�ܣ�����������ֹ����Դ���ռ�ķ��ʡ�", Toast.LENGTH_SHORT).show();
		}
		if(camera!=null)
			camera.startPreview();//��ʼ���Ԥ��
		//�������Ԥ�� 
		updatePicturePreView();
	}

	@Override
	public void onDestroy() {
		CloseSensor();
		super.onDestroy();
	}

	@Override
	public void onFocus(boolean success, List<Area> areas) {
		if(success)
		{
			mImageFocus.setVisibility(View.GONE);
		}
		if(success && areas!=null && areas.size()>0)
		{
			Area area=areas.get(0);
			Log.i(TAG, "onFocus "+area.rect.centerX()+" "+area.rect.centerY());
		}
	}
	
	private void clearFaceView()
	{
		for(int i=0;;i++)
		{
			View view=mFaceLayout.findViewById(i);
			if(view==null)
				break;
			view.setVisibility(View.INVISIBLE);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onCameraChange(Camera camera,int id) {
		ArrayAdapter<String> adapterResolution;
		ArrayAdapter<String> adapterWhiteBalance;
		ArrayAdapter<String> adapterColorEffect;

		mSpinnerResolution.setSelection(0);
		mSpinnerWhiteBalances.setSelection(0);
		mSpinnerColorEffect.setSelection(0);
		
		adapterResolution=(ArrayAdapter<String>)mSpinnerResolution.getAdapter();
		adapterWhiteBalance=(ArrayAdapter<String>)mSpinnerWhiteBalances.getAdapter();
		adapterColorEffect=(ArrayAdapter<String>)mSpinnerColorEffect.getAdapter();
		
		adapterResolution.clear();
		adapterWhiteBalance.clear();
		adapterColorEffect.clear();
		try 
		{
			Parameters parameters=camera.getParameters();
			List<Size> sizes=parameters.getSupportedPictureSizes();
			if(sizes!=null)
			{
				for(int i=0;i<sizes.size();i++)
				{
					adapterResolution.add(""+sizes.get(i).width+"*"+sizes.get(i).height);
				}
				parameters.setPictureSize(sizes.get(0).width, sizes.get(0).height);
			}
			List<String> whiteBalances=parameters.getSupportedWhiteBalance();
			if(whiteBalances!=null)
			{
				for(int i=0;i<whiteBalances.size();i++)
				{
					adapterWhiteBalance.add(WhiteBalancesString.getTitleByName(whiteBalances.get(i)));
				}
			}
			List<String> colorEffect=parameters.getSupportedColorEffects();
			if(colorEffect!=null)
			{
				for(int i=0;i<colorEffect.size();i++)
				{
					adapterColorEffect.add(ColorEffectString.getTitleByName(colorEffect.get(i)));
				}
			}
			camera.setParameters(parameters);
			
		} catch (Exception e) {
		}
		
		camera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
			@Override
			public void onFaceDetection(Face[] faces, Camera camera) {
				clearFaceView();
				if(faces!=null&&faces.length>0)
				{
					for(int i=0;i<faces.length;i++)
					{
						FaceView faceView=null;
						faceView=(FaceView) mFaceLayout.findViewById(i);
						if(faceView==null)
						{
							faceView=new FaceView(getActivity());
							faceView.setId(i);
							mFaceLayout.addView(faceView);
						}
						int w=(int)((faces[i].rect.height())/2000f*mFaceLayout.getWidth());
						int h=(int)((faces[i].rect.width())/2000f*mFaceLayout.getHeight());
						int x= (int) ((1f-(1000f+faces[i].rect.bottom)/2000f)*mFaceLayout.getWidth()) ;
						int y;
						if(mCameraControl.getCamerasId()==0)
							y=(int) (((1000+faces[i].rect.left)/2000f)*mFaceLayout.getHeight()) ;
						else
						{
							y=(int) ((1f-(1000+faces[i].rect.right)/2000f)*mFaceLayout.getHeight()) ;
						}
						
						faceView.setX(x);
						faceView.setY(y);
						LayoutParams params=faceView.getLayoutParams();
						if(w!=params.width || h!=params.height)
						{
							params.width=w;
							params.height=h;
							mFaceLayout.updateViewLayout(faceView, params);
						}
						faceView.setVisibility(View.VISIBLE);
						
						Log.i(TAG, "x"+x+" y"+y+" w"+w+" h"+h);
					}
				}
			}
		});
		camera.startFaceDetection();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch(parent.getId())
		{
		case R.id.resolution:
			try {
				Camera camera=mCameraControl.getCamera();
				Parameters parameters=camera.getParameters();
				List<Size>sizes=parameters.getSupportedPictureSizes();
				parameters.setPictureSize(sizes.get((int)id).width, sizes.get((int)id).height);
				camera.setParameters(parameters);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.white_balances:
			try {
				Camera camera=mCameraControl.getCamera();
				Parameters parameters=camera.getParameters();
				List<String>sizes=parameters.getSupportedWhiteBalance();
				parameters.setWhiteBalance(sizes.get((int)id));
				camera.setParameters(parameters);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.color_effect:
			try {
				Camera camera=mCameraControl.getCamera();
				Parameters parameters=camera.getParameters();
				List<String>sizes=parameters.getSupportedColorEffects();
				parameters.setColorEffect(sizes.get((int)id));
				camera.setParameters(parameters);
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	
	private void requestNeedPermissions()
	{
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
		{
			ArrayList<String> permissions=new ArrayList<String>();
			if(this.getActivity().checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
			{
				permissions.add(Manifest.permission.CAMERA);
			}
			if(this.getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
			{
				permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
			}
			if(this.getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
			{
				permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			}
			if(permissions.size()>0)
			{
				String []strs=new String[permissions.size()];
				permissions.toArray(strs);
				this.requestPermissions(strs, 0);
			}
		}
		else
		{
		}
	}
	
	@SuppressLint("InlinedApi")
	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		for(int i=0;i<permissions.length;i++)
		{
			if(grantResults[i]== PackageManager.PERMISSION_GRANTED)
			{//���� 
				if(permissions[i].equals(Manifest.permission.CAMERA))
				{
					if(mCameraControl.getCamera()==null)
					{
						Log.i(TAG, "reopen ");
						mCameraControl.reopenCamera();
					}
				}
			}
			else
			{
				if(permissions[i].equals(Manifest.permission.CAMERA))
				{
					Toast.makeText(this.getContext(), "���ѽ�ֹ�����������ͷȨ�ޣ������޷����պ�¼���볢����->����->Ȩ�޹��� �������������ͷ.",Toast.LENGTH_LONG).show() ;
				}
				if(permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE))
				{
					Toast.makeText(this.getContext(), "���ѽ�ֹ�����ȡ�ⲿ�洢�������޷�Ԥ��ͼƬ���볢����->����->Ӧ�ù���->��Ӧ��->Ȩ�� �д򿪴洢����.",Toast.LENGTH_LONG).show() ;
				}
				if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
				{
					Toast.makeText(this.getContext(), "���ѽ�ֹ����д���ⲿ�洢�������޷�����Ƭ��¼�񱣴档�볢����->����->Ӧ�ù���->��Ӧ��->Ȩ�� �д򿪴洢����.",Toast.LENGTH_LONG).show() ;
				}
			}
		}
	}
}
