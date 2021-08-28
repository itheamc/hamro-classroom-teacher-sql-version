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
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentHomeBinding;
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
import com.itheamc.hamroclassroom_teachers.utils.OtherUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HomeFragment extends Fragment implements QueryCallbacks, View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding homeBinding;
    private NavController navController;
    private MainViewModel viewModel;


    // Constructor
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return homeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and ViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Setting greeting label text
        homeBinding.greetingLabel.setText(OtherUtils.showGreeting());


        // Setting OnClickListener on Views
        homeBinding.userIconCardView.setOnClickListener(this);
        homeBinding.classesCardView.setOnClickListener(this);
        homeBinding.assignmentCardView.setOnClickListener(this);
        homeBinding.extraMaterialsCardView.setOnClickListener(this);
        homeBinding.noticesCardView.setOnClickListener(this);

        // OnRefresh Listener on Swipe Refresh Layout
        homeBinding.swipeRefreshLayout.setOnRefreshListener(this::retrieveUser);

        // Retrieving user
        if (viewModel.getUser() != null) {
            setUserData(viewModel.getUser());
        } else {
            retrieveUser();
        }
    }

    /**
     * Function to retrieve user
     */
    private void retrieveUser() {
        String userId = null;
        if (getActivity() != null) userId = LocalStorage.getInstance(getActivity()).getUserId();
        if (userId != null) QueryHandler.getInstance(this).getUser(userId);
    }

    /**
     * Function to pass data on the views
     */
    private void setUserData(User user) {
        Picasso.get().load(user.get_image())
                .into(homeBinding.userIcon);
        homeBinding.userName.setText(user.get_name().split(" ")[0]);
    }


    /**
     * -----------------------------------------------------------------------------------
     * This is the method overrided from the View.OnClickListener to listen the click event
     * @param v - It is the instance of the view got clicked
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == homeBinding.userIconCardView.getId()) {
            navigateTo(R.id.action_homeFragment_to_profileFragment);
        } else if (id == homeBinding.classesCardView.getId()) {
            navigateTo(R.id.action_homeFragment_to_classesFragment);
        } else if (id == homeBinding.assignmentCardView.getId()) {
            navigateTo(R.id.action_homeFragment_to_assignmentsFragment);
        } else if (id == homeBinding.extraMaterialsCardView.getId()) {
            navigateTo(R.id.action_homeFragment_to_materialsFragment);
        } else if (id == homeBinding.noticesCardView.getId()) {
            navigateTo(R.id.action_homeFragment_to_noticesFragment);
        } else {
            NotifyUtils.logDebug(TAG, "Unspecified view is clicked");
        }
    }

    /*
    Function to handle navigation
     */
    private void navigateTo(int actionId) {
        navController.navigate(actionId);
    }


    /**
     * -------------------------------------------------------------------------
     * These are the methods overrided from the QueryCallbacks
     */
    @Override
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Material> materials, List<Submission> submissions, List<Notice> notices) {

    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Material material, Submission submission, Notice notice) {
        if (homeBinding == null) return;
        // If User retrieved from the Firestore
        if (user != null) {
            viewModel.setUser(user);
            viewModel.setSchool(user.get_schools());
            setUserData(user);
        }
        ViewUtils.handleRefreshing(homeBinding.swipeRefreshLayout);
    }

    @Override
    public void onQuerySuccess(String message) {
        if (homeBinding == null) return;
        ViewUtils.handleRefreshing(homeBinding.swipeRefreshLayout);
    }

    @Override
    public void onQueryFailure(Exception e) {
        if (homeBinding == null) return;
        if (getContext() != null) NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        ViewUtils.handleRefreshing(homeBinding.swipeRefreshLayout);

    }
}