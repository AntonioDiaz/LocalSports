package com.adiaz.deportelocal.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.adiaz.deportelocal.R;
import com.adiaz.deportelocal.entities.Competition;
import com.adiaz.deportelocal.entities.Match;
import com.adiaz.deportelocal.utilities.retrofit.callbacks.AddIssueCallback;
import com.adiaz.deportelocal.utilities.retrofit.DeporteLocalRestApi;
import com.adiaz.deportelocal.utilities.retrofit.entities.issue.Issue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences;
/* Created by toni on 28/03/2017. */

public class DeporteLocalUtils {

    private static final String TAG = DeporteLocalUtils.class.getSimpleName();


    public static String getStringResourceByName(Context context, String aString) {
        String packageName = context.getPackageName();
        String strResource = aString;
        try {
            int resId = context.getResources().getIdentifier(aString, "string", packageName);
            strResource = context.getString(resId);
        } catch (Exception e) {
            Log.d(TAG, "resource not found at getStringResourceByName: " + aString);
        }
        return strResource;
    }

    public static void showNoInternetAlert(Context context, View view) {
        String strError = context.getString(R.string.internet_required);
        final Snackbar snackbar = Snackbar.make(view, strError, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    public static boolean isShowNotification(Context context) {
        SharedPreferences preferences = getDefaultSharedPreferences(context);
        String notificationsKey = context.getString(R.string.pref_notifications_key);
        boolean notificationsDefault = Boolean.parseBoolean(context.getString(R.string.pref_notifications_default));
        return preferences.getBoolean(notificationsKey, notificationsDefault);
    }

    public static void sendIssue(Context context, Competition competition, Match match, String description, String userEmail) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DeporteLocalConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DeporteLocalRestApi deporteLocalRestApi = retrofit.create(DeporteLocalRestApi.class);
        Issue issue = new Issue();
        issue.setClientId(userEmail);
        issue.setCompetitionId(competition.serverId());
        issue.setMatchId(match.idMatch());
        issue.setDescription(description);
        Call<Long> call = deporteLocalRestApi.addIssue(issue);
        call.enqueue(new AddIssueCallback(context));
    }

    public static void disableImageButton(ImageButton imageButton) {
        imageButton.setClickable(false);
        imageButton.setEnabled(false);
        Drawable drawableDisabled = DeporteLocalUtils.convertDrawableToGrayScale(imageButton.getDrawable());
        imageButton.setImageDrawable(drawableDisabled);
    }

    public static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }

    public static String matchStateName(Context context, Integer state) {
        String stateDesc = "";
        switch (state) {
            case DeporteLocalConstants.STATE_PLAYED:
                stateDesc = context.getString(R.string.match_played);
                break;
            case DeporteLocalConstants.STATE_PENDING:
                stateDesc = context.getString(R.string.match_pending);
                break;
            case DeporteLocalConstants.STATE_CANCELED:
                stateDesc = context.getString(R.string.match_cancel);
                break;
            case DeporteLocalConstants.STATE_RESTING:
                stateDesc = context.getString(R.string.match_resting);
                break;
        }
        return stateDesc;
    }

    public static boolean isUserLogged() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser!=null && currentUser.isEmailVerified();
    }
}


