package opencvdm2.zj.com.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.RectF;

public class BitmapWork {
	/**
	 * ȡ��ͼƬ����Ҫ����
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getMainBitmap(Bitmap bitmap,int width ,int height)
	{
		float scaleW=0.0f;
		float scaleH=0.0f;
		Bitmap bmp=Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas=new Canvas(bmp);
		Rect src = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
		Rect dst = new Rect(0,0,canvas.getWidth(),canvas.getHeight());
		scaleW=src.right/dst.right;
		scaleH=src.bottom/dst.bottom;
		if(scaleW>scaleH)
		{
			src.right=(int) (dst.right*scaleH);
			src.left+=(bitmap.getWidth()-src.right)/2;
			src.right+=(bitmap.getWidth()-src.right)/2;
			System.out.println("1:"+src.left+" "+src.right);
		}
		else
		{
			src.bottom=(int)(dst.bottom*scaleW);
			src.top+=(bitmap.getHeight()-src.bottom)/2;
			src.bottom+=(bitmap.getHeight()-src.bottom)/2;
			System.out.println("2:"+src.top+" "+src.bottom);
		}
		Paint paint =new Paint();
		canvas.drawBitmap(bitmap,src,dst,paint);
		canvas.save();
		return bmp;
	}
	
	public static Bitmap resizeBitmap(Bitmap bitmap,int width ,int height)
	{
		Bitmap bmp=Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas=new Canvas(bmp);
		Rect src = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
		Rect dst = new Rect(0,0,canvas.getWidth(),canvas.getHeight());
		Paint paint =new Paint();
		canvas.drawBitmap(bitmap,src,dst,paint);
		canvas.save();
		return bmp;
	}
	
	/**
	 * ����Բ��λͼ 
	 * @return 
	 */
	public static Bitmap roundBitmap(Bitmap bitmap,float rx,float ry,float strokeWidth,int strokeColor)
	{
		Bitmap bmp=Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas=new Canvas(bmp);
		Paint paint =new Paint();
		canvas.drawColor(Color.argb(0, 0, 0, 0));
		//�����ཻģʽ
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		canvas.drawRoundRect(rectF, rx, ry, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, rect, paint);
		paint.setColor(strokeColor);
		//����Ϊ���� 
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(strokeWidth);
		canvas.drawRoundRect(rectF,rx,ry, paint);
		canvas.save();
		return bmp;
	}

}
