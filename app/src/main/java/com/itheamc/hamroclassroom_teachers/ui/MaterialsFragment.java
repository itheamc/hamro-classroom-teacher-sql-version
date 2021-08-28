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

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.MaterialAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.MaterialCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentMaterialsBinding;
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


public class MaterialsFragment extends Fragment implements MaterialCallbacks, QueryCallbacks, View.OnClickListener {
    private static final String TAG = "MaterialsFragment";
    private FragmentMaterialsBinding materialsBinding;
    private NavController navController;
    private MaterialAdapter materialAdapter;
    private MainViewModel viewModel;
    private Subject subject;

    /*
    For Deleting
     */
    private boolean isDeleting = false;
    private int position = 0;


    public MaterialsFragment() {
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
        materialsBinding = FragmentMaterialsBinding.inflate(inflater, container, false);
        return materialsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        Initializing NavController and MainViewModel
         */
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);


        materialAdapter = new MaterialAdapter(this);
        materialsBinding.materialsRecyclerView.setAdapter(materialAdapter);

        // Setting subject value from the ViewModel
        subject = viewModel.getSubject();

        /*
        Adding Onclick listener on views
         */
        materialsBinding.backButton.setOnClickListener(this);
        materialsBinding.addMaterialButton.setOnClickListener(this);

        /*
        Setting OnRefreshListener on the swipe-refresh layout
         */
        materialsBinding.swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.overlay);
        materialsBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            ViewUtils.hideViews(materialsBinding.noItemFoundLayout);
            getMaterials();
        });

        /*
        Getting assignments from cloud
         */
        checkMaterials();
    }

    /*
    handling OnClickEvent
     */
    @Override
    public void onClick(View view) {
        int _id = view.getId();
        if (_id == materialsBinding.backButton.getId()) {
            navController.popBackStack();
        } else if (_id == materialsBinding.addMaterialButton.getId()) {
            navController.navigate(R.id.action_materialsFragment_to_addMaterialFragment);
        } else {
            NotifyUtils.logDebug(TAG, "Unspecified view is clicked");
        }
    }


    /*
    Function to check submissions in the ViewModel
     */
    private void checkMaterials() {
        List<Material> materials = viewModel.getMaterials();
        if (materials != null) {
            submitListToAdapter(materials);
            return;
        }

        getMaterials();
    }


    /*
    Function to get subjects
     */
    private void getMaterials() {
        if (getActivity() == null) return;
        QueryHandler.getInstance(this).getMaterials(LocalStorage.getInstance(getActivity()).getUserId(), false);
        if (!materialsBinding.swipeRefreshLayout.isRefreshing()) showProgress();
    }


    /*
    Function to submit List<Submission> to the SubmissionAdapter
     */
    private void submitListToAdapter(List<Material> materials) {
        if (materials.size() == 0) {
            ViewUtils.visibleViews(materialsBinding.noItemFoundLayout);
            return;
        }
        viewModel.setMaterials(materials);
        materialAdapter.submitList(materials);
    }

    /*
    Function to show progress
     */
    private void showProgress() {
        ViewUtils.showProgressBar(materialsBinding.progressBarContainer);
    }

    /*
    Function to hide progress
     */
    private void hideProgress() {
        ViewUtils.hideProgressBar(materialsBinding.progressBarContainer);
        ViewUtils.handleRefreshing(materialsBinding.swipeRefreshLayout);
    }


    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the AssignmentViewCallbacks
     * -------------------------------------------------------------------
     */
    @Override
    public void onClick(int _position) {
        setMaterial(_position);
        navController.navigate(R.id.action_materialsFragment_to_materialFragment);
    }

    @Override
    public void onEditClick(int _position) {
        setMaterial(_position);
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Will be added in coming update!!");
    }

    @Override
    public void onDeleteClick(int _position) {
        Material material = null;
        if (viewModel.getMaterials() != null)
            material = viewModel.getMaterials().get(_position);

        if (material != null) {
            isDeleting = true;
            position = _position;
            QueryHandler.getInstance(this).deleteMaterial(material.get_id());
            showProgress();
        }
    }

    // Custom function to set assignment in ViewModel
    private void setMaterial(int _position) {
        Material material = null;
        if (viewModel.getMaterials() != null && !viewModel.getMaterials().isEmpty())
            material = viewModel.getMaterials().get(_position);

        if (material != null) viewModel.setMaterial(material);
    }

    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the FirestoreCallbacks
     * -------------------------------------------------------------------
     */
    @Override
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Material> materials, List<Submission> submissions, List<Notice> notices) {
        if (materialsBinding == null) return;

        if (materials != null) {
            if (materials.size() == 0) {
                ViewUtils.visibleViews(materialsBinding.noItemFoundLayout);
                return;
            }

            viewModel.setMaterials(materials);
            checkMaterials();
        }

        hideProgress();
    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Material material, Submission submission, Notice notice) {

    }

    @Override
    public void onQuerySuccess(String message) {
        if (materialsBinding == null) return;

        if (isDeleting) {
            isDeleting = false;
            viewModel.removeMaterial(position);
            materialAdapter.notifyItemRemoved(position);
        }

        hideProgress();
        if (message.equals("Not found")) ViewUtils.visibleViews(materialsBinding.noItemFoundLayout);
        if (getContext() != null && !message.equals("Not found")) NotifyUtils.showToast(getContext(), message);

    }

    @Override
    public void onQueryFailure(Exception e) {
        if (materialsBinding == null) return;
        hideProgress();
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
        materialsBinding = null;
    }

}