package com.rollncode.colorlines.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.rollncode.colorlines.activity.MainActivity;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 01.07.16
 */
public class GooglePlayHelper implements OnConnectionFailedListener {

    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;
    public static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";

    @SuppressLint("StaticFieldLeak")
    private static GooglePlayHelper sInstance;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;

    private boolean mResolvingError = false;

    private GooglePlayHelper(@NonNull Context context, @NonNull View view) {
        mContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .setViewForPopups(view)
                .addApi(Games.API)
                .addOnConnectionFailedListener(this)
                .addScope(Games.SCOPE_GAMES)
                .build();
    }

    public static void init(@NonNull Context context, @NonNull View view) {
        sInstance = new GooglePlayHelper(context, view);
    }

    public static GooglePlayHelper getInstance() {
        return sInstance;
    }

    public GoogleApiClient getClient() {
        return this.mGoogleApiClient;
    }

    public void connect() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void disconnect() {
        if (isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isResolvingError() {
        return mResolvingError;
    }

    public void setResolvingError(boolean resolvingError) {
        mResolvingError = resolvingError;
    }

    public boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    public void enterScore(@NonNull Context context, int score, int size) {
        if (mGoogleApiClient.isConnected()) {
            Games.Leaderboards.submitScore(mGoogleApiClient, Utils.getLeaderboard(context, size), score);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!mResolvingError) {
            if (result.hasResolution()) {
                try {
                    mResolvingError = true;
                    result.startResolutionForResult((Activity) mContext, REQUEST_RESOLVE_ERROR);
                } catch (SendIntentException e) {
                    connect();
                }
            } else {
                showErrorDialog(result.getErrorCode());
                mResolvingError = true;
            }
        }
    }

    private void showErrorDialog(int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(((MainActivity) mContext).getSupportFragmentManager(), DIALOG_ERROR);
    }

    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }

}
