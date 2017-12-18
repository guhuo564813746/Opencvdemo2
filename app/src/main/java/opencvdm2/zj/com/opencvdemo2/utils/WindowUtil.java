package opencvdm2.zj.com.opencvdemo2.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by kevin on 2017/12/8.
 */

public class WindowUtil {
    public static DisplayMetrics getWidthandHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;// 屏幕高度（像素）
    }
}
