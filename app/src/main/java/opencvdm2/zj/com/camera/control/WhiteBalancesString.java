package opencvdm2.zj.com.camera.control;

import android.hardware.Camera.Parameters;

public class WhiteBalancesString {
	public static final String mWhiteBalanceTable[][]=
	{
		{Parameters.WHITE_BALANCE_AUTO,"�Զ�"},
		{Parameters.WHITE_BALANCE_INCANDESCENT,"�׳��"},
		{Parameters.WHITE_BALANCE_FLUORESCENT,"�չ��"},
		{Parameters.WHITE_BALANCE_WARM_FLUORESCENT,"ů�չ��"},
		{Parameters.WHITE_BALANCE_DAYLIGHT,"����"},
		{Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT,"����"},
		{Parameters.WHITE_BALANCE_TWILIGHT,"�ƻ�"},
		{Parameters.WHITE_BALANCE_SHADE,"����"},
		{"manual-cct","�ֶ�"},
		{"manual","�ֶ�"}
	};
	
	public static String getTitleByName(String name)
	{
		for(int i=0;i<mWhiteBalanceTable.length;i++)
		{
			if(mWhiteBalanceTable[i][0].compareToIgnoreCase(name)==0)
				return mWhiteBalanceTable[i][1];
		}
		return name;
	}
}
