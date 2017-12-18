package opencvdm2.zj.com.camera;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import opencvdm2.zj.com.opencvdemo2.R;

public class MainActivity extends Activity {
	private CameraFragment mCameraFragment=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Window window;
		super.onCreate(savedInstanceState);
		//�õ�����
		window = getWindow();
		//�����ޱ���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//����ȫ��
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//���ú���
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//������Ļ����
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main2c);
		mCameraFragment=new CameraFragment();
		ChangeFragment(mCameraFragment);
		setScreenBrightness(200);
	}
	
	/**
	 * �л�fragment
	 * 
	 * @param fragment ��Ҫ�л�����fragment
	 */
	private void ChangeFragment(Fragment fragment)
	{
		fragment.setArguments(getIntent().getExtras());
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment, fragment);
		transaction.commit();
	}

	private void setScreenBrightness(int paramInt){
		Window localWindow = getWindow();
		WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
		float f = paramInt / 255.0F;
		localLayoutParams.screenBrightness = f;
		localWindow.setAttributes(localLayoutParams);
	}

}
