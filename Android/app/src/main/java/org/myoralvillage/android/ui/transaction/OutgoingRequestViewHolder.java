package org.myoralvillage.android.ui.transaction;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrency;
import org.myoralvillage.android.data.currency.MOVCurrencyCache;
import org.myoralvillage.android.data.model.MOVRequest;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.util.GlideApp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OutgoingRequestViewHolder extends RecyclerView.ViewHolder implements ValueEventListener, View.OnClickListener {

    private MOVRequest request;
    private DatabaseReference activeReference;

    private final ImageView contactImage;
    private final TextView recipient;
    private final TextView amount;
    private final OnTransactionListener onTransactionListener;

    private final MOVCurrencyCache currencyCache;

    public OutgoingRequestViewHolder(@NonNull View itemView, MOVCurrencyCache currencyCache, OnTransactionListener onTransactionListener) {
        super(itemView);

        itemView.setOnClickListener(this);
        this.onTransactionListener = onTransactionListener;

        this.recipient = itemView.findViewById(R.id.outgoing_request_recipient);
        this.amount = itemView.findViewById(R.id.outgoing_request_amount);
        this.contactImage = itemView.findViewById(R.id.outgoing_request_contact_image);
        contactImage.bringToFront();

        this.currencyCache = currencyCache;
    }

    public void setRequest(final MOVRequest request) {
        this.request = request;

        MOVCurrency currency = currencyCache.getCurrency(itemView.getContext(), request.getCurrency());
        String amountText = currency.getFormattedString(request.getAmount());

        amount.setText(amountText);

        String uid;

        uid = request.getFrom();

        if(activeReference != null) {
            activeReference.removeEventListener(this);
        }
        activeReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        activeReference.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        MOVUser user = dataSnapshot.getValue(MOVUser.class);

        if(user != null &&
                (user.getUid().equals(request.getTo())
                        || user.getUid().equals(request.getFrom()))) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(user.getImage());

            recipient.setText(user.getName());

            GlideApp.with(itemView.getContext())
                    .load(storageReference)
                    .dontAnimate()
                    .into(contactImage);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void onClick(View v) {
        onTransactionListener.onTransactionClick(getAdapterPosition());
    }
}
