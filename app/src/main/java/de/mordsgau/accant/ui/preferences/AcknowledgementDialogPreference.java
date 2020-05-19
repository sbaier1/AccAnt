package de.mordsgau.accant.ui.preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;
import de.mordsgau.accant.R;

public class AcknowledgementDialogPreference extends DialogPreference {

    public AcknowledgementDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AcknowledgementDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AcknowledgementDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AcknowledgementDialogPreference(Context context) {
        super(context);
    }



    @Override
    public int getDialogLayoutResource() {
        return R.layout.preference_acknowledgement_dialog;
    }


}
