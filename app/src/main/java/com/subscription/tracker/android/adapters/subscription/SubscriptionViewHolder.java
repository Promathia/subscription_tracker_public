package com.subscription.tracker.android.adapters.subscription;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.subscription.tracker.android.R;
import com.subscription.tracker.android.entity.response.Subscription;

public class SubscriptionViewHolder extends RecyclerView.ViewHolder {

    private final TextView id;
    private final TextView visits;
    private final TextView terms;

    public SubscriptionViewHolder(final View view) {
        super(view);
        this.id = view.findViewById(R.id.subscription_add_item_id);
        this.visits = view.findViewById(R.id.subscription_add_item_visits);
        this.terms = view.findViewById(R.id.subscription_add_item_terms);
    }

    public TextView getId() {
        return id;
    }

    public TextView getVisits() {
        return visits;
    }

    public TextView getTerms() {
        return terms;
    }

    public void bind(final Subscription subscription,
                     final SubscriptionItemClickListener listener,
                     final DialogFragment dialogFragment) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(subscription);
                dialogFragment.dismiss();
            }
        });
    }
}
