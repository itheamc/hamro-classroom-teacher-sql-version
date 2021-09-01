package com.itheamc.hamroclassroom_teachers.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseUser;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.SchoolAdapter;
import com.itheamc.hamroclassroom_teachers.adapters.SpinnerAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.SchoolCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentRegisterBinding;
import com.itheamc.hamroclassroom_teachers.databinding.SchoolBottomSheetBinding;
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
import com.itheamc.hamroclassroom_teachers.utils.TimeUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.LoginViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class RegisterFragment extends Fragment implements QueryCallbacks, SchoolCallbacks, AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final String TAG = "RegisterFragment";
    private FragmentRegisterBinding registerBinding;
    private NavController navController;
    private LoginViewModel loginViewModel;
    private List<School> schools;
    private School school;

    /*
    For Bottom Sheet -- Schools list
    */
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private SchoolBottomSheetBinding bottomSheetBinding;
    private SchoolAdapter schoolAdapter;


    // EditText
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText schoolEditText;

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

          /*
        Initializing Bottom Sheet and its views
         */
        ConstraintLayout bottomSheetLayout = (ConstraintLayout) registerBinding.schoolBottomSheetCoordinatorLayout.findViewById(R.id.schoolBottomSheetLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBinding = SchoolBottomSheetBinding.bind(bottomSheetLayout);

        schoolAdapter = new SchoolAdapter(this);
        bottomSheetBinding.schoolRecyclerView.setAdapter(schoolAdapter);

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
        schoolEditText = registerBinding.editTextSchool;


        // Custom Back Press handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    handleBottomSheet();
                    return;
                }
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // Calling function to set the known data got from firebase user to the edittext
        setKnownValue();

        // Setting on click listener on the views
        registerBinding.continueButton.setOnClickListener(this);
        schoolEditText.setOnClickListener(this);
        bottomSheetBinding.addSchoolButton.setOnClickListener(this);
        bottomSheetBinding.bottomSheetBackButton.setOnClickListener(this);

    }



    /**
     * -----------------------------------------------------------------
     * Function to handle OnClick event on views
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == registerBinding.continueButton.getId()) handleUserStore();
        else if (id == schoolEditText.getId()) handleBottomSheet();
        else if (id == bottomSheetBinding.addSchoolButton.getId())
            navController.navigate(R.id.action_registerFragment_to_addSchoolFragment);
        else if (id == bottomSheetBinding.bottomSheetBackButton.getId()) handleBottomSheet();
        else NotifyUtils.logDebug(TAG, "Unspecified view is clicked!!");

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
                    school.get_id(),
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
        String _school = schoolEditText.getText().toString().trim();

        if (TextUtils.isEmpty(_name) ||
                TextUtils.isEmpty(_email) ||
                TextUtils.isEmpty(_phone) ||
                TextUtils.isEmpty(_address) ||
                TextUtils.isEmpty(_school) ||
                gender_position == 0) {
            NotifyUtils.showToast(getContext(), "Please enter all the data");
            return false;
        }

        return true;

    }

    /**
     * -----------------------------------------------------------------------
     * Function to show or hide bottomsheet
     */
    private void handleBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            ViewUtils.showProgressBar(bottomSheetBinding.progressBarContainer);
            QueryHandler.getInstance(this).getSchools();
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
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
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Material> materials, List<Submission> submissions, List<Notice> notices) {
        if (registerBinding == null) return;
        if (schools != null) {
            this.schools = new ArrayList<>();
            this.schools = schools;
            schoolAdapter.submitList(this.schools);
            ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);
        }
    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Material material, Submission submission, Notice notice) {

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
        NotifyUtils.logError(TAG, "onQueryFailure()", e);
        ViewUtils.hideProgressBar(registerBinding.overlayLayout);
        ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);
        ViewUtils.enableViews(
                nameEditText,
                phoneEditText,
                emailEditText,
                addressEditText,
                registerBinding.spinnerGender,
                registerBinding.continueButton);
    }

    /**
     * --------------------------------------------------------------------------
     * Method implemented from SchoolViewCallbacks
     *
     * @param _position - clicked item position in a list
     */
    @Override
    public void onClick(int _position) {
        school = schools.get(_position);
        schoolEditText.setText(school.get_name());
        ViewUtils.handleBottomSheet(bottomSheetBehavior);
    }


    /**
     * Methods implemented from the AdapterView.OnItemSelectedListener
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

    @Override
    public void onResume() {
        super.onResume();
        // Handle school data
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            QueryHandler.getInstance(this).getSchools();
            ViewUtils.showProgressBar(bottomSheetBinding.progressBarContainer);
        }
    }
}