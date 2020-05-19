package de.mordsgau.accant.fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import de.mordsgau.accant.R;
import de.mordsgau.accant.ui.preferences.AcknowledgementDialog;
import de.mordsgau.accant.ui.preferences.AcknowledgementDialogPreference;

public class SettingsFragment extends PreferenceFragmentCompat implements DialogPreference.TargetFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof AcknowledgementDialogPreference) {
            final AcknowledgementDialog dialog = AcknowledgementDialog.newInstance(preference.getKey());
            dialog.setTargetFragment(this, 0);
            final FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                dialog.show(fragmentManager, "test");
            }

        }
    }

}
