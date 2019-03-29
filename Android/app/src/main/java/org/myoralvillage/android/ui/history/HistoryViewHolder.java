package org.myoralvillage.android.ui.history;

import android.view.View;
import android.view.ViewTreeObserver;
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
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.data.transaction.MOVTransaction;
import org.myoralvillage.android.ui.util.GlideApp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import static org.myoralvillage.android.ui.history.HistoryAdapter.VIEW_TYPE_FROM;
import static org.myoralvillage.android.ui.history.HistoryAdapter.VIEW_TYPE_TO;

public class HistoryViewHolder extends RecyclerView.ViewHolder implements ValueEventListener, View.OnClickListener {

    private MOVTransaction transaction;
    private DatabaseReference activeReference;

    private CircleImageView contactImage;
    private TextView amountText;
    HistoryAdapter.OnTransactionListener onTransactionListener;

    private MOVCurrencyCache currencyCache;

    public HistoryViewHolder(@NonNull View itemView, MOVCurrencyCache currencyCache, HistoryAdapter.OnTransactionListener onTransactionListener) {
        super(itemView);
        this.currencyCache = currencyCache;

        itemView.setOnClickListener(this);
        this.onTransactionListener = onTransactionListener;

        contactImage = itemView.findViewById(R.id.cell_history_image_contact);
        contactImage.bringToFront();

        amountText = itemView.findViewById(R.id.cell_history_text_amount);
    }

    public void setTransaction(final MOVTransaction transaction) {
        this.transaction = transaction;

        MOVCurrency currency = currencyCache.getCurrency(itemView.getContext(), transaction.getCurrency());
        String text = currency.getFormattedString(transaction.getAmount());
        if(getItemViewType() == VIEW_TYPE_FROM) {
            text = "-"+text;
        }
        amountText.setText(text);

        String uid;

        if(getItemViewType() == VIEW_TYPE_FROM) {
            uid = transaction.getTo();
        } else if(getItemViewType() == VIEW_TYPE_TO) {
            uid = transaction.getFrom();
        } else {
            throw new IllegalStateException("MOVTransactionHistoryViewHolder is wrong type");
        }

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
                (user.getUid().equals(transaction.getTo())
                        || user.getUid().equals(transaction.getFrom()))) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(user.getImage());

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