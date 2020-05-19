package de.mordsgau.accant.ui.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.preference.PreferenceDialogFragmentCompat;
import de.mordsgau.accant.R;

public class AcknowledgementDialog extends PreferenceDialogFragmentCompat {

    public static AcknowledgementDialog newInstance(String key) {
        final AcknowledgementDialog acknowledgementDialog = new AcknowledgementDialog();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        acknowledgementDialog.setArguments(bundle);
        return acknowledgementDialog;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        WebView webView = view.findViewById(R.id.webView);
        webView.loadData(getString(R.string.license_info), "text/html", "UTF-8");
    }


}
