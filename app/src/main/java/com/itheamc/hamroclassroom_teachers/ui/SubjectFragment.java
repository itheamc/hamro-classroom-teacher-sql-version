package com.itheamc.hamroclassroom_teachers.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.SchoolAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.SchoolCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentSubjectBinding;
import com.itheamc.hamroclassroom_teachers.databinding.SchoolBottomSheetBinding;
import com.itheamc.hamroclassroom_teachers.handlers.QueryHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Material;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.LocalStorage;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.IdGenerator;
import com.itheamc.hamroclassroom_teachers.utils.TimePickers;
import com.itheamc.hamroclassroom_teachers.utils.TimeUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class SubjectFragment extends Fragment implements QueryCallbacks, SchoolCallbacks, View.OnClickListener {
    private static final String TAG = "SubjectFragment";
    private FragmentSubjectBinding subjectBinding;
    private NavController navController;
    private MainViewModel viewModel;

    /*
    For Bottom Sheet -- Schools list
   */
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private SchoolBottomSheetBinding bottomSheetBinding;
    private SchoolAdapter schoolAdapter;

    /*
    TextInput Layouts
     */
    private TextInputLayout subjectInputLayout;
    private TextInputLayout classInputLayout;
    private TextInputLayout schoolInputLayout;
    private TextInputLayout classTimeInputLayout;

    /*
    EditTexts
     */
    EditText subEditText;
    EditText classEditText;
    EditText schoolEditText;
    EditText timeEditText;

    /*
    Buttons
     */
    private Button addEditBtn;

    /*
    Strings
     */
    private String _subject = null;
    private String _class = null;
    private String _school = null;
    private String _time = null;

    /*
    User and School
     */
    private User user = null;
    private School school;


    // Constructor
    public SubjectFragment() {
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
        subjectBinding = FragmentSubjectBinding.inflate(inflater, container, false);
        return subjectBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and MainViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        /*
        Initializing TextInput Layouts
         */
        subjectInputLayout = subjectBinding.subjectInputLayout;
        classInputLayout = subjectBinding.classInputLayout;
        schoolInputLayout = subjectBinding.schoolInputLayout;
        classTimeInputLayout = subjectBinding.timeInputLayout;

        /*
        Initializing EditTexts
         */
        subEditText = subjectInputLayout.getEditText();
        classEditText = classInputLayout.getEditText();
        schoolEditText = schoolInputLayout.getEditText();
        timeEditText = classTimeInputLayout.getEditText();

        /*
        Initializing Buttons
         */
        addEditBtn = subjectBinding.addEditButton;

        /*
        Updating edit texts if it is going to update
         */
        processingEditTexts();

         /*
        Initializing Bottom Sheet and its views
         */
        ConstraintLayout bottomSheetLayout = (ConstraintLayout) subjectBinding.bottomSheetCoordinatorLayout.findViewById(R.id.schoolBottomSheetLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBinding = SchoolBottomSheetBinding.bind(bottomSheetLayout);

        schoolAdapter = new SchoolAdapter(this);
        bottomSheetBinding.schoolRecyclerView.setAdapter(schoolAdapter);


        /*
        Setting onClickListener and OnFocusChangeListener on views
         */
        addEditBtn.setOnClickListener(this);
        subjectBinding.backButton.setOnClickListener(this);
        schoolInputLayout.setOnClickListener(this);
        classTimeInputLayout.setOnClickListener(this);
        schoolEditText.setOnClickListener(this);
        timeEditText.setOnClickListener(this);
        bottomSheetBinding.bottomSheetBackButton.setOnClickListener(this);


    }


    /**
     * --------------------------------------------------------------------------
     * Method implemented from View.OnClickListener
     *
     * @param v - it represents the view that is being clicked
     */
    @Override
    public void onClick(View v) {
        int _id = v.getId();
        if (_id == schoolInputLayout.getId() || v == schoolInputLayout.getEditText())
            handleBottomSheet();
        else if (_id == addEditBtn.getId()) {
            handleAddNow();
        } else if (_id == classTimeInputLayout.getId() || v == classTimeInputLayout.getEditText()) {
            DialogFragment newFragment = new TimePickers(subjectBinding);
            if (getActivity() != null)
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        }
        else if (_id == subjectBinding.backButton.getId()) navController.popBackStack();
        else if (_id == bottomSheetBinding.bottomSheetBackButton.getId()) handleBottomSheet();
        else NotifyUtils.logDebug(TAG, "Unspecified view is clicked!!");
    }


    /*
    ---------------------------------------
    Function to set the known value in the edit text to make changes
     */
    private void processingEditTexts() {
        if (!viewModel.isSubjectUpdating()) return;

        Subject subject = viewModel.getSubject();
        if (subject == null) return;
        if (subjectBinding.screenLabel != null) subjectBinding.screenLabel.setText("Update Subject");
        if (subEditText != null) subEditText.setText(subject.get_name());
        if (classEditText != null) classEditText.setText(subject.get_class());
        if (schoolEditText != null) schoolEditText.setText(subject.get_school().get_name());
        if (timeEditText != null) timeEditText.setText(subject.get_start_time());
        addEditBtn.setText(getString(R.string.update));
    }


    /*
    ---------------------------------------
    Function to handle the subject creation
     */
    private void handleAddNow() {
        boolean isValidated = isInputsValid();
        Log.d(TAG, "handleAddNow: " + String.valueOf(isValidated));
        if (!isValidated) {
            if (getContext() != null)
                NotifyUtils.showToast(getContext(), "Please fill all the details!!");
            return;
        }

        if (!viewModel.isSubjectUpdating()) {
            user = viewModel.getUser();

            if (user == null) {
                Log.d(TAG, "handleAddNow: User is null");
                if (getActivity() != null) {
                    QueryHandler.getInstance(this).getUser(LocalStorage.getInstance(getActivity()).getUserId());
                    ViewUtils.showProgressBar(subjectBinding.subjectProgressBarContainer);
                    ViewUtils.disableViews(subjectInputLayout, classInputLayout, schoolInputLayout, classTimeInputLayout, addEditBtn);
                }
                return;
            }

            addSubject();
            return;
        }

        updateSubject();

    }

    /*
    --------------------------------------
    Finally adding subject to cloud firestore
     */
    private void addSubject() {
        Subject subject = new Subject(
                IdGenerator.generateRandomId(),
                _subject,
                _class,
                user.get_id(),
                null,
                school.get_id(),
                null,
                "",
                _time,
                TimeUtils.now(),
                false,
                false
        );

        NotifyUtils.logDebug(TAG, subject.toString());
        ViewUtils.showProgressBar(subjectBinding.subjectProgressBarContainer);
        ViewUtils.disableViews(subjectInputLayout, classInputLayout, schoolInputLayout, classTimeInputLayout, addEditBtn);
        QueryHandler.getInstance(this).addSubject(subject);
    }


    /*
    --------------------------------------
    Finally updating subject to cloud firestore
     */
    private void updateSubject() {
        Subject subject = viewModel.getSubject();

        Subject updatedSub = new Subject();

        String _name = subEditText.getText().toString().trim();
        String _class = classEditText.getText().toString().trim();
        String _time = timeEditText.getText().toString().trim();

        updatedSub.set_id(subject.get_id());
        if (!TextUtils.isEmpty(_name) && !_name.equals(subject.get_name()))
            updatedSub.set_name(_name);
        if (!TextUtils.isEmpty(_class) && !_class.equals(subject.get_class()))
            updatedSub.set_class(_class);
        if (school != null) updatedSub.set_school_ref(school.get_id());
        if (!TextUtils.isEmpty(_time) && !_time.equals(subject.get_start_time()))
            updatedSub.set_start_time(_time);

        if (updatedSub.get_name() != null ||
                updatedSub.get_class() != null ||
                updatedSub.get_school_ref() != null ||
                updatedSub.get_start_time() != null) {

            ViewUtils.showProgressBar(subjectBinding.subjectProgressBarContainer);
            ViewUtils.disableViews(subjectInputLayout, classInputLayout, schoolInputLayout, classTimeInputLayout, addEditBtn);
            QueryHandler.getInstance(this).updateSubject(updatedSub);
            return;
        }
        if (getContext() != null)
            NotifyUtils.showToast(getContext(), "You have not make any changes");

    }

    /*
    --------------------------------------
    Function to verify inputs
     */
    private boolean isInputsValid() {
        // Setting the value to the string variables
        if (subEditText != null) _subject = subEditText.getText().toString().trim();
        if (classEditText != null) _class = classEditText.getText().toString().trim();
        if (schoolEditText != null) _school = schoolEditText.getText().toString().trim();
        if (timeEditText != null) _time = timeEditText.getText().toString().trim();

        return !TextUtils.isEmpty(_subject) &&
                !TextUtils.isEmpty(_class) &&
                !TextUtils.isEmpty(_school) &&
                !TextUtils.isEmpty(_time);

    }

    /**
     * -----------------------------------------------------------------------
     * Function to show or hide bottomsheet
     */
    private void handleBottomSheet() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            if (viewModel.getSchools() != null) {
                schoolAdapter.submitList(viewModel.getSchools());
                return;
            }
            if (bottomSheetBinding.progressBarContainer.getVisibility() == View.GONE)
                bottomSheetBinding.progressBarContainer.setVisibility(View.VISIBLE);
            QueryHandler.getInstance(this).getSchools();
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    /**
     * --------------------------------------------------------------------------
     * Method implemented from SchoolViewCallbacks
     *
     * @param _position - clicked item position in a list
     */
    @Override
    public void onClick(int _position) {
        this.school = viewModel.getSchools().get(_position);
        if (schoolEditText != null) schoolEditText.setText(this.school.get_name());
        handleBottomSheet();
    }


    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the QueryCallbacks
     * -------------------------------------------------------------------
     */
    @Override
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Material> materials, List<Submission> submissions, List<Notice> notices) {
        if (subjectBinding == null) return;

        if (schools != null) {
            schoolAdapter.submitList(schools);
            viewModel.setSchools(schools);
            ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);
        }
    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Material material, Submission submission, Notice notice) {
        if (subjectBinding == null) return;
        if (user != null) {
            this.user = user;
            viewModel.setUser(user);
            Log.d(TAG, "onQuerySuccess: " + user.toString());
            addSubject();
        }
    }

    @Override
    public void onQuerySuccess(String message) {
        if (subjectBinding == null) return;
        ViewUtils.hideProgressBar(subjectBinding.subjectProgressBarContainer);
        ViewUtils.clearEditTexts(subEditText, classEditText, schoolEditText, timeEditText);   // Calling function to clear the EditTexts after adding
        ViewUtils.enableViews(subjectInputLayout, classInputLayout, schoolInputLayout, classTimeInputLayout, addEditBtn);
        Log.d(TAG, "onQuerySuccess: " + message);
        if (!viewModel.isSubjectUpdating()) {
            NotifyUtils.showToast(getContext(), "Added Successfully");
        } else {
            NotifyUtils.showToast(getContext(), "Updated Successfully");
            viewModel.setSubjectUpdating(false);
            viewModel.setSubject(null);
            addEditBtn.setText(getString(R.string.add_now));
        }
    }

    @Override
    public void onQueryFailure(Exception e) {
        if (subjectBinding == null) return;
        ViewUtils.hideProgressBar(subjectBinding.subjectProgressBarContainer);
        ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);
        ViewUtils.enableViews(subjectInputLayout, classInputLayout, schoolInputLayout, classTimeInputLayout, addEditBtn);
        NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        NotifyUtils.logDebug(TAG, "onUserInfoRetrievedError: - " + e.getMessage());
    }


     /*
    Override method to handle view destroy
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subjectBinding = null;
    }


}