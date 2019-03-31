package org.myoralvillage.android.ui.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.contacts.ContactsActivity;
import org.myoralvillage.android.ui.widgets.ContactCard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionSelectContactFragment extends Fragment implements TransactionPage {

    private FirebaseRecyclerAdapter<MOVUser, MOVUserCardViewHolder> adapter;

    private MaterialCardView selectContactButton;
    private MaterialCardView selectedContactView;
    private FloatingActionButton selectedContactDeleteButton;

    private OnTransactionPageInteractionListener interactionListener;

    private static final int REQUEST_SELECT_CONTACT = 0;

    public TransactionSelectContactFragment() {
        // Required blank constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_contact, null);

        selectContactButton = view.findViewById(R.id.transaction_contact_select_button);
        selectedContactView = view.findViewById(R.id.transaction_contact_selected);
        selectedContactDeleteButton = view.findViewById(R.id.transaction_contact_delete_button);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recentContactsRecycler = view.findViewById(R.id.transaction_contact_recycler);
        recentContactsRecycler.setLayoutManager(layoutManager);

        setUpRecyclerView(recentContactsRecycler);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TransactionViewModel transactionViewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        transactionViewModel.getSelectedContact().observe(this, new Observer<MOVUser>() {
            @Override
            public void onChanged(MOVUser movUser) {
                if(movUser == null) {
                    selectContactButton.setVisibility(View.VISIBLE);
                    selectedContactView.setVisibility(View.INVISIBLE);
                    selectedContactDeleteButton.hide();
                } else {
                    selectContactButton.setVisibility(View.GONE);
                    selectedContactView.setVisibility(View.VISIBLE);
                    selectedContactDeleteButton.show();

                    ContactCard.setUser(getContext(), selectedContactView, movUser);
                }

                if(movUser == null) {
                    interactionListener.setCanGoForward(TransactionSelectContactFragment.this, false);
                } else {
                    interactionListener.setCanGoForward(TransactionSelectContactFragment.this, true);
                }
            }
        });

        selectContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ContactsActivity.class);
                intent.putExtra(ContactsActivity.EXTRA_SELECTION_MODE, ContactsActivity.SELECTION_MODE_PICK);

                startActivityForResult(intent, REQUEST_SELECT_CONTACT);
            }
        });

        selectedContactDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactionViewModel.setSelectedContact(null);
            }
        });

        if(transactionViewModel.getSelectedContact().getValue() == null) {
            selectedContactDeleteButton.hide();
        }
    }

    private void setUpRecyclerView(RecyclerView recyclerView) {
        Query query = FirebaseDatabase.getInstance().getReference().child("users");

        FirebaseRecyclerOptions<MOVUser> options =
                new FirebaseRecyclerOptions.Builder<MOVUser>()
                        .setQuery(query, new SnapshotParser<MOVUser>() {
                            @NonNull
                            @Override
                            public MOVUser parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return snapshot.getValue(MOVUser.class);
                            }
                        }).build();

        adapter = new FirebaseRecyclerAdapter<MOVUser, MOVUserCardViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MOVUserCardViewHolder movUserCardViewHolder, int i, @NonNull MOVUser movUser) {
                movUserCardViewHolder.setUser(movUser);
            }

            @NonNull
            @Override
            public MOVUserCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                MaterialCardView view = (MaterialCardView) LayoutInflater.from(getContext()).inflate(R.layout.layout_contact_button, parent, false);
                final MOVUserCardViewHolder holder = new MOVUserCardViewHolder(view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransactionViewModel viewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
                        viewModel.setSelectedContact(holder.getUser());
                    }
                });
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_SELECT_CONTACT) {
            if(resultCode == ContactsActivity.RESULT_DID_PICK && data != null) {
                String uid = data.getStringExtra(ContactsActivity.RESULT_SELECTED_CONTACT);

                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                MOVUser user = dataSnapshot.getValue(MOVUser.class);

                                if(user != null) {
                                    TransactionViewModel viewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
                                    viewModel.setSelectedContact(user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof OnTransactionPageInteractionListener) {
            interactionListener = (OnTransactionPageInteractionListener) context;
        } else {
            throw new IllegalStateException("Fragment must be attached to OnTransactionPageInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(adapter != null) {
            adapter.stopListening();
        }

        interactionListener = null;
    }

    @Override
    public void onPageMadeActive() {
        FragmentActivity activity = getActivity();
        if(activity != null) {
            TransactionViewModel viewModel = ViewModelProviders.of(activity).get(TransactionViewModel.class);

            MOVUser selectedUser = viewModel.getSelectedContact().getValue();

            interactionListener.setCanGoForward(this, selectedUser != null);
        }
    }

    @Override
    public int getForwardButtonText() {
        return R.string.transaction_next;
    }

    @Override
    public int getForwardButtonIcon() {
        return R.drawable.baseline_arrow_forward_black_24;
    }

    @Override
    public void onNextButtonPressed() {

    }

    public static class MOVUserCardViewHolder extends RecyclerView.ViewHolder {

        private MOVUser user;

        MOVUserCardViewHolder(@NonNull MaterialCardView itemView) {
            super(itemView);

        }

        void setUser(MOVUser user) {
            this.user = user;
            ContactCard.setUser(itemView.getContext(), (MaterialCardView) itemView, user);
        }

        MOVUser getUser() {
            return user;
        }
    }
}
