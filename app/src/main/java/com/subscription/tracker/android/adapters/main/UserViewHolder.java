package com.subscription.tracker.android.adapters.main;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.subscription.tracker.android.R;

public class UserViewHolder extends RecyclerView.ViewHolder {

    private final TextView userId;
    private final TextView firstName;
    private final TextView lastName;
    private final TextView birthDate;
    private final TextView role;
    private final Button addSubscription;
    private final Button useSubscription;
    private final Button acceptUser;
    private final UserListAdapter userListAdapter;

    public UserViewHolder(final View view, final UserListAdapter userListAdapter) {
        super(view);
        this.userId = view.findViewById(R.id.user_list_id);
        this.firstName = view.findViewById(R.id.user_list_name);
        this.lastName = view.findViewById(R.id.user_list_surname);
        this.birthDate = view.findViewById(R.id.user_list_birthdate);
        this.role = view.findViewById(R.id.user_list_role);
        this.addSubscription = view.findViewById(R.id.add_subscription);
        this.useSubscription = view.findViewById(R.id.use_subscription);
        this.acceptUser = view.findViewById(R.id.accept_user);
        this.userListAdapter = userListAdapter;
    }

    public TextView getUserId() {
        return userId;
    }

    public TextView getFirstName() {
        return firstName;
    }

    public TextView getLastName() {
        return lastName;
    }

    public TextView getBirthDate() {
        return birthDate;
    }

    public TextView getRole() {
        return role;
    }

    public Button getAddSubscriptionButton() {
        return addSubscription;
    }

    public Button getUseSubscriptionButton() {
        return useSubscription;
    }

    public Button getAcceptUserButton() {
        return acceptUser;
    }

    public void notifyItemChanged() {
        userListAdapter.notifyItemChanged(this.getAdapterPosition());
    }
}
