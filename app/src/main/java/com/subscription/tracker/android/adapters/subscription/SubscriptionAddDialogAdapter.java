package com.subscription.tracker.android.adapters.subscription;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.subscription.tracker.android.R;
import com.subscription.tracker.android.entity.response.Subscription;

import java.util.List;


public class SubscriptionAddDialogAdapter extends RecyclerView.Adapter<SubscriptionViewHolder> {

    private final List<Subscription> subscriptionList;
    private final DialogFragment dialogFragment;
    private final SubscriptionItemClickListener listener;

    public SubscriptionAddDialogAdapter(final List<Subscription> subscriptionList,
                                        final DialogFragment dialogFragment,
                                        final SubscriptionItemClickListener listener) {
        this.subscriptionList = subscriptionList;
        this.dialogFragment = dialogFragment;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return subscriptionList.size();
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.subscription_add_item, viewGroup, false);
        return new SubscriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SubscriptionViewHolder viewHolder, final int position) {
        final Subscription subscription = subscriptionList.get(position);
        viewHolder.getId().setText(String.valueOf(subscription.getId()));
        viewHolder.getTerms().setText(String.valueOf(subscription.getTermDays()));
        String visits;
        if (subscription.getVisitsLimit() == 0) {
            visits = dialogFragment.getContext().getString(R.string.subscription_add_item_unlimited_text);
        } else {
            visits = String.valueOf(subscription.getVisitsLimit());
        }
        viewHolder.getVisits().setText(visits);
        viewHolder.bind(subscription, listener, dialogFragment);
    }

}
