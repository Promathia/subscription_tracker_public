package com.subscription.tracker.android.activity.main.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.subscription.tracker.android.Constants;
import com.subscription.tracker.android.R;
import com.subscription.tracker.android.entity.response.SingleUserData;
import com.subscription.tracker.android.entity.response.UserSubscription;
import com.subscription.tracker.android.listener.FailRequestListener;
import com.subscription.tracker.android.listener.SuccessRequestListener;
import com.subscription.tracker.android.services.EncryptionService;
import com.subscription.tracker.android.services.SharedPreferencesService;
import com.subscription.tracker.android.services.SubscriptionService;
import com.subscription.tracker.android.services.utils.ClubUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class VisitorHomeFragment extends Fragment {

    private LinearLayout linearLayout;
    private TextView visits;
    private TextView deadline;
    private ImageView qrCode;
    private TextView userNotAccepted;
    private TextView noActiveSubscriptions;
    private SingleUserData singleUserResponse;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home_visitor, container, false);
        linearLayout = root.findViewById(R.id.visitor_home_subscription_container);
        visits = root.findViewById(R.id.visitor_home_subscription_visits);
        deadline = root.findViewById(R.id.visitor_home_subscription_deadline);
        qrCode = root.findViewById(R.id.user_subscription__qr_code);
        userNotAccepted = root.findViewById(R.id.visitor_home_not_accepted_to_club_message);
        noActiveSubscriptions = root.findViewById(R.id.visitor_home_no_active_subscriptions);
        return root;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.singleUserResponse = new SharedPreferencesService(getContext())
                .getObject(R.string.preference_user_data_key, SingleUserData.class);
        if (this.singleUserResponse == null) {
            return;
        }
        if (!ClubUtils.getActiveUserClub(this.singleUserResponse).isUserAccepted()) {
            userNotAccepted.setVisibility(View.VISIBLE);
            return;
        }
        populateUserSubscription();
    }

    private void populateUserSubscription() {
        new SubscriptionService(getContext()).populateSubscriptionForUser(singleUserResponse.getId(),
                new SuccessRequestListener<UserSubscription>() {
                    @Override
                    public void doUpdate(final UserSubscription userSubscription) {
                        setVisitField(userSubscription);
                        setDeadlineField(userSubscription.getDeadline());
                        linearLayout.setVisibility(View.VISIBLE);
                        qrCode.setVisibility(View.VISIBLE);
                        generateAndDrawQRCode(prepareJsonForQRCode(userSubscription), qrCode);
                    }
                },
                new FailRequestListener() {
                    @Override
                    public void doUpdate() {
                        noActiveSubscriptions.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setVisitField(UserSubscription userSubscription) {
        String visitsString;
        Integer visitCounter = userSubscription.getVisitCounter();
        Integer visitsLimit = userSubscription.getVisitsLimit();
        if (userSubscription.getVisitsLimit() == 0) {
            visitsString = getString(R.string.subscription_add_item_unlimited_text);
        } else {
            visitsString = String.format(
                    "%s %s %s",
                    visitCounter,
                    getString(R.string.visitor_home_subscription_total_from_used),
                    visitsLimit);
        }
        visits.setText(visitsString);
        if ((visitsLimit - visitCounter) == 1) {
            this.visits.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void setDeadlineField(Date deadline) {
        this.deadline.setText(Constants.DATE_FORMATTER.format(deadline));
        long diffInMillies = Math.abs(new Date().getTime() - deadline.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        if (diff <= Constants.VISITOR_CLOSE_DEADLINE_DAY_VALUE) {
            this.deadline.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private String prepareJsonForQRCode(final UserSubscription userSubscription) {
        try {
            final JSONObject jsonObject = new JSONObject(new ObjectMapper().writeValueAsString(userSubscription));
            jsonObject.put("userId", singleUserResponse.getId());
            return jsonObject.toString();
        } catch (JSONException | JsonProcessingException e) {
            Toast.makeText(getContext(), getContext().getString(R.string.error_general), Toast.LENGTH_LONG).show();
        }
        return new JSONObject().toString();
    }

    private void generateAndDrawQRCode(final String data, final ImageView imageView) {
        final int width = getResources().getConfiguration().screenWidthDp;
        final int height = getResources().getConfiguration().screenHeightDp;
        final int smallerDimension = Math.min(width, height);
        final QRGEncoder qrgEncoder = new QRGEncoder(new EncryptionService().encryptString(data), null, QRGContents.Type.TEXT, smallerDimension);
        try {
            imageView.setImageBitmap(qrgEncoder.encodeAsBitmap());
        } catch (WriterException e) {
            Toast.makeText(getContext(), getContext().getString(R.string.error_qr_code_generation_for_user_subscription), Toast.LENGTH_LONG).show();
        }
    }

}