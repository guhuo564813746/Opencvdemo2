package opencvdm2.zj.com.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class FaceView extends View
{

	public FaceView(Context context) {
		super(context);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint=new Paint();
		paint.setColor(Color.argb(255, 255, 255, 255));
		canvas.drawLine(0, 0, canvas.getWidth()/4, 0, paint);
		canvas.drawLine(0, 0, 0, canvas.getHeight()/4, paint);

		canvas.drawLine(canvas.getWidth()-1, 0, canvas.getWidth()*3/4, 0, paint);
		canvas.drawLine(canvas.getWidth()-1, 0, canvas.getWidth()-1,  canvas.getHeight()/4, paint);

		canvas.drawLine(canvas.getWidth()-1, canvas.getHeight()-1, canvas.getWidth()-1, canvas.getHeight()*3/4, paint);
		canvas.drawLine(canvas.getWidth()-1, canvas.getHeight()-1, canvas.getWidth()*3/4, canvas.getHeight()-1, paint);

		canvas.drawLine(0, canvas.getHeight()-1, canvas.getWidth()/4, canvas.getHeight()-1, paint);
		canvas.drawLine(0, canvas.getHeight()-1, 0, canvas.getHeight()*3/4, paint);
		super.onDraw(canvas);
	}
}
