package org.cordova.plugin.labs.kiosk;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import org.apache.cordova.*;
import android.widget.*;
import android.view.*;

public class KioskActivity extends CordovaActivity {

    public static volatile boolean running = false;
    public static volatile boolean kioskModeEnabled = false;

    public Context context = null;
    protected void onStart() {
        super.onStart();
        System.out.println("KioskActivity started");
        running = true;
    }

    protected void onStop() {
        super.onStop();
        System.out.println("KioskActivity stopped");
        running = false;
    }

    public void onCreate(Bundle savedInstanceState) {
        System.out.println("KioskActivity paused");
        super.onCreate(savedInstanceState);
        super.init();

        if (running) {
            finish(); // prevent more instances of kiosk activity
        }

        context = getWindow().getContext();

        loadUrl(launchUrl);
        preventStatusBarExpansion(context);
    }
    
    public static void preventStatusBarExpansion(Context context) {
        WindowManager manager = ((WindowManager) context.getApplicationContext()
            .getSystemService(Context.WINDOW_SERVICE));
    
        Activity activity = (Activity)context;
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
    
        // this is to enable the notification to recieve touch events
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
    
        // Draws over status bar
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
    
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = activity.getResources().getDimensionPixelSize(resId);
        }
    
        localLayoutParams.height = result;
    
        localLayoutParams.format = PixelFormat.TRANSPARENT;
    
        customViewGroup view = new customViewGroup(context);
    
        manager.addView(view, localLayoutParams);
    }
    
    public static class customViewGroup extends ViewGroup {
    
        public customViewGroup(Context context) {
            super(context);
        }
    
        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }
    
        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            Log.v("customViewGroup", "**********Intercepted");
            return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (kioskModeEnabled) {
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);

            activityManager.moveTaskToFront(getTaskId(), 0);
        }

    }
}