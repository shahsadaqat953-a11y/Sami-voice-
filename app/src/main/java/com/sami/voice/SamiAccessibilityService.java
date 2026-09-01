package com.sami.voice;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class SamiAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Sami screen events یہاں دیکھ سکے گا
    }

    @Override
    public void onInterrupt() {
        // Accessibility service interrupted
    }
}
