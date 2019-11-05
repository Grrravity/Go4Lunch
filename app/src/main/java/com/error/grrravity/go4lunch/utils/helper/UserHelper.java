package com.error.grrravity.go4lunch.utils.helper;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class UserHelper {

    public interface OnRequestListener{
        void onResult(List<User> userList);
    }

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_RESTAURANTID = "restaurantId";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }



    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture) {
        // 1 - Create Obj
        User userToCreate = new User(uid, username, urlPicture);

        return UserHelper.getUsersCollection()
                .document(uid)
                .set(userToCreate);
    }



    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<QuerySnapshot> getRestaurant(String restaurantId){
        return UserHelper.getUsersCollection().whereEqualTo("restaurantId", restaurantId).get();
    }



    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid, String urlPicture) {
        return UserHelper.getUsersCollection().document(uid).update("username", username, "urlPicture", urlPicture);
    }



    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }


    // FIREBASE

    @Nullable
    public static FirebaseUser getCurrentUser(){return FirebaseAuth.getInstance().getCurrentUser(); }

    public static boolean isCurrentUserLogged(){return (getCurrentUser() != null); }


}

