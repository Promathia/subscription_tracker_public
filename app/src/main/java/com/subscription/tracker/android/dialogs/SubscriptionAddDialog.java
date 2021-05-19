package com.subscription.tracker.android.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.subscription.tracker.android.R;
import com.subscription.tracker.android.adapters.subscription.SubscriptionAddDialogAdapter;
import com.subscription.tracker.android.adapters.subscription.SubscriptionItemClickListener;
import com.subscription.tracker.android.entity.response.Subscription;

import java.util.List;

public class SubscriptionAddDialog extends DialogFragment {

    private RecyclerView mRecyclerView;
    private List<Subscription> subscriptionList;
    private SubscriptionItemClickListener listener;

    public SubscriptionAddDialog(final List<Subscription> subscriptionList,
                                 final SubscriptionItemClickListener listener) {
        this.subscriptionList = subscriptionList;
        this.listener = listener;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_add_subscription, container, false);
        mRecyclerView = view.findViewById(R.id.subscriptions_add_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        final SubscriptionAddDialogAdapter adapter =
                new SubscriptionAddDialogAdapter(subscriptionList, this, listener);
        mRecyclerView.setAdapter(adapter);
        return view;
    }
}