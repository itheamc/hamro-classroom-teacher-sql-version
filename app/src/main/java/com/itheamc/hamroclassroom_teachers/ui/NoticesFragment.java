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
import com.itheamc.hamroclassroom_teachers.adapters.NoticeAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.NoticeCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentNoticesBinding;
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
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import java.util.List;


public class NoticesFragment extends Fragment implements QueryCallbacks, NoticeCallbacks {
    private static final String TAG = "NoticesFragment";
    private FragmentNoticesBinding noticesBinding;
    private NavController navController;
    private MainViewModel viewModel;
    private NoticeAdapter noticeAdapter;
    private boolean isRefreshing = false;


    public NoticesFragment() {
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
        noticesBinding = FragmentNoticesBinding.inflate(inflater, container, false);
        return noticesBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and ViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing AssignmentAdapter and setting to recyclerview
        noticeAdapter = new NoticeAdapter(this);
        noticesBinding.noticesRecyclerView.setAdapter(noticeAdapter);


        // Setting swipe and refresh layout
        noticesBinding.noticesSwipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.setNotices(null);
            isRefreshing = true;
            ViewUtils.hideViews(noticesBinding.noNoticeLayout);
            checksUser();
        });

        // Handling back button
        noticesBinding.backButton.setOnClickListener(v -> {
            navController.popBackStack();
        });

        noticesBinding.addNoticeBtn.setOnClickListener(v -> {
            navController.navigate(R.id.action_noticesFragment_to_addNoticeFragment);
        });


        // Checks User for assignment extraction
        List<Notice> notices = viewModel.getNotices();
        if (notices == null) {
            checksUser();
        } else {
            handleNotices(notices);
        }


    }


    /**
     * Function to checks whether the user is already stored in viewmodel or not
     */
    private void checksUser() {
        User user = viewModel.getUser();
        if (user == null) {
            if (getActivity() != null) {
                QueryHandler.getInstance(this).getUser(LocalStorage.getInstance(getActivity()).getUserId());
                showProgress();
            }
            return;
        }

        retrieveNotices(user);
    }

    /**
     * Function to retrieve notices
     */
    private void retrieveNotices(@NonNull User user) {
        String userId = user.get_id();
        if ((userId == null || userId.isEmpty())) {
            hideProgress();
            return;
        }

        QueryHandler.getInstance(this).getNotices(userId, false);
        if (!isRefreshing) showProgress();
    }

    /**
     * Function to handle notices
     */
    private void handleNotices(List<Notice> notices) {
        if (notices.isEmpty()) {
            ViewUtils.visibleViews(noticesBinding.noNoticeLayout);
            return;
        }

        viewModel.setNotices(notices);
        noticeAdapter.submitList(notices);
    }


    /*
    Function to show progressbar
     */
    private void showProgress() {
        ViewUtils.showProgressBar(noticesBinding.noticesOverlayLayLayout);
    }

    /*
    Function to hide progressbar
     */
    private void hideProgress() {
        ViewUtils.hideProgressBar(noticesBinding.noticesOverlayLayLayout);
        ViewUtils.handleRefreshing(noticesBinding.noticesSwipeRefreshLayout);
        isRefreshing = false;
    }


    /**
     * Methods implemented from the QueryCallbacks
     */
    @Override
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Material> materials, List<Submission> submissions, List<Notice> notices) {
        if (noticesBinding == null) return;

        if (notices != null) {
            handleNotices(notices);
            hideProgress();
            return;
        }

        // If success but result is null
        hideProgress();
    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Material material, Submission submission, Notice notice) {
        if (noticesBinding == null) return;
        if (user != null) {
            viewModel.setUser(user);
            retrieveNotices(user);
        }
    }

    @Override
    public void onQuerySuccess(String message) {
        if (noticesBinding == null) return;
        if (message.equals("Not found")) ViewUtils.visibleViews(noticesBinding.noNoticeLayout);
        if (getContext() != null && !message.equals("Not found")) NotifyUtils.showToast(getContext(), message);
        hideProgress();
    }

    @Override
    public void onQueryFailure(Exception e) {
        if (noticesBinding == null) return;
        hideProgress();
        NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        NotifyUtils.logError(TAG, "onFailure()", e);
    }

    /**
     * Method overrided from the NoticeCallbacks
     *
     * @param _position - a position of the clicked item in the recycler view
     */
    @Override
    public void onClick(int _position) {
        List<Notice> notices = viewModel.getNotices();
        if (notices == null) return;

        Notice notice = notices.get(_position);
        viewModel.setNotice(notice);
        navController.navigate(R.id.action_noticesFragment_to_noticeFragment);
    }

    @Override
    public void onEditClick(int _position) {

    }

    @Override
    public void onDeleteClick(int _position) {

    }
}