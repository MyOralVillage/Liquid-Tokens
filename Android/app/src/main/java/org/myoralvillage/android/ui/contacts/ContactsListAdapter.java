package org.myoralvillage.android.ui.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.model.MOVUser;
import org.myoralvillage.android.ui.widgets.ContactCard;

import java.util.ArrayList;
import java.util.List;

public class ContactsListAdapter extends BaseAdapter implements Filterable {

    private List<MOVUser> contacts;
    private List<MOVUser> contactsToDisplay;
    private LayoutInflater inflater;

    private CharSequence lastConstraint;

    private OnContactSelectedListener contactSelectedListener;

    public ContactsListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        contactsToDisplay = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return contactsToDisplay.size();
    }

    @Override
    public MOVUser getItem(int position) {
        return contactsToDisplay.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null) {
            view =  inflater.inflate(R.layout.cell_contacts_contact, parent, false);
        }

        final MOVUser user = getItem(position);
        TextView nameText = view.findViewById(R.id.cell_contact_text_name);
        ImageView imageView = view.findViewById(R.id.cell_contact_image_contact);

        nameText.setText(user.getName());
        ContactCard.setUserImage(view.getContext(), user, imageView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contactSelectedListener != null) {
                    contactSelectedListener.onContactSelected(user);
                }
            }
        });

        return view;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void setContacts(List<MOVUser> contacts) {
        this.contacts = contacts;
        this.contactsToDisplay = getFilteredResults(lastConstraint);
        notifyDataSetChanged();
    }

    private List<MOVUser> getFilteredResults(CharSequence constraint) {
        List<MOVUser> results;
        if(constraint == null || constraint.length() == 0) {
            results = contacts;
        } else {
            results = new ArrayList<>();
            String constraintString = constraint.toString().toLowerCase().trim();
            for(MOVUser user : contacts) {
                if(user.getName().toLowerCase().contains(constraintString)) {
                    results.add(user);
                }
            }

        }

        return results;
    }


    private final Filter filter = new Filter() {

        @SuppressWarnings("unchecked")
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            lastConstraint = constraint;

            FilterResults filterResults = new FilterResults();
            filterResults.values = getFilteredResults(constraint);
            filterResults.count = ((List<MOVUser>) filterResults.values).size();

            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contactsToDisplay = (List<MOVUser>)results.values;

            notifyDataSetChanged();
        }
    };

    public void setContactSelectedListener(OnContactSelectedListener contactSelectedListener) {
        this.contactSelectedListener = contactSelectedListener;
    }

    public interface OnContactSelectedListener {
        void onContactSelected(MOVUser user);
    }
}
