package com.subscription.tracker.android.adapters.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.subscription.tracker.android.Constants;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.adapters.subscription.SubscriptionItemClickListener;
import com.subscription.tracker.android.dialogs.SubscriptionAddDialog;
import com.subscription.tracker.android.dialogs.VisitorInfoDialog;
import com.subscription.tracker.android.entity.request.UserUpdatePostRequest;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.entity.response.Subscription;
import com.subscription.tracker.android.entity.response.UserForClubResponse;
import com.subscription.tracker.android.listener.SuccessRequestListener;
import com.subscription.tracker.android.services.SharedPreferencesService;
import com.subscription.tracker.android.services.SubscriptionService;
import com.subscription.tracker.android.services.UserService;
import com.subscription.tracker.android.services.utils.ClubUtils;
import com.subscription.tracker.android.services.utils.RoleUtils;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private final List<UserForClubResponse> users;
    private final Fragment parent;
    private List<Subscription> subscriptionList = new ArrayList<>();
    private final SingleUserData currentUser;
    private final SubscriptionService subscriptionService;

    public UserListAdapter(List<UserForClubResponse> users, Fragment parent) {
        this.users = users;
        this.parent = parent;
        this.currentUser = new SharedPreferencesService(parent.getContext())
                .getObject(R.string.preference_user_data_key, SingleUserData.class);
        final int clubId = ClubUtils.getActiveClub(currentUser).getId();
        this.subscriptionService = new SubscriptionService(parent.getContext());
        this.subscriptionService.initializeSubscriptions(subscriptionList, clubId);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public UserViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_card_item, viewGroup, false);
        return new UserViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder viewHolder, final int position) {
        final UserForClubResponse userForClubResponse = users.get(position);
        viewHolder.getUserId().setText(String.valueOf(userForClubResponse.getId()));
        viewHolder.getFirstName().setText(userForClubResponse.getFirstName());
        viewHolder.getLastName().setText(userForClubResponse.getLastName());
        viewHolder.getBirthDate().setText(Constants.DATE_FORMATTER.format(userForClubResponse.getBirthDate()));
        viewHolder.getRole().setText(RoleUtils.getRoleTextIdByName(userForClubResponse.getRole().getRoleName()));
        if (!userForClubResponse.getUserAccepted()) {
            handleUserNotAcceptedControls(viewHolder, userForClubResponse);
        } else {
            handleUserAcceptedControls(viewHolder, userForClubResponse);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final TextView userId = view.findViewById(R.id.user_list_id);
                showVisitorInfoDialog(Integer.parseInt(userId.getText().toString()),
                        new SuccessRequestListener<UserUpdatePostRequest>() {
                            @Override
                            public void doUpdate(final UserUpdatePostRequest data) {
                                if (data.getUserName() != null && !data.getUserName().isEmpty()) {
                                    viewHolder.getFirstName().setText(data.getUserName());
                                }
                                if (data.getUserSurname() != null && !data.getUserSurname().isEmpty()) {
                                    viewHolder.getLastName().setText(data.getUserSurname());
                                }
                                if (data.getUserBirthdate() != null && !data.getUserBirthdate().isEmpty()) {
                                    viewHolder.getBirthDate().setText(data.getUserBirthdate());
                                }
                                if (data.getUserRole() != null && !data.getUserRole().isEmpty()) {
                                    viewHolder.getRole().setText(RoleUtils.getRoleTextIdByRoleId(Integer.parseInt(data.getUserRole())));
                                }
                            }
                        });
            }
        });
    }

    private void handleUserAcceptedControls(final UserViewHolder viewHolder,
                                            final UserForClubResponse userForClubResponse) {
        viewHolder.getAcceptUserButton().setEnabled(false);
        viewHolder.getAddSubscriptionButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSubscriptionDialog(userForClubResponse.getId());
            }
        });
    }

    private void showVisitorInfoDialog(final int userId, final SuccessRequestListener<UserUpdatePostRequest> successListener) {
        final FragmentTransaction fragmentTransaction = parent.getFragmentManager().beginTransaction();
        final Fragment prev = parent.getFragmentManager().findFragmentByTag("userInfoDialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        final DialogFragment dialogFragment = new VisitorInfoDialog(successListener);
        final Bundle args = new Bundle();
        args.putInt("userId", userId);
        args.putInt("userClubId", ClubUtils.getActiveClub(currentUser).getId());
        args.putInt("requestorId", currentUser.getId());
        args.putInt("requestorRoleId", ClubUtils.getActiveUserClub(currentUser).getRole().getId());
        dialogFragment.setArguments(args);
        dialogFragment.show(fragmentTransaction, "userInfoDialog");
    }

    private void showAddSubscriptionDialog(final int userId) {
        final FragmentTransaction fragmentTransaction = parent.getFragmentManager().beginTransaction();
        final Fragment prev = parent.getFragmentManager().findFragmentByTag("subscriptionListDialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        final DialogFragment dialogFragment = new SubscriptionAddDialog(subscriptionList, new SubscriptionItemClickListener() {
            @Override
            public void onItemClick(Subscription item) {
                subscriptionService.addSubscriptionToUser(userId, item, currentUser.getId());
            }
        });
        dialogFragment.show(fragmentTransaction, "subscriptionListDialog");
    }


    private void handleUserNotAcceptedControls(final UserViewHolder viewHolder,
                                               final UserForClubResponse userForClubResponse) {
        viewHolder.getAddSubscriptionButton().setEnabled(false);
        viewHolder.getUseSubscriptionButton().setEnabled(false);
        viewHolder.getAcceptUserButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                viewHolder.getAcceptUserButton().setEnabled(false);
                if (currentUser != null) {
                    new UserService(parent.getContext()).executeUserAcceptRequest(currentUser, userForClubResponse, viewHolder);
                }
            }
        });
    }

}
