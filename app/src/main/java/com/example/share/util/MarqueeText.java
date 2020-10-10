package com.example.share.util;

import android.content.Context;
import android.util.AttributeSet;

public class MarqueeText extends androidx.appcompat.widget.AppCompatTextView {
    public MarqueeText(Context context) {
        super(context);
    }
    
    public MarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public MarqueeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @Override
    public boolean isFocused() {
        return true;
    }
}
