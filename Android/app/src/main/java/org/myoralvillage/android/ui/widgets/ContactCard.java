package org.myoralvillage.android.ui.widgets;

import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.annotations.NotNull;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVUser;

public class ContactCard {

    public static void setUser(@NotNull MaterialCardView contactCard, @NotNull MOVUser user) {
        TextView nameText = contactCard.findViewById(R.id.contact_button_name);
        nameText.setText(user.getName());
    }
}
