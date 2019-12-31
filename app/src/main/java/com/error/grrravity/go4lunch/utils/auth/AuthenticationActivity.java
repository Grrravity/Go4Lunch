package com.error.grrravity.go4lunch.utils.auth;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.MainActivity;
import com.error.grrravity.go4lunch.controllers.base.BaseActivity;
import com.error.grrravity.go4lunch.utils.helper.User;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;
import com.facebook.FacebookSdk;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Collections;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class AuthenticationActivity extends BaseActivity {


    //FOR DATA
        private static final int RC_SIGN_IN = 123;
        private static final String TAG = "AuthenticationActivity";
        private final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    public int getFragmentLayout() {
        return R.layout.authentication_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestLocationPermission();

        FacebookSdk.sdkInitialize(getApplicationContext());
        if(UserHelper.isCurrentUserLogged()){
            this.logSucceed();
        }
    }

    @OnClick(R.id.auth_google_login_btn)
    public void onClickGoogleButton() {
        if (UserHelper.isCurrentUserLogged()) {
            this.logSucceed();
        } else {
            //this.startSignInActivityForGoogle();
            this.signInInitiate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // - Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void signInInitiate(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .build(),
                RC_SIGN_IN);
    }

    //private void startSignInActivityForGoogle(){
    //    startActivityForResult(
    //            AuthUI.getInstance()
    //                    .createSignInIntentBuilder()
    //                    .setAvailableProviders(
    //                            Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.GOOOGLE_PROVIDER).build())) //GOOGLE
    //                    .setIsSmartLockEnabled(false, true)
    //                    .build(), RC_SIGN_IN);
    //}
//
    //// - Launch Sign-In Activity for Facebook
    //private void startSignInActivityForFacebook(){
    //    startActivityForResult(
    //            AuthUI.getInstance()
    //                    .createSignInIntentBuilder()
    //                    .setAvailableProviders(
    //                            Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build())) //FACEBOOK
    //                    .setIsSmartLockEnabled(false, true)
    //                    .build(), RC_SIGN_IN);
    //}

    private void createUserFireStore(){
        if(UserHelper.getCurrentUser() != null) {
            String urlPicture = (UserHelper.getCurrentUser().getPhotoUrl() != null) ? UserHelper.getCurrentUser().getPhotoUrl().toString() : null;
            String username = UserHelper.getCurrentUser().getDisplayName();
            String uid = UserHelper.getCurrentUser().getUid();

            UserHelper.getUser(UserHelper.getCurrentUser().getUid()).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    User currentUser = task.getResult().toObject(User.class);
                    if(currentUser != null && UserHelper.getCurrentUser().getUid().equals(currentUser.getUid())){
                        UserHelper.updateUsername(uid, username, urlPicture);
                        this.logSucceed();
                    } else {
                        UserHelper.createUser(uid, username, urlPicture).addOnCompleteListener(task1 -> {
                            this.logSucceed();
                        })
                                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_user_creation), Toast.LENGTH_SHORT).show());
                    }

                }
            });

        }

    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.success_connection), Toast.LENGTH_SHORT).show();
                this.createUserFireStore();

            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_authentication_canceled), Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // - Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void logSucceed(){
        Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
        }
        else {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.error_location_permission), REQUEST_LOCATION_PERMISSION, perms);
        }
    }

}
