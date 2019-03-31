package org.myoralvillage.android.ui.widgets;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.util.GlideApp;

public class ContactCard {

    public static void setUser(Context context, @NotNull MaterialCardView contactCard, @NotNull MOVUser user) {
        TextView nameText = contactCard.findViewById(R.id.contact_button_name);
        nameText.setText(user.getName());

        if (user.getImage() != null) {

            ImageView imageView = contactCard.findViewById(R.id.contact_button_image);

            setUserImage(context, user, imageView);
        }
    }

    public static void setUserImage(Context context, MOVUser user, ImageView imageView) {
        setUserImage(context, user, imageView, false);
    }

    public static void setUserImage(Context context, MOVUser user, ImageView imageView, boolean skipMemoryCache) {
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(user.getImage());
            GlideApp.with(context)
                    .load(storageReference)
                    .dontAnimate()
                    .skipMemoryCache(skipMemoryCache)
                    .into(imageView);
        } catch (Exception e) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("/users/profile.jpg");
            GlideApp.with(context)
                    .load(storageReference)
                    .dontAnimate()
                    .skipMemoryCache(skipMemoryCache)
                    .into(imageView);
        }
    }
}
