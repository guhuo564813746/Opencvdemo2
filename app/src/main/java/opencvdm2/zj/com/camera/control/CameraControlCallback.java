package opencvdm2.zj.com.camera.control;

import java.util.List;

import android.hardware.Camera;

public interface CameraControlCallback {
	public void onFocus(boolean success, List<Camera.Area> areas);
	public void onCameraChange(Camera camera, int id);
}
