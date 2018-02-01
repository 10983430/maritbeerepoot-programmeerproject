package com.example.marit.serietrackerapplication;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * This class inflates a dialogfragment that contains information about what the different colours
 * in the UserDetailsFragment mean
 */

public class ColorInformationDialogFragment extends DialogFragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout and put a listener on the close button
        View rootView = inflater.inflate(R.layout.dialog_fragment, container, false);
        Button closeDialog = rootView.findViewById(R.id.CloseDialog);
        closeDialog.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        // Close dialogfragment when the button is pressed
        Fragment prev = getFragmentManager().findFragmentByTag("Explanation colors");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }
}