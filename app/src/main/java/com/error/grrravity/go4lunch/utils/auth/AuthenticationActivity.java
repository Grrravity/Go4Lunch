package com.error.grrravity.go4lunch.utils.auth;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.Button;

import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controllers.MainActivity;
import com.error.grrravity.go4lunch.controllers.base.BaseActivity;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthenticationActivity extends BaseActivity {

        //FOR DATA
        private static final int RC_SIGN_IN = 123;

        //FOR DESIGN
        @BindView (R.id.ma_login_button) Button mButtonLogin;
        @BindView (R.id.ma_coordinator_layout) CoordinatorLayout mCoordinatorLayout;

        @Override
        public int getFragmentLayout() {
            return R.layout.authentication_activity;
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            this.handleResponseAfterSignIn(requestCode, resultCode, data);
        }

        @Override
        protected void onResume() {
            super.onResume();
           // this.updateUIWhenResuming();
        }

        // --------------------
        // NAVIGATION
        // --------------------

        private void startSignInActivity(){
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.LoginTheme)
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                                                    .build(), //GOOGLE
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
                                                    .build())) // FACEBOOK
                            .setIsSmartLockEnabled(false, true)
                            .setLogo(R.drawable.goforfood_front_full)
                            .build(),
                    RC_SIGN_IN);
        }

        // -------------------
        // ACTION
        // -------------------
        @OnClick (R.id.ma_login_button)
        public void onClickLoginButton(){
            if (this.isCurrentUserLogged()){
                this.startMainActivity();
            } else {
                this.startSignInActivity();
            }
        }

        private void startMainActivity(){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        // --------------------
        // REST REQUEST
        // --------------------

        private void createUserInFirestore(){

            if (this.getCurrentUser() != null){

                String urlPicture = (this.getCurrentUser().getPhotoUrl() != null)
                        ? this.getCurrentUser().getPhotoUrl().toString() : null;
                String username = this.getCurrentUser().getDisplayName();
                String uid = this.getCurrentUser().getUid();

                UserHelper.createUser(uid, username, urlPicture)
                        .addOnFailureListener(this.onFailureListener());
            }
        }

        // --------------------
        // UI
        // --------------------

        private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
            Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
        }

       // private void updateUIWhenResuming(){
       //     this.mButtonLogin.setText(this.isCurrentUserLogged()
       //             ? getString(R.string.button_login_text_logged)
       //             : getString(R.string.button_login_text_not_logged));
       // }

        // --------------------
        // UTILS
        // --------------------

        private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (requestCode == RC_SIGN_IN) {
                if (resultCode == RESULT_OK) { // SUCCESS
                    this.createUserInFirestore();
                    showSnackBar(this.mCoordinatorLayout, getString(R.string.connection_succeed));
                } else { // ERRORS
                    if (response == null) {
                        showSnackBar(this.mCoordinatorLayout,
                                getString(R.string.error_authentication_canceled));
                    } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                        showSnackBar(this.mCoordinatorLayout,
                                getString(R.string.error_no_internet));
                    } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar(this.mCoordinatorLayout,
                                getString(R.string.error_unknown_error));
                    }
                }
            }
        }
    }
