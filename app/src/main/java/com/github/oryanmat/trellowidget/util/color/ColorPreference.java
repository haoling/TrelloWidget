package com.github.oryanmat.trellowidget.util.color;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.github.oryanmat.trellowidget.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

/**
 * a Preference class for colors (using a color picker) in the PreferenceFragments/Activities
 */
public class ColorPreference extends DialogPreference {
    public static final int DEFAULT_VALUE = Color.BLACK;

    ColorPicker picker;
    int color = DEFAULT_VALUE;
    ColorPreference copyFrom = null;

    public ColorPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setDialogLayoutResource(R.layout.color_chooser);
    }

    public void setCopyFrom(ColorPreference sourceColor)
    {
        copyFrom = sourceColor;
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);
        picker = (ColorPicker) view.findViewById(R.id.color_picker);
        picker.addSVBar((SVBar) view.findViewById(R.id.svbar));
        picker.addOpacityBar((OpacityBar) view.findViewById(R.id.opacitybar));
        Button copyButton = (Button) view.findViewById(R.id.copyButton);
        if (copyFrom != null) {
            copyButton.setVisibility(View.VISIBLE);
            copyButton.setText(String.format(getContext().getString(R.string.pref_title_copy_from_card), copyFrom.getTitle()));
            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    picker.setColor(copyFrom.getColor());
                }
            });
        } else {
            copyButton.setVisibility(View.INVISIBLE);
        }
        picker.setColor(this.getPersistedInt(DEFAULT_VALUE));
        picker.setOldCenterColor(this.getPersistedInt(DEFAULT_VALUE));
        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                color = i;
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(color);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            color = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            color = (Integer) defaultValue;
            persistInt(color);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray array, int index) {
        return array.getInteger(index, DEFAULT_VALUE);
    }

    public int getColor() {
        return color;
    }
}
