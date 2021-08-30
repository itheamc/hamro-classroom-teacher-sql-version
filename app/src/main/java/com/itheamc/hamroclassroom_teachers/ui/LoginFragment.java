package com.itheamc.hamroclassroom_teachers.ui;

import static com.itheamc.hamroclassroom_teachers.utils.Constants.FACEBOOK_SIGN_IN_REQUEST_CODE;
import static com.itheamc.hamroclassroom_teachers.utils.Constants.GOOGLE_SIGN_IN_REQUEST_CODE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.callbacks.LoginCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentLoginBinding;
import com.itheamc.hamroclassroom_teachers.handlers.LoginHandler;
import com.itheamc.hamroclassroom_teachers.handlers.PathHandler;
import com.itheamc.hamroclassroom_teachers.handlers.QueryHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Material;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.LocalStorage;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.LoginViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoginFragment extends Fragment implements LoginCallbacks, QueryCallbacks, View.OnClickListener {
    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding loginBinding;
    private NavController navController;
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private LoginHandler loginHandler;
    private LoginViewModel viewModel;
    private int LOGIN_REQUEST_CODE = 0;



    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        return loginBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing view model and navController
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        // Setting onClickListener on these views
        loginBinding.facebookLoginButton.setOnClickListener(this);
        loginBinding.googleLoginButton.setOnClickListener(this);
        loginBinding.privacyPolicy.setOnClickListener(this);


        // Activity Result launcher to listen the result of the googleSignIn intent
        ActivityResultLauncher<Intent> signInActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (loginBinding == null) return;
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            handleIntentResult(data);   // Calling function to handle google signin result
                        } else {
                            ViewUtils.hideProgressBar(loginBinding.overlayLayout);
                            ViewUtils.enableViews(loginBinding.facebookLoginButton, loginBinding.googleLoginButton);
                            NotifyUtils.showToast(getContext(), getString(R.string.unable_to_login_message));
                        }
                    }
                });

        /*
        Initializing LoginHandler Object
         */
        loginHandler = LoginHandler.getInstance(this, mAuth, this, signInActivityResultLauncher);
    }

    @Override
    public void onClick(View v) {
        int _id = v.getId();
        if (_id == loginBinding.facebookLoginButton.getId()) {
            LOGIN_REQUEST_CODE = FACEBOOK_SIGN_IN_REQUEST_CODE;
            ViewUtils.disableViews(loginBinding.facebookLoginButton, loginBinding.googleLoginButton);
            loginHandler.fbLoginHandler(callbackManager);

        } else if (_id == loginBinding.googleLoginButton.getId()) {
            LOGIN_REQUEST_CODE = GOOGLE_SIGN_IN_REQUEST_CODE;
            ViewUtils.showProgressBar(loginBinding.overlayLayout);
            ViewUtils.disableViews(loginBinding.facebookLoginButton, loginBinding.googleLoginButton);
            loginHandler.googleLoginHandler();

        } else if (_id == loginBinding.privacyPolicy.getId()) {
            Uri uri = Uri.parse(PathHandler.PRIVACY_POLICY_URL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            NotifyUtils.logDebug(TAG, "Unspecified view is clicked");
        }

    }


    /**
     * -----------------------------------------------------------------------
     * Function to handle the google sign in activity result
     * -----------------------------------------------------------------------
     */
    public void handleIntentResult(Intent data) {
        if (data != null) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener(signInAccount -> {
                        loginHandler.firebaseAuthWithGoogle(signInAccount.getIdToken());
                        NotifyUtils.logDebug(TAG, signInAccount.getIdToken());
                    })
                    .addOnFailureListener(e -> {
                        NotifyUtils.logError(TAG, "onActivityResult", e);
                        NotifyUtils.showToast(getContext(), "You cancelled the login request");
                        ViewUtils.hideProgressBar(loginBinding.overlayLayout);
                        ViewUtils.enableViews(loginBinding.facebookLoginButton, loginBinding.googleLoginButton);

                    });
        } else {
            ViewUtils.hideProgressBar(loginBinding.overlayLayout);
            ViewUtils.enableViews(loginBinding.facebookLoginButton, loginBinding.googleLoginButton);
            NotifyUtils.showToast(getContext(), getString(R.string.unable_to_login_message));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // For Facebook Login
        if (LOGIN_REQUEST_CODE == FACEBOOK_SIGN_IN_REQUEST_CODE) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }


    /**
     * ----------------------------------------------------------------------
     * Function to navigate to the Main Activity if everything if ok
     * ----------------------------------------------------------------------
     */
    private void startMainActivity() {
        requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
        requireActivity().finish();
    }

    /**
     * ----------------------------------------------------------------------
     * Function to store info on local storage
     * ----------------------------------------------------------------------
     */
    private void storeInfo(User user) {
        LocalStorage localStorage = null;
        if (getActivity() != null) localStorage = LocalStorage.getInstance(getActivity());

        if (localStorage == null) return;
        localStorage.storeUserId(user.get_id());
    }


    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the QueryCallbacks
     * -------------------------------------------------------------------
     */
    @Override
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Material> materials, List<Submission> submissions, List<Notice> notices) {

    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Material material, Submission submission, Notice notice) {
        if (loginBinding == null) return;
        if (user != null) {
            NotifyUtils.logDebug(TAG, user.toString());
            if (getActivity() != null) storeInfo(user);
            startMainActivity();
        } else {
            viewModel.setFirebaseUser(mAuth.getCurrentUser());
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        }
    }

    @Override
    public void onQuerySuccess(String message) {
        if (loginBinding == null) return;
        ViewUtils.hideProgressBar(loginBinding.overlayLayout);
        ViewUtils.enableViews(loginBinding.facebookLoginButton, loginBinding.googleLoginButton);
        viewModel.setFirebaseUser(mAuth.getCurrentUser());
        navController.navigate(R.id.action_loginFragment_to_registerFragment);
    }

    @Override
    public void onQueryFailure(Exception e) {
        if (loginBinding == null) return;
        ViewUtils.hideProgressBar(loginBinding.overlayLayout);
        ViewUtils.enableViews(loginBinding.facebookLoginButton, loginBinding.googleLoginButton);
        NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        NotifyUtils.logDebug(TAG, "onUserInfoRetrievedError: - " + e.getMessage());
    }

    /**
     * ---------------------------------------------------------------------------
     * These are functions Overrided from the LoginStatusCallback
     *
     * @param user -- It the instance of the firebase user after successful login
     *             ---------------------------------------------------------------------------
     */
    @Override
    public void onLoginSuccess(@NonNull FirebaseUser user) {
        if (loginBinding == null) return;
        QueryHandler.getInstance(this).getUser(user.getUid());
    }

    @Override
    public void onLoginFailure(@NonNull String errorMessage) {
        if (loginBinding == null) return;
        ViewUtils.hideProgressBar(loginBinding.overlayLayout);
        ViewUtils.enableViews(loginBinding.facebookLoginButton, loginBinding.googleLoginButton);
        NotifyUtils.logDebug(TAG, errorMessage);
        NotifyUtils.showToast(getContext(), errorMessage.split("--")[1]);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loginBinding = null;
    }

}