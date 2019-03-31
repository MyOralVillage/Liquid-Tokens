package org.myoralvillage.android.ui.widgets;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import org.myoralvillage.android.R;

import java.io.File;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

public class PhotoSelectionDialog {

    public static final int REQUEST_SELECT_PHOTO = 0;
    public static final int REQUEST_TAKE_PHOTO = 1;


    public static AlertDialog presentPictureOptionsDialog(final FragmentActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        View bodyView = LayoutInflater.from(activity).inflate(R.layout.layout_contact_photo_select, null);
        MaterialButton selectPhotoButton = bodyView.findViewById(R.id.contact_photo_select_button_select);
        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectPhoto = new Intent(Intent.ACTION_GET_CONTENT);
                selectPhoto.setType("image/*");

                activity.startActivityForResult(Intent.createChooser(selectPhoto, "Select Photo"), REQUEST_SELECT_PHOTO);
            }
        });
        MaterialButton takePhotoButton = bodyView.findViewById(R.id.contact_photo_select_button_take);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File tempFile = new File(activity.getFilesDir(), "images/image.jpg");
                if (!tempFile.exists() && !tempFile.mkdir()) return;

                Uri uri = FileProvider.getUriForFile(
                        activity,
                        "org.myoralvillage.android.provider",
                        tempFile);

                Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                activity.startActivityForResult(takePhoto, REQUEST_TAKE_PHOTO);
            }
        });

        if(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(activity.getPackageManager()) == null) {
            takePhotoButton.setVisibility(View.GONE);
        }


        builder.setView(bodyView);
        builder.setTitle(activity.getResources().getString(R.string.login_completion_dialog_add_photo));

        return builder.show();
    }
}
