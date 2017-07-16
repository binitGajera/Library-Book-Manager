package com.example.castlesword.librarybooklocator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.TableLayout;
/**
 * Created by binit_gajera on 03-04-2017.
 */
    /**
     * helper for Prompt-Dialog creation
     */
    public abstract class PromptDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener {
        private final EditText input;

        /**
         * @param context
         * @param title resource id
         * @param message resource id
         */
        public PromptDialog(Context context, String title, String message) {
            super(context);
            setTitle(title);
            setMessage(message);

            input = new EditText(context);
            TableLayout.LayoutParams params = new TableLayout.LayoutParams();
            params.setMargins(20, 20, 20, 20);
            input.setLayoutParams(params);
            setView(input);

            setPositiveButton("Add", this);
            setNegativeButton("Cancel", this);
        }
        public void onCancelClicked(DialogInterface dialog) {
            dialog.dismiss();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                if (onOkClicked(input.getText().toString())) {
                    dialog.dismiss();
                }
            } else {
                onCancelClicked(dialog);
            }
        }

        abstract public boolean onOkClicked(String input);
    }