package com.itheamc.hamroclassroom_teachers.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseUser;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.SpinnerAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentRegisterBinding;
import com.itheamc.hamroclassroom_teachers.handlers.QueryHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.LocalStorage;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.TimeUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.LoginViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class RegisterFragment extends Fragment implements QueryCallbacks, AdapterView.OnItemSelectedListener {
    private static final String TAG = "RegisterFragment";
    private FragmentRegisterBinding registerBinding;
    private NavController navController;
    private LoginViewModel loginViewModel;


    // EditText
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText addressEditText;

    /*
    For Gender Spinner
     */
    private SpinnerAdapter spinnerAdapter;
    private final String[] genders = new String[]{"Gender", "Male", "Female", "Other"};
    private int gender_position;


    public RegisterFragment() {
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
        registerBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        return registerBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing view model and navController
        navController = Navigation.findNavController(view);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        //Spinner Initialization
        // Spinner Adapter
        if (getContext() != null)
            spinnerAdapter = new SpinnerAdapter(getContext(), R.layout.custom_dropdown_item, genders);
        if (spinnerAdapter != null) registerBinding.spinnerGender.setAdapter(spinnerAdapter);
        registerBinding.spinnerGender.setOnItemSelectedListener(this);


        // Initializing the EditTexts
        nameEditText = registerBinding.editTextName;
        emailEditText = registerBinding.editTextEmail;
        phoneEditText = registerBinding.editTextPhone;
        addressEditText = registerBinding.editTextAddress;

        // Calling function to set the known data got from firebase user to the edittext
        setKnownValue();

        // Setting on click listener on the continue button
        registerBinding.continueButton.setOnClickListener(v -> {
            handleUserStore();
        });

    }

    // Setting some known value to the edit text
    private void setKnownValue() {
        FirebaseUser user = loginViewModel.getFirebaseUser();
        if (user != null) {
            if (user.getDisplayName() != null) nameEditText.setText(user.getDisplayName());
            if (user.getEmail() != null) {
                emailEditText.setText(user.getEmail());
                emailEditText.setEnabled(false);
            }
            if (user.getPhoneNumber() != null) phoneEditText.setText(user.getPhoneNumber());
        }
    }

    //----------------------------------------------------------------
    // Function to handle the storage of user data to the Firestore
    private void handleUserStore() {
        FirebaseUser firebaseUser = loginViewModel.getFirebaseUser();

        if (firebaseUser != null && isValidated()) {
            String _name = nameEditText.getText().toString().trim();
            String _phone = phoneEditText.getText().toString().trim();
            String _email = emailEditText.getText().toString().trim();
            String _address = addressEditText.getText().toString().trim();
            String _gender = genders[gender_position];

            User user = new User(
                    firebaseUser.getUid(),
                    _name,
                    _gender,
                    String.valueOf(firebaseUser.getPhotoUrl()),
                    _phone,
                    _email,
                    _address,
                    "",
                    null,
                    TimeUtils.now()
            );

            QueryHandler.getInstance(this).storeUser(user);
            ViewUtils.showProgressBar(registerBinding.overlayLayout);
            ViewUtils.disableViews(
                    nameEditText,
                    phoneEditText,
                    emailEditText,
                    addressEditText,
                    registerBinding.spinnerGender,
                    registerBinding.continueButton);
        }

    }


    //-----------------------------------------------------
    // Function to validate the data
    private boolean isValidated() {
        if (getContext() == null) return false;

        String _name = nameEditText.getText().toString().trim();
        String _phone = phoneEditText.getText().toString().trim();
        String _email = emailEditText.getText().toString().trim();
        String _address = addressEditText.getText().toString().trim();
        if (TextUtils.isEmpty(_name) ||
                TextUtils.isEmpty(_email) ||
                TextUtils.isEmpty(_phone) ||
                TextUtils.isEmpty(_address) ||
                gender_position == 0) {
            NotifyUtils.showToast(getContext(), "Please enter all the data");
            return false;
        }

        return true;

    }


    /**
     * ----------------------------------------------------------------------
     * Function to store info on local storage
     * ----------------------------------------------------------------------
     */
    private void storeInfo() {
        LocalStorage localStorage = null;
        String userId = loginViewModel.getFirebaseUser().getUid();
        if (getActivity() != null) localStorage = LocalStorage.getInstance(getActivity());
        if (localStorage == null) return;
        localStorage.storeUserId(userId);
    }

    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the QueryCallbacks
     * -------------------------------------------------------------------
     */
    @Override
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Notice> notices) {

    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Submission submission, Notice notice) {

    }

    @Override
    public void onQuerySuccess(String message) {
        if (registerBinding == null) return;
        storeInfo();
        ViewUtils.hideProgressBar(registerBinding.overlayLayout);
        requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onQueryFailure(Exception e) {
        if (registerBinding == null) return;
        if (getContext() != null) NotifyUtils.showToast(getContext(), e.getMessage());
        ViewUtils.hideProgressBar(registerBinding.overlayLayout);
        ViewUtils.enableViews(
                nameEditText,
                phoneEditText,
                emailEditText,
                addressEditText,
                registerBinding.spinnerGender,
                registerBinding.continueButton);
    }


    /**
     * Methods implemented from the AdapterView.OnItemSelectedListener
     *
     * @param parent   -
     * @param view     -
     * @param position -
     * @param id       -
     */

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gender_position = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        gender_position = 1;
    }

    // On Fragment Destroy
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        registerBinding = null;
    }


}