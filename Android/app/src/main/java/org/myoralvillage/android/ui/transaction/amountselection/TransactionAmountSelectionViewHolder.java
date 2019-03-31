package org.myoralvillage.android.ui.transaction.amountselection;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;

import org.myoralvillage.android.R;
import org.myoralvillage.android.data.currency.MOVCurrencyDenomination;
import org.myoralvillage.android.data.currency.MOVCurrencyDenominationType;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionAmountSelectionViewHolder extends RecyclerView.ViewHolder{

    private static final int TALLY_HEIGHT_DP = 30;
    private static final int TALLY_PADDING_DP = 8;

    private static final int MAX_STACK = 5;

    private DenominationImageView[] imageViews;
    private MaterialCardView[] cardViews;
    private LinearLayout tallyLayout;
    private View containerView;

    public TransactionAmountSelectionViewHolder(@NonNull View itemView) {
        super(itemView);

        tallyLayout = itemView.findViewById(R.id.cell_denomination_tally_layout);
        containerView = itemView.findViewById(R.id.cell_denomination_tally_container);

        imageViews = new DenominationImageView[MAX_STACK];
        imageViews[0] = itemView.findViewById(R.id.bill_image0);
        imageViews[1] = itemView.findViewById(R.id.bill_image1);
        imageViews[2] = itemView.findViewById(R.id.bill_image2);
        imageViews[3] = itemView.findViewById(R.id.bill_image3);
        imageViews[4] = itemView.findViewById(R.id.bill_image4);

        cardViews = new MaterialCardView[MAX_STACK];
        cardViews[0] = itemView.findViewById(R.id.cell_denomination_tally_card_0);
        cardViews[1] = itemView.findViewById(R.id.cell_denomination_tally_card_1);
        cardViews[2] = itemView.findViewById(R.id.cell_denomination_tally_card_2);
        cardViews[3] = itemView.findViewById(R.id.cell_denomination_tally_card_3);
        cardViews[4] = itemView.findViewById(R.id.cell_denomination_tally_card_4);

        for(int i = 1; i < imageViews.length; i++) {
            cardViews[i].setVisibility(View.GONE);
        }
    }

    public void setSelectedDenomination(final MOVCurrencyDenomination denomination, int amount, int type) {
        setViewType(type);
        tallyLayout.removeAllViews();

        for(int i = 0; i < MAX_STACK; i++) {
            cardViews[i].setVisibility(View.GONE);

            if(denomination.getType() == MOVCurrencyDenominationType.Coin) {
                cardViews[i].setRadius(1000);
            } else {
                cardViews[i].setRadius(0);
            }
        }

        for(int i = 0; i < Math.min(amount, MAX_STACK); i++) {
            cardViews[i].setVisibility(View.VISIBLE);
            imageViews[i].setCurrencyDenomination(denomination);
        }

        while(amount > 0) {

            @DrawableRes int tally;
            int s;
            if(amount / 5 > 0) {
                s = 5;
                tally = R.drawable.tally_5;
            } else if(amount / 4 > 0) {
                s = 4;
                tally = R.drawable.tally_4;
            } else if(amount / 3 > 0) {
                s = 3;
                tally = R.drawable.tally_3;
            } else if(amount / 2 > 0) {
                s = 2;
                tally = R.drawable.tally_2;
            } else {
                s = 1;
                tally = R.drawable.tally_1;
            }
            ImageView tallyImage = createTallyView();
            tallyImage.setImageResource(tally);
            tallyLayout.addView(tallyImage);

            amount -= s;
        }

        int maxImageHeight;
        if(denomination.getType() == MOVCurrencyDenominationType.Bill) {
            maxImageHeight = Integer.MAX_VALUE;
        } else {
            maxImageHeight = dpToPx(50);
        }

        for(int i = 0; i < imageViews.length; i++) {
            imageViews[i].setMaxHeight(maxImageHeight);
        }
    }

    private ImageView createTallyView() {
        int heightPx = dpToPx(TALLY_HEIGHT_DP);
        int paddingPx = dpToPx(TALLY_PADDING_DP);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                heightPx, 1f);
        params.setMargins(paddingPx, paddingPx, paddingPx,paddingPx);
        ImageView imageView = new ImageView(itemView.getContext());
        imageView.setLayoutParams(params);

        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);

        return imageView;
    }

    private void setViewType(int type) {
        FrameLayout.LayoutParams itemViewLayoutParams = (FrameLayout.LayoutParams) containerView.getLayoutParams();
        int tallyHeightPx = dpToPx(TALLY_HEIGHT_DP);
        int tallyPaddingPx = dpToPx(TALLY_PADDING_DP);

        if(type == TransactionAmountSelectionRecyclerAdapter.VIEW_TYPE_VERTICAL) {
            itemViewLayoutParams.setMargins(
                    itemViewLayoutParams.leftMargin,
                    itemViewLayoutParams.topMargin,
                    itemViewLayoutParams.rightMargin,
                    tallyHeightPx + 2 * tallyPaddingPx);
        } else {
            itemViewLayoutParams.setMargins(
                    itemViewLayoutParams.leftMargin,
                    itemViewLayoutParams.topMargin,
                    itemViewLayoutParams.rightMargin,
                    0);
        }
    }

    private int dpToPx(int dp) {
        Resources resources = itemView.getResources();

        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    public View getDragPayloadImage() {
        return cardViews[0];
    }
}
