package com.itheamc.hamroclassroom_teachers.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.SubmissionAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.SubmissionCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentSubmissionsBinding;
import com.itheamc.hamroclassroom_teachers.handlers.QueryHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Material;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SubmissionsFragment extends Fragment implements QueryCallbacks, SubmissionCallbacks {
    private static final String TAG = "SubmissionFragment";
    private FragmentSubmissionsBinding submissionsBinding;
    private NavController navController;
    private MainViewModel viewModel;
    private SubmissionAdapter submissionAdapter;

    public SubmissionsFragment() {
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
        submissionsBinding = FragmentSubmissionsBinding.inflate(inflater, container, false);
        return submissionsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and MainViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing SubmissionAdapter
        submissionAdapter = new SubmissionAdapter(this);
        submissionsBinding.submissionsRecyclerView.setAdapter(submissionAdapter);

        /*
        OnClickListener on back button
         */
        submissionsBinding.backButton.setOnClickListener(v -> {
            navController.popBackStack();
        });

         /*
        Setting OnRefreshListener on the swipe-refresh layout
         */
        submissionsBinding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.overlay);
        submissionsBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            ViewUtils.hideViews(submissionsBinding.noItemFoundLayout);
            getSubmissions();
        });

        // Get Submissions
        checkSubmissions();
    }

    /*
    Function to check submissions in the ViewModel
     */
    private void checkSubmissions() {
        Assignment assignment = viewModel.getAssignment();
        if (assignment == null) return;
        Map<String, List<Submission>> hashMap= viewModel.getSubmissionsHashMap();
        if (hashMap == null) {
            getSubmissions();
            return;
        }

        List<Submission> submissions = hashMap.get(assignment.get_id());
        if (submissions != null) {
            submitListToAdapter(submissions);
            return;
        }

        getSubmissions();
    }


    /*
    Functions to get Submissions from cloud
     */
    private void getSubmissions() {
        QueryHandler.getInstance(this).getSubmissions(viewModel.getAssignment().get_id());
        if (!submissionsBinding.swipeRefreshLayout.isRefreshing()) showProgress();
    }

    /*
    Function to submit List<Submission> to the SubmissionAdapter
     */
    private void submitListToAdapter(List<Submission> submissions) {
        if (submissions.size() == 0) {
            ViewUtils.visibleViews(submissionsBinding.noItemFoundLayout);
            return;
        }
        viewModel.setSubmissions(submissions);
        viewModel.setSubmissionsHashMap(submissions);
        submissionAdapter.submitList(submissions);
    }

    /*
    Function to show progress
     */
    private void showProgress() {
        ViewUtils.showProgressBar(submissionsBinding.progressBarContainer);
    }

    /*
    Function to hide progress
     */
    private void hideProgress() {
        ViewUtils.hideProgressBar(submissionsBinding.progressBarContainer);
        ViewUtils.handleRefreshing(submissionsBinding.swipeRefreshLayout);
    }


    /**
     * -------------------------------------------------------------------------
     * These are the methods implemented from the QueryCallbacks
     * -------------------------------------------------------------------------
     */
    @Override
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Material> materials, List<Submission> submissions, List<Notice> notices) {
        if (submissionsBinding == null) return;
        if (submissions != null) {
            submitListToAdapter(submissions);
        }

        hideProgress();
    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Material material, Submission submission, Notice notice) {

    }

    @Override
    public void onQuerySuccess(String message) {
        if (submissionsBinding == null) return;
        NotifyUtils.logDebug(TAG, message);
        if (getContext() != null && !message.equals("Not found")) NotifyUtils.showToast(getContext(), message);
        if (message.equals("Not found")) ViewUtils.visibleViews(submissionsBinding.noItemFoundLayout);
        hideProgress();
    }

    @Override
    public void onQueryFailure(Exception e) {
        if (submissionsBinding == null) return;
        if (getContext() != null) NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        NotifyUtils.logError(TAG, "onFailure: ", e);
        hideProgress();
    }


    /**
     * -------------------------------------------------------------------------
     * These are the methods implemented from the SubmissionViewCallbacks
     * -------------------------------------------------------------------------
     */
    @Override
    public void onClick(int _position) {
        if (viewModel.getSubmissions() != null && !viewModel.getSubmissions().isEmpty()) {
            Submission submission = viewModel.getSubmissions().get(_position);
            viewModel.setSubmission(submission);
            navController.navigate(R.id.action_submissionsFragment_to_submissionFragment);
        }
    }


}