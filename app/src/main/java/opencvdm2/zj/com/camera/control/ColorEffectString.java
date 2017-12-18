package opencvdm2.zj.com.camera.control;

import android.hardware.Camera.Parameters;

public class ColorEffectString {
	public static final String[][] mColorEffectTable=
	{
		{Parameters.EFFECT_NONE,"��"},
		{Parameters.EFFECT_MONO,"�ڰ�"},
		{Parameters.EFFECT_NEGATIVE,"��Ƭ"},
		{Parameters.EFFECT_SOLARIZE,"�ع����"},
		{Parameters.EFFECT_SEPIA,"���ɫ"},
		{Parameters.EFFECT_POSTERIZE,"ɫ������"},
		{Parameters.EFFECT_WHITEBOARD,"�װ�"},
		{Parameters.EFFECT_BLACKBOARD,"�ڰ�"},
		{Parameters.EFFECT_AQUA,"ǳ��"},
		{"emboss","����"},
		{"sketch","����"},
		{"neon","�޺��"},
	};
	public static String getTitleByName(String name)
	{
		for(int i=0;i<mColorEffectTable.length;i++)
		{
			if(mColorEffectTable[i][0].compareToIgnoreCase(name)==0)
				return mColorEffectTable[i][1];
		}
		return name;
	}
}
