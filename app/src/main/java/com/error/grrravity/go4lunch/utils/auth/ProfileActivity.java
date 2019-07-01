package com.error.grrravity.go4lunch.utils.auth;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.error.grrravity.go4lunch.R;
import com.error.grrravity.go4lunch.controlers.base.BaseActivity;
import com.error.grrravity.go4lunch.utils.helper.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity {

    //FOR DESIGN
    @BindView(R.id.profile_activity_profile_image) ImageView mImageViewProfile;
    @BindView(R.id.profile_activity_username) TextInputEditText mTextInputEditTextUsername;
    @BindView(R.id.profile_activity_mail) TextView mTextViewMail;
    @BindView(R.id.profile_activity_progress_bar) ProgressBar mProgressBar;

    //FOR DATA
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.updateUIWhenCreating();
    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_profile; }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.profile_activity_button_update)
    public void onClickUpdateButton() { this.updateUsernameInFirebase(); }

    @OnClick(R.id.profile_activity_button_sign_out)
    public void onClickSignOutButton() { this.signOutUserFromFirebase(); }

    @OnClick(R.id.profile_activity_button_delete)
    public void onClickDeleteButton() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.popup_confirmation_delete_account)
                .setPositiveButton(R.string.popup_choice_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                    }
                })
                .setNegativeButton(R.string.popup_choice_no, null)
                .show();
    }

    // --------------------
    // REST REQUESTS
    // --------------------

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this,
                        this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {

            //4 - We also delete user from firestore storage
            UserHelper.deleteUser(this.getCurrentUser().getUid())
                    .addOnFailureListener(this.onFailureListener());

            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this,
                            this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
    }

    // 3 - Update User Username
    private void updateUsernameInFirebase(){

        this.mProgressBar.setVisibility(View.VISIBLE);
        String username = this.mTextInputEditTextUsername.getText().toString();

        if (this.getCurrentUser() != null){
            if (!username.isEmpty() &&
                    !username.equals(getString(R.string.info_no_username_found))){
                UserHelper.updateUsername(username,
                        this.getCurrentUser().getUid())
                        .addOnFailureListener(this.onFailureListener())
                        .addOnSuccessListener(this
                                .updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME));
            }
        }
    }

    // --------------------
    // UI
    // --------------------

    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mImageViewProfile);
            }

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser()
                    .getEmail()) ? getString(R.string.info_no_email_found) :
                    this.getCurrentUser().getEmail();

            //Update views with data
            this.mTextViewMail.setText(email);

          //// 5 - Get additional data from Firestore
          //UserHelper.getUser(this.getCurrentUser().getUid())
          //        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
          //    @Override
          //    public void onSuccess(DocumentSnapshot documentSnapshot) {
          //        User currentUser = documentSnapshot.toObject(User.class);
          //        String username = TextUtils.
          //                isEmpty(currentUser.getUsername()) ?
          //                getString(R.string.info_no_username_found) : currentUser.getUsername();
          //        checkBoxIsMentor.setChecked(currentUser.getIsMentor());
          //        mTextInputEditTextUsername.setText(username);
          //    }
          //});
        }
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case UPDATE_USERNAME:
                        mProgressBar.setVisibility(View.INVISIBLE);
                        break;
                    case SIGN_OUT_TASK:
                        finish();
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}
