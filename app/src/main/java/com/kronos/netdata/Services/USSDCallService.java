package com.kronos.netdata.Services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by farah on 02/nov/2019.
 */
public class USSDCallService extends AccessibilityService {

    public static String TAG_USSD_CODE_EXECUTE=USSDCallService.class.toString();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG_USSD_CODE_EXECUTE,"onAccessibilityEvent");

        String textMessage=event.getText().toString();

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG_USSD_CODE_EXECUTE,"onServiceConnected");
        AccessibilityServiceInfo accessibilityServiceInfo=new AccessibilityServiceInfo();
        accessibilityServiceInfo.flags=AccessibilityServiceInfo.DEFAULT;
        accessibilityServiceInfo.packageNames=new String[]{"com.android.phone"};
        accessibilityServiceInfo.eventTypes=AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        accessibilityServiceInfo.feedbackType=AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(accessibilityServiceInfo);
    }

    @Override
    public void onInterrupt() {

    }


}
