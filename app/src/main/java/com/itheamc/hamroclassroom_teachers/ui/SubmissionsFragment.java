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
import com.itheamc.hamroclassroom_teachers.callbacks.FirestoreCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.SubmissionCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentSubmissionsBinding;
import com.itheamc.hamroclassroom_teachers.handlers.FirestoreHandler;
import com.itheamc.hamroclassroom_teachers.handlers.QueryHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
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
        ViewUtils.showProgressBar(submissionsBinding.progressBarContainer);
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


    /**
     * -------------------------------------------------------------------------
     * These are the methods implemented from the FirestoreCallbacks
     * -------------------------------------------------------------------------
     */
    @Override
    public void onQuerySuccess(User user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Submission> submissions, List<Notice> notices) {
        if (submissionsBinding == null) return;
        if (submissions != null) {
            submitListToAdapter(submissions);
        }

        ViewUtils.hideProgressBar(submissionsBinding.progressBarContainer);
        ViewUtils.handleRefreshing(submissionsBinding.swipeRefreshLayout);

    }

    @Override
    public void onQuerySuccess(String message) {

    }

    @Override
    public void onQueryFailure(Exception e) {
        if (submissionsBinding == null) return;
        NotifyUtils.logDebug(TAG, "Failure");

        ViewUtils.hideProgressBar(submissionsBinding.progressBarContainer);
        ViewUtils.handleRefreshing(submissionsBinding.swipeRefreshLayout);
        if (getContext() != null) NotifyUtils.showToast(getContext(), e.getMessage());
        NotifyUtils.logError(TAG, "onFailure: ", e);
    }


    /**
     * -------------------------------------------------------------------------
     * These are the methods implemented from the SubmissionViewCallbacks
     * -------------------------------------------------------------------------
     */
    @Override
    public void onClick(int _position) {
        if (viewModel.getSubmissions() != null && viewModel.getSubmissions().size() > 0) {
            Submission submission = viewModel.getSubmissions().get(_position);
            viewModel.setSubmission(submission);
            navController.navigate(R.id.action_submissionsFragment_to_submissionFragment);
        }
    }


}