package org.myoralvillage.android.ui.amountselection;

import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;

import java.util.ArrayList;
import java.util.List;

public class AmountSelectedListAdapter extends BaseAdapter {

    private List<MOVCurrencyDenomination> selectedAmounts;
    private AmountSelectionViewModel viewModel;

    public AmountSelectedListAdapter(AmountSelectionViewModel viewModel) {
        selectedAmounts = new ArrayList<>();
        this.viewModel = viewModel;
    }
    @Override
    public int getCount() {
        return selectedAmounts.size();
    }

    @Override
    public MOVCurrencyDenomination getItem(int position) {
        return selectedAmounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final BillImageView billImageView;

        if(!(convertView instanceof BillImageView)) {
            LinearLayout billLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bill, null);
            billImageView = billLayout.findViewById(R.id.bill_image);
        } else {
            billImageView = convertView.findViewById(R.id.bill_image);
        }

        billImageView.setCurrencyDenomination(selectedAmounts.get(position));
        billImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        billImageView);
                BillDragPayload payload = new BillDragPayload(selectedAmounts.get(position), true);
                billImageView.startDrag(data, shadowBuilder, payload, 0);

                viewModel.removeCurrency(position);

                return true;
            }
        });

        return billImageView;
    }

    public void setSelectedDenominations(List<MOVCurrencyDenomination> denominations) {
        selectedAmounts = denominations;
        notifyDataSetChanged();
    }
}
