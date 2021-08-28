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

import com.itheamc.hamroclassroom_teachers.adapters.SliderAdapter;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentAssignmentBinding;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.utils.ArrayUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

public class AssignmentFragment extends Fragment {
    private static final String TAG = "AssignmentFragment";
    private FragmentAssignmentBinding assignmentBinding;

    public AssignmentFragment() {
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
        assignmentBinding = FragmentAssignmentBinding.inflate(inflater, container, false);
        return assignmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and ViewModel
        NavController navController = Navigation.findNavController(view);
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);


        // Initializing slider adapter...
        SliderAdapter sliderAdapter = new SliderAdapter();
        assignmentBinding.assignmentViewPager.setAdapter(sliderAdapter);

        Assignment assignment = viewModel.getAssignment();
        if (assignment != null) {
            assignmentBinding.setAssignment(assignment);
            if (assignment.get_images().length > 0) sliderAdapter.submitList(ArrayUtils.asList(assignment.get_images()));
        }

    }

    // View Destroy
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        assignmentBinding = null;
    }
}