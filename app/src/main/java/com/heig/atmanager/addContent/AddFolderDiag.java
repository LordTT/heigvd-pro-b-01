package com.heig.atmanager.addContent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.heig.atmanager.MainActivity;
import com.heig.atmanager.userData.PostRequests;
import com.heig.atmanager.R;
import com.heig.atmanager.folders.Folder;

/**
 * Author : Chau Ying Kot
 * Date   : 29.04.2020
 **/
public class AddFolderDiag extends DialogFragment {

    public AddFolderDiag() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_add_folder_diag, null);
        AlertDialog dialog = builder.setView(view)
                // Null, we override this later to change the close behaviour
                .setPositiveButton(getString(R.string.add), null)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddFolderDiag.this.getDialog().cancel();
                    }
                }).create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveBtn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText folderName = view.findViewById(R.id.newFolderName);
                        TextInputLayout folderLayout = view.findViewById(R.id.newFolderName_layout);

                        if (folderName.getText().toString().isEmpty()) {
                            folderLayout.setError(getString(R.string.input_missing));
                        } else {

                            Folder newFolder = new Folder(folderName.getText().toString());
                            PostRequests.postFolder(newFolder, getContext());
                            MainActivity.user.addFolder(newFolder);
                            dismiss();
                        }
                    }
                });
            }
        });

        return dialog;

    }
}