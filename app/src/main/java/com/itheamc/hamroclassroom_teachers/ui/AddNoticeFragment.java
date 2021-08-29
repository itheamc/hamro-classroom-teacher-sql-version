package com.itheamc.hamroclassroom_teachers.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentAddNoticeBinding;
import com.itheamc.hamroclassroom_teachers.handlers.QueryHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Material;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.ArrayUtils;
import com.itheamc.hamroclassroom_teachers.utils.IdGenerator;
import com.itheamc.hamroclassroom_teachers.utils.LocalStorage;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.TimeUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import java.util.List;

public class AddNoticeFragment extends Fragment implements QueryCallbacks, View.OnClickListener {
    private static final String TAG = "AddNoticeFragment";
    private FragmentAddNoticeBinding addNoticeBinding;
    private NavController navController;
    private MainViewModel viewModel;

    /*
    TextInputLayouts
     */
    private TextInputLayout titleTextInputLayout;
    private TextInputLayout descTextInputLayout;
    private TextInputLayout classesTextInputLayout;

    /*
    EditTexts
     */
    private EditText titleEditText;
    private EditText descEditText;
    private EditText classesEditText;

    /*
    Strings
     */
    String title = null;
    String desc = null;
    String[] classes = null;

    public AddNoticeFragment() {
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
        addNoticeBinding = FragmentAddNoticeBinding.inflate(inflater, container, false);
        return addNoticeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and MainViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing TextInputLayouts and EditTexts
        titleTextInputLayout = addNoticeBinding.titleInputLayout;
        descTextInputLayout = addNoticeBinding.descInputLayout;
        classesTextInputLayout = addNoticeBinding.classesInputLayout;

        titleEditText = titleTextInputLayout.getEditText();
        descEditText = descTextInputLayout.getEditText();
        classesEditText = classesTextInputLayout.getEditText();

        addNoticeBinding.addNoticeButton.setOnClickListener(this);
        addNoticeBinding.backButton.setOnClickListener(this);


    }
    /*
    Handling click event on views
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == addNoticeBinding.backButton.getId()) {
            navController.popBackStack();
        } else if (id == addNoticeBinding.addNoticeButton.getId()) {
            addNotice();
        } else {
            NotifyUtils.logDebug(TAG, "Unspecified view is clicked!!");
        }
    }

    /*
      Function to add notice
       */
    private void addNotice() {
        if (!isInputsValid()) return;

        User user = viewModel.getUser();
        if (user == null) {
            if (getActivity() != null) {
                QueryHandler.getInstance(this).getUser(LocalStorage.getInstance(getActivity()).getUserId());
                showProgress();
            }
            return;
        }

        Log.d(TAG, "addNotice: " + user.toString());
        Notice notice = new Notice(
                IdGenerator.generateRandomId(),
                title,
                desc,
                user.get_schools_ref(),
                null,
                classes,
                user.get_id(),
                null,
                TimeUtils.now()
        );

        QueryHandler.getInstance(this).addNotice(notice);
        showProgress();
    }


    /*
    Function to check if inputs are valid
     */
    private boolean isInputsValid() {
        if (titleEditText != null) title = titleEditText.getText().toString().trim();
        if (descEditText != null) desc = descEditText.getText().toString().trim();
        if (classesEditText != null) {
            if (!classesEditText.getText().toString().trim().isEmpty()) classes = ArrayUtils.toArray(classesEditText.getText().toString().trim(), ", ");
        }

        if ((title == null || title.isEmpty()) || (desc == null || desc.isEmpty()) || (classes == null || classes.length == 0)) {
           if (getContext() != null) NotifyUtils.showToast(getContext(), "Please check if anything left!!");
            return false;
        }

        return true;
    }

    /*
    Function to show progress
     */
    private void showProgress() {
        ViewUtils.showProgressBar(addNoticeBinding.progressBarContainer);
        ViewUtils.disableViews(addNoticeBinding.addNoticeButton, titleTextInputLayout, descTextInputLayout);
    }

    /*
    Function to hide progress
     */
    private void hideProgress() {
        ViewUtils.hideProgressBar(addNoticeBinding.progressBarContainer);
        ViewUtils.enableViews(addNoticeBinding.addNoticeButton, titleTextInputLayout, descTextInputLayout);
    }

    /*
    Function to clear editTexts
     */
    private void clearEditTexts() {
        ViewUtils.clearEditTexts(titleEditText, descEditText, classesEditText);
    }

    /**
     * -----------------------------------------------------------------------------------------
     * Methods implemented from the QueryCallbacks
     */
    @Override
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Material> materials, List<Submission> submissions, List<Notice> notices) {

    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Material material, Submission submission, Notice notice) {
        if (addNoticeBinding == null) return;
        if (user != null) {
            viewModel.setUser(user);
            addNotice();
        }
    }

    @Override
    public void onQuerySuccess(String message) {
        if (addNoticeBinding == null) return;
        hideProgress();
        if (getContext() != null && message.equals("success")) {
            NotifyUtils.showToast(getContext(), "Added Successfully");
            clearEditTexts();
        }
        else NotifyUtils.showToast(getContext(), message);
    }

    @Override
    public void onQueryFailure(Exception e) {
        if (addNoticeBinding == null) return;
        hideProgress();
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Unable to add");
        NotifyUtils.logError(TAG, "onFailure()", e);
    }
}