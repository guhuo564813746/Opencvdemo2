package opencvdm2.zj.com.tools;

public class MyMath {
	
	public static float orientationToDegress(int orientation)
	{
		float degress;
		degress=orientation*45;
		degress=90-degress;
		return doDegress(degress);
	}

	public static float doDegress(float degress)
	{
		return (degress+360)%360;
	}
	
	public static double doAngle(double angle)
	{
		return  ((angle+Math.PI*2)%(Math.PI*2));
	}
	
	public static int getOrientation(double angle)
	{
		int orientation=0;
		orientation=(int)( ((doAngle(angle)+Math.PI/8)%(Math.PI*2))/(Math.PI/4) );
		//System.out.println("����"+orientation);
		return orientation;
	}
	
	/**
	 * ͨ�������ȡ��x��ĽǶ�
	 * @param x ������
	 * @param y ������
	 * @return [0 , 2pi)
	 */
	public static double getAngle(float x,float y)
	{
		double angle=0;
		if(x>0)//��1,4���� 
		{
			angle=Math.atan(y/x);
		}
		else
		if(x<0)//2,3���� 
		{
			angle=Math.PI+Math.atan(y/x);
		}
		else//x=0
		{
			if(y>=0.0f)
				angle=Math.PI/2;
			else
				angle=Math.PI/2;
		}
		return angle;
		//System.out.println("�Ƕ� "+angle);
	}
}
