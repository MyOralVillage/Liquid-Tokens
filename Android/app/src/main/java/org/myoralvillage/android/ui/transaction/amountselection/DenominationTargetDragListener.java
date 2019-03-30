package org.myoralvillage.android.ui.transaction.amountselection;

import android.view.DragEvent;
import android.view.View;

public class DenominationTargetDragListener implements View.OnDragListener {

    private final TransactionAmountSelectionViewModel model;
    private final boolean add;

    /**
     * A drag listener to interact with BillImageView dragging
     *
     * @param model the TransactionAmountSelectionViewModel in this context
     * @param add If true, the parent view will add currency to the model. Else, remove.
     */
    public DenominationTargetDragListener(TransactionAmountSelectionViewModel model, boolean add) {
        this.model = model;
        this.add = add;
    }
    @Override
    public boolean onDrag(View v, DragEvent event) {
        DenominationDragPayload payload = (DenominationDragPayload) event.getLocalState();

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                if(!payload.isSelectedAmount()) {
                    model.setAdding(true);
                } else {
                    model.setRemoving(true);
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                if(add) {
                    model.addCurrency(payload.getDenomination());
                }

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                if(add && !event.getResult()) {
                    model.addCurrency(payload.getDenomination());
                }

                model.setAdding(false);
                model.setRemoving(false);
            default:
                break;
        }
        return true;
    }
}



