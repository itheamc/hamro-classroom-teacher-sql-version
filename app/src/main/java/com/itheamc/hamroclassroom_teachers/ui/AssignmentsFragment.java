package com.itheamc.hamroclassroom_teachers.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.material.textfield.TextInputLayout;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.AssignmentAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.AssignmentCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentAssignmentsBinding;
import com.itheamc.hamroclassroom_teachers.databinding.UpdateTitleBinding;
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
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class AssignmentsFragment extends Fragment implements AssignmentCallbacks, QueryCallbacks, View.OnClickListener {
    private static final String TAG = "AssignmentsFragment";
    private FragmentAssignmentsBinding assignmentsBinding;
    private NavController navController;
    private AssignmentAdapter assignmentAdapter;
    private MainViewModel viewModel;
    private Subject subject;

    /*
   For Bottom Sheet -- Schools list
    */
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private UpdateTitleBinding bottomSheetBinding;
    private TextInputLayout updatedTitleInputLayout;
    private EditText updateTitleEditText;
    private Button updateTitleButton;

    /*
    For Deleting
     */
    private boolean isDeleting = false;
    private int position = 0;


    public AssignmentsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        assignmentsBinding = FragmentAssignmentsBinding.inflate(inflater, container, false);
        return assignmentsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        Initializing NavController and MainViewModel
         */
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        /*
        Initializing Bottom Sheet and its views
         */
        ConstraintLayout bottomSheetLayout = (ConstraintLayout) assignmentsBinding.titleUpdateBottomSheetLayout.findViewById(R.id.updateTitleBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBinding = UpdateTitleBinding.bind(bottomSheetLayout);
        updatedTitleInputLayout = bottomSheetBinding.updateTitleInputLayout;
        updateTitleButton = bottomSheetBinding.updateTitleButton;
        updateTitleEditText = updatedTitleInputLayout.getEditText();

        updateTitleButton.setOnClickListener(v -> {
//            updateAssignment();
            if (getContext() != null) NotifyUtils.showToast(getContext(), "Will be added in coming update!!");
        });


        assignmentAdapter = new AssignmentAdapter(this);
        assignmentsBinding.assignmentsRecyclerView.setAdapter(assignmentAdapter);

        // Setting subject value from the ViewModel
        subject = viewModel.getSubject();

        /*
        Adding Onclick listener on views
         */
        assignmentsBinding.backButton.setOnClickListener(this);
        assignmentsBinding.addAssignmentButton.setOnClickListener(this);

        /*
        Controlling the visibility of the add assignment button
         */
        assignmentsBinding.addAssignmentButton.setVisibility(viewModel.isFromSubject() ? View.VISIBLE : View.GONE);

        /*
        OnBackPressedCallbacks
         */
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.setFromSubject(false);
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        /*
        Setting OnRefreshListener on the swipe-refresh layout
         */
        assignmentsBinding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.overlay);
        assignmentsBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            ViewUtils.hideViews(assignmentsBinding.noItemFoundLayout);
            getAssignments();
        });

        /*
        Getting assignments from cloud
         */
        checkAssignments();
    }

    /*
    handling OnClickEvent
     */
    @Override
    public void onClick(View view) {
        int _id = view.getId();
        if (_id == assignmentsBinding.backButton.getId()) {
            viewModel.setFromSubject(false);
            navController.popBackStack();
        } else if (_id == assignmentsBinding.addAssignmentButton.getId()) {
            navController.navigate(R.id.action_assignmentsFragment_to_addAssignmentFragment);
        } else {
            NotifyUtils.logDebug(TAG, "Unspecified view is clicked");
        }
    }


    /*
    Function to check submissions in the ViewModel
     */
    private void checkAssignments() {
        if (viewModel.isFromSubject()) {
            Map<String, List<Assignment>> hashMap = viewModel.getAssignmentsHashMap();
            if (hashMap == null) {
                getAssignments();
                return;
            }

            if (subject == null) return;
            List<Assignment> assignments = hashMap.get(subject.get_id());
            if (assignments != null) {
                submitListToAdapter(assignments);
                return;
            }

        } else {
            List<Assignment> listOfAssignments = viewModel.getListOfAllAssignments();
            if (listOfAssignments != null) {
                submitListToAdapter(listOfAssignments);
                return;
            }
        }

        getAssignments();

    }


    /*
    Function to get subjects
     */
    private void getAssignments() {
        if (getActivity() == null) return;
        if (viewModel.isFromSubject()) {
            if (subject == null) return;
            QueryHandler.getInstance(this).getAssignments(subject.get_id(), true);
            if (!assignmentsBinding.swipeRefreshLayout.isRefreshing()) showProgress();
            return;
        }

        QueryHandler.getInstance(this).getAssignments(LocalStorage.getInstance(getActivity()).getUserId(), false);
        if (!assignmentsBinding.swipeRefreshLayout.isRefreshing()) showProgress();
    }


    /*
    Function to submit List<Submission> to the SubmissionAdapter
     */
    private void submitListToAdapter(List<Assignment> assignments) {
        if (assignments.size() == 0) {
            ViewUtils.visibleViews(assignmentsBinding.noItemFoundLayout);
            return;
        }
        if (viewModel.isFromSubject()) viewModel.setAssignments(assignments);
        else viewModel.setListOfAllAssignments(assignments);
        assignmentAdapter.submitList(assignments);
    }

    /*
   Function to update assignment title
    */
    private void updateAssignment() {
        String assignId = null;
        String updatedTitle = updateTitleEditText.getText().toString();
        if (viewModel.getAssignment() != null) assignId = viewModel.getAssignment().get_id();

        if (assignId == null || TextUtils.isEmpty(updatedTitle)) {
            if (getContext() != null)
                NotifyUtils.showToast(getContext(), "Input field can't be empty.");
            return;
        }

        if (updatedTitle.equals(viewModel.getAssignment().get_title())) {
            if (getContext() != null)
                NotifyUtils.showToast(getContext(), "You haven't make any change on title");
            return;
        }

        QueryHandler.getInstance(this).updateAssignmentTitle(assignId, updatedTitle);
        ViewUtils.showProgressBar(bottomSheetBinding.progressBarContainer);
        ViewUtils.disableViews(updatedTitleInputLayout, updateTitleButton);

    }

    /*
    Function to show progress
     */
    private void showProgress() {
        ViewUtils.showProgressBar(assignmentsBinding.progressBarContainer);
    }

    /*
    Function to hide progress
     */
    private void hideProgress() {
        ViewUtils.hideProgressBar(assignmentsBinding.progressBarContainer);
        ViewUtils.handleRefreshing(assignmentsBinding.swipeRefreshLayout);
    }


    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the AssignmentViewCallbacks
     * -------------------------------------------------------------------
     */
    @Override
    public void onClick(int _position) {
        setAssignment(_position);
        navController.navigate(R.id.action_assignmentsFragment_to_assignmentFragment);
    }

    @Override
    public void onSubmissionsClick(int _position) {
        setAssignment(_position);
        navController.navigate(R.id.action_assignmentsFragment_to_submissionsFragment);
    }

    @Override
    public void onEditClick(int _position) {
        setAssignment(_position);
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Will be added in coming update!!");
//        ViewUtils.handleBottomSheet(bottomSheetBehavior);
//        if (updateTitleEditText != null && viewModel.getAssignment() != null)
//            updateTitleEditText.setText(viewModel.getAssignment().get_title());
    }

    @Override
    public void onDeleteClick(int _position) {
        Assignment assignment = null;
        if (viewModel.isFromSubject()) {
            if (viewModel.getAssignments() != null)
                assignment = viewModel.getAssignments().get(_position);
        } else {
            if (viewModel.getListOfAllAssignments() != null)
                assignment = viewModel.getListOfAllAssignments().get(_position);
        }

        if (assignment != null) {
            isDeleting = true;
            position = _position;
            QueryHandler.getInstance(this).deleteAssignment(assignment.get_id());
            showProgress();
        }
    }

    // Custom function to set assignment in ViewModel
    private void setAssignment(int _position) {
        Assignment assignment = null;

        if (viewModel.isFromSubject()) {
            if (viewModel.getAssignments() != null && !viewModel.getAssignments().isEmpty())
                assignment = viewModel.getAssignments().get(_position);
        } else {
            if (viewModel.getListOfAllAssignments() != null && !viewModel.getListOfAllAssignments().isEmpty())
                assignment = viewModel.getListOfAllAssignments().get(_position);
        }

        if (assignment != null) viewModel.setAssignment(assignment);
    }

    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the FirestoreCallbacks
     * -------------------------------------------------------------------
     */
    @Override
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Material> materials, List<Submission> submissions, List<Notice> notices) {
        if (assignmentsBinding == null) return;

        if (assignments != null) {
            if (assignments.size() == 0) {
                ViewUtils.visibleViews(assignmentsBinding.noItemFoundLayout);
                return;
            }
            if (viewModel.isFromSubject()) viewModel.setAssignmentsHashMap(assignments);
            else viewModel.setListOfAllAssignments(assignments);
            checkAssignments();
        }

        hideProgress();
    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Material material, Submission submission, Notice notice) {

    }

    @Override
    public void onQuerySuccess(String message) {
        if (assignmentsBinding == null) return;

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            if (viewModel.getAssignment() != null)
                viewModel.getAssignment().set_title(updateTitleEditText.getText().toString());
            ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);
            ViewUtils.enableViews(updatedTitleInputLayout, updateTitleButton);
            if (getContext() != null) NotifyUtils.showToast(getContext(), "Updated Successfully");
            ViewUtils.handleBottomSheet(bottomSheetBehavior);
            getAssignments();
            return;
        }

        if (isDeleting) {
            isDeleting = false;
            viewModel.removeAssignment(position);
            assignmentAdapter.notifyItemRemoved(position);
        }

        hideProgress();
        if (message.equals("Not found")) ViewUtils.visibleViews(assignmentsBinding.noItemFoundLayout);
        if (getContext() != null && !message.equals("Not found")) NotifyUtils.showToast(getContext(), message);

    }

    @Override
    public void onQueryFailure(Exception e) {
        if (assignmentsBinding == null) return;
        hideProgress();

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);
            ViewUtils.enableViews(updatedTitleInputLayout, updateTitleButton);
        }
        if (getContext() != null)
            NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        NotifyUtils.logError(TAG, "onFailure: ", e);
    }


    /*
    Overrided method to handle view destroy
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        viewModel.setFromSubject(false);
        assignmentsBinding = null;
    }

}