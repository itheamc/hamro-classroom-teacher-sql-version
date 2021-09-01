package com.itheamc.hamroclassroom_teachers.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentAddSchoolBinding;
import com.itheamc.hamroclassroom_teachers.handlers.QueryHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Material;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.IdGenerator;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.TimeUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.LoginViewModel;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AddSchoolFragment extends Fragment implements QueryCallbacks, View.OnClickListener {
    private static final String TAG = "AddSchoolFragment";
    private FragmentAddSchoolBinding addSchoolBinding;
    private LoginViewModel viewModel;
    private NavController navController;

    /*
    TextInputLayouts
     */
    private TextInputLayout nameInputLayout;
    private TextInputLayout phoneInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout addressInputLayout;
    private TextInputLayout websiteInputLayout;
    private TextInputLayout dateInputLayout;

    /*
    EditTexts
     */
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText addressEditText;
    private EditText websiteEditText;
    private EditText dateEditText;

    /*
    Strings
     */
    private String name;
    private String phone;
    private String email;
    private String address;
    private String website;
    private String date;



    public AddSchoolFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        addSchoolBinding = FragmentAddSchoolBinding.inflate(inflater, container, false);
        return addSchoolBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and ViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        // Initializing TextInputLayouts and EditTexts
        nameInputLayout = addSchoolBinding.nameInputLayout;
        phoneInputLayout = addSchoolBinding.phoneInputLayout;
        emailInputLayout = addSchoolBinding.emailInputLayout;
        addressInputLayout = addSchoolBinding.addressInputLayout;
        websiteInputLayout = addSchoolBinding.websiteInputLayout;
        dateInputLayout = addSchoolBinding.dateInputLayout;

        nameEditText = nameInputLayout.getEditText();
        phoneEditText = phoneInputLayout.getEditText();
        emailEditText = emailInputLayout.getEditText();
        addressEditText = addressInputLayout.getEditText();
        websiteEditText = websiteInputLayout.getEditText();
        dateEditText = dateInputLayout.getEditText();



        // Adding OnClickListener
        addSchoolBinding.addSchoolButton.setOnClickListener(this);
        addSchoolBinding.backButton.setOnClickListener(this);

    }

    /**
     * ---------------------------------------------------------------------
     * Function to handle onClickEvent
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == addSchoolBinding.addSchoolButton.getId()) addSchool();
        else if (id == addSchoolBinding.backButton.getId()) navController.popBackStack();
        else NotifyUtils.logDebug(TAG, "Unspecified view is clicked!!");
    }

    /**
     * ------------------------------------------------------------------
     * Function to handle school addition
     */
    private void addSchool() {
        if (!isInputsValid()) {
            if (getContext() != null) NotifyUtils.showToast(getContext(), "Please inputs all the required fields!!");
            return;
        }

        String uid = viewModel.getFirebaseUser().getUid();

        School school = new School(
                IdGenerator.generateRandomId(),
                name,
                phone,
                email,
                address,
                website,
                "https://example.edu.np/icon.jpg",
                uid,
                null,
                date + "-04-13 00:00:00",
                TimeUtils.now()
        );

        QueryHandler.getInstance(this).addSchool(school);
        showProgress();
    }

    /*
    Function to checks if inputs are valid
     */
    private boolean isInputsValid() {
        if (nameEditText != null) name = nameEditText.getText().toString().trim();
        if (phoneEditText != null) phone = phoneEditText.getText().toString().trim();
        if (emailEditText != null) email = emailEditText.getText().toString().trim();
        if (addressEditText != null) address = addressEditText.getText().toString().trim();
        if (websiteEditText != null) website = websiteEditText.getText().toString().trim();
        if (dateEditText != null) date = dateEditText.getText().toString().trim();

        if (phone == null || phone.isEmpty()) phone = "000-000000";
        if (email == null || email.isEmpty()) {
            int n = new Random().nextInt(100);
            if (name != null && !name.isEmpty()) {
                email = name.split(" ")[0].toLowerCase(Locale.ENGLISH).trim() + n + "@example.com";
            }
            else email = "rand" + n + "@example.com";
        }
        if (website == null || website.isEmpty()) website = "https://example.edu.np";

        return (name != null && !name.isEmpty()) && (address != null && !address.isEmpty()) && (date != null && !date.isEmpty());
    }

    /*
    --------------------------------------------------------------------
    Function to show progressbar
     */
    private void showProgress() {
        ViewUtils.disableViews(addSchoolBinding.addSchoolButton, nameInputLayout, phoneInputLayout, emailInputLayout, addressInputLayout, websiteInputLayout, dateInputLayout);
        ViewUtils.showProgressBar(addSchoolBinding.progressBarContainer);
    }

    /*
   --------------------------------------------------------------------
   Function to hide progressbar
    */
    private void hideProgress() {
        ViewUtils.hideProgressBar(addSchoolBinding.progressBarContainer);
        ViewUtils.enableViews(addSchoolBinding.addSchoolButton, nameInputLayout, phoneInputLayout, emailInputLayout, addressInputLayout, websiteInputLayout, dateInputLayout);
    }

    /*
   Function to clear editTexts
    */
    private void clearEditTexts() {
        ViewUtils.clearEditTexts(nameEditText, phoneEditText, emailEditText, addressEditText, websiteEditText, dateEditText);
    }


    /**
     * -------------------------------------------------------------------------------------
     * Methods Implemented from QueryCallBacks
     */

    @Override
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Material> materials, List<Submission> submissions, List<Notice> notices) {

    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Material material, Submission submission, Notice notice) {

    }

    @Override
    public void onQuerySuccess(String message) {
        if (addSchoolBinding == null) return;
        hideProgress();
        if (getContext() != null && message.equals("success")) {
            NotifyUtils.showToast(getContext(), "Added Successfully");
            clearEditTexts();
        }
        else NotifyUtils.showToast(getContext(), message);
    }

    @Override
    public void onQueryFailure(Exception e) {
        if (addSchoolBinding == null) return;
        hideProgress();
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Unable to add");
        NotifyUtils.logError(TAG, "onFailure()", e);
    }


}