package me.veryyoung.wechat.luckymoney;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

public class RangeSeekBarPreference extends DialogPreference {

    private CrystalRangeSeekbar rangeSeekbar;

    private TextView textView;

    private String hintText = "拆开红包";

    private String delayStart, delayEnd;

    public RangeSeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_rangeseekbar);


        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String attr = attrs.getAttributeName(i);
            if (attr.equalsIgnoreCase("start")) {
                delayStart = attrs.getAttributeValue(i);
            } else if (attr.equalsIgnoreCase("end")) {
                delayEnd = attrs.getAttributeValue(i);
            }
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        rangeSeekbar = (CrystalRangeSeekbar) view.findViewById(R.id.seekBar);

        SharedPreferences pref = getSharedPreferences();
        int delayStartValue = pref.getInt(delayStart, 0);
        int delayEndValue = pref.getInt(delayEnd, 0);

        this.rangeSeekbar.setMinStartValue(delayStartValue).setMaxStartValue(delayEndValue).apply();

        textView = (TextView) view.findViewById(R.id.pref_seekbar_textview);
        textView.setText("立即" + hintText);

        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                if (0 == maxValue.intValue()) {
                    textView.setText("立即" + hintText);
                } else if (minValue.equals(maxValue)) {
                    textView.setText("延迟" + minValue + "毫秒" + hintText);
                } else {
                    textView.setText("随机延迟" + minValue + "-" + maxValue + "毫秒" + hintText);
                }
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SharedPreferences.Editor editor = getEditor();
            editor.putInt(delayStart, rangeSeekbar.getSelectedMinValue().intValue());
            editor.putInt(delayEnd, rangeSeekbar.getSelectedMaxValue().intValue());
            editor.commit();
        }
        super.onDialogClosed(positiveResult);
    }
}
