package com.itheamc.hamroclassroom_teachers.ui;

import static com.itheamc.hamroclassroom_teachers.utils.Constants.SUBJECT_VIEW;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;
import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.adapters.ImageAdapter;
import com.itheamc.hamroclassroom_teachers.adapters.SchoolAdapter;
import com.itheamc.hamroclassroom_teachers.adapters.SubjectAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.ImageCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.StorageCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.SubjectCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentAddMaterialBinding;
import com.itheamc.hamroclassroom_teachers.databinding.SchoolBottomSheetBinding;
import com.itheamc.hamroclassroom_teachers.databinding.SubjectBottomSheetBinding;
import com.itheamc.hamroclassroom_teachers.handlers.QueryHandler;
import com.itheamc.hamroclassroom_teachers.handlers.StorageHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Material;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.IdGenerator;
import com.itheamc.hamroclassroom_teachers.utils.LocalStorage;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.TimeUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;


public class AddMaterialFragment extends Fragment implements QueryCallbacks, StorageCallbacks, ImageCallbacks, SubjectCallbacks, View.OnClickListener {
    private static final String TAG = "AddMaterialFragment";
    private FragmentAddMaterialBinding addMaterialBinding;
    private NavController navController;
    private ActivityResultLauncher<Intent> imagePickerResultLauncher;
    private List<Uri> imagesUri;
    private ImageAdapter imageAdapter;
    private MainViewModel viewModel;

    /*
    For Bottom Sheet -- Schools list
      */
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;
    private SubjectBottomSheetBinding bottomSheetBinding;
    private SubjectAdapter subjectAdapter;

    /*
    Array to store the uploaded image url
      */
    private String[] images;

    /*
    TextInputLayout
     */
    private TextInputLayout titleInputLayout;
    private TextInputLayout subjectInputLayout;

    /*
    EditTexts
     */
    private EditText titleEdittext;
    private EditText subjectEditText;

    /*
    Strings
     */
    private String _title = "";

    /*
    Subject Object
     */
    private Subject _subject;

    /*
    Boolean
     */
    private boolean is_uploading = false;   //To handle the image remove


    public AddMaterialFragment() {
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
        addMaterialBinding = FragmentAddMaterialBinding.inflate(inflater, container, false);
        return addMaterialBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and MainViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing Image adapter and setting to the recycler view
        imageAdapter = new ImageAdapter(this);
        addMaterialBinding.materialRecyclerView.setAdapter(imageAdapter);

        // Initializing InputLayout and Edittext
        titleInputLayout = addMaterialBinding.titleInputLayout;
        subjectInputLayout = addMaterialBinding.subjectInputLayout;
        titleEdittext = titleInputLayout.getEditText();
        subjectEditText = subjectInputLayout.getEditText();

        /*
        Initializing Bottom Sheet and its views
         */
        ConstraintLayout bottomSheetLayout = (ConstraintLayout) addMaterialBinding.bottomSheetCoordinatorLayout.findViewById(R.id.subjectBottomSheetLayout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBinding = SubjectBottomSheetBinding.bind(bottomSheetLayout);

        subjectAdapter = new SubjectAdapter(this, SUBJECT_VIEW);
        bottomSheetBinding.subjectRecyclerView.setAdapter(subjectAdapter);

        // Custom Back Press handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    handleBottomSheet();
                    return;
                }
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        // Activity Result launcher to listen the result of the multi image picker
        imagePickerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (addMaterialBinding == null) return;
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data == null) return;

                        ViewUtils.hideViews(addMaterialBinding.imagePickerButton);     // To hide the image picker
                        imagesUri = new ArrayList<>();

                        // Get the Images from data
                        ClipData mClipData = data.getClipData();
                        if (mClipData != null) {
                            int count = mClipData.getItemCount();
                            for (int i = 0; i < count; i++) {
                                // adding imageUri in array
                                Uri imageUri = mClipData.getItemAt(i).getUri();
                                imagesUri.add(imageUri);
                            }

                            submitImagesToImageAdapter();   // Submitting image to adapter
                            return;
                        }

                        Uri imageUri = data.getData();
                        imagesUri.add(imageUri);
                        submitImagesToImageAdapter();   // Submitting image to adapter

                    } else {
                        NotifyUtils.logDebug(TAG, "Image Picker Closed!!");
                    }
                });

        /*
        Setting OnClickListener
         */
        addMaterialBinding.backButton.setOnClickListener(this);
        addMaterialBinding.imagePickerButton.setOnClickListener(this);
        addMaterialBinding.addMaterialButton.setOnClickListener(this);
        subjectInputLayout.setOnClickListener(this);
        subjectEditText.setOnClickListener(this);
        bottomSheetBinding.bottomSheetBackButton.setOnClickListener(this);
    }

    /**
     * ----------------------------------------------------------------------------
     * Function to handle click events on views
     */
    @Override
    public void onClick(View view) {
        int _id = view.getId();
        if (_id == addMaterialBinding.backButton.getId()) navController.popBackStack();
        else if (_id == addMaterialBinding.imagePickerButton.getId()) showImagePicker();
        else if (_id == addMaterialBinding.addMaterialButton.getId()) {
            if (!isInputsValid()) {
                if (getContext() != null) NotifyUtils.showToast(getContext(), "Please enter title");
                return;
            }

            storeOnDatabase();
        }
        else if (_id == subjectInputLayout.getId() || _id == subjectEditText.getId()) handleBottomSheet();
        else if (_id == bottomSheetBinding.bottomSheetBackButton.getId()) handleBottomSheet();
        else NotifyUtils.logDebug(TAG, "Unspecified view is clicked!!");
    }

    /**
     * -----------------------------------------------------------------------------
     * Function to start the image picker intent
     */
    private void showImagePicker() {
        // initialising intent
        Intent intent = new Intent();

        // setting type to select to be image
        intent.setType("image/*");

        // allowing multiple image to be selected
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerResultLauncher.launch(Intent.createChooser(intent, "Select Images"));
    }

    /*
    Function to submit data to image adapter
     */
    private void submitImagesToImageAdapter() {
        if (imagesUri != null && imagesUri.size() > 0) imageAdapter.submitList(imagesUri);
    }

    /**
     * --------------------------------------------------------------------------
     * Function to handle Firestore upload
     * It will bi triggered only after all the images uploaded
     */
    private void storeOnDatabase() {
        if (getActivity() == null) return;
        String userId = LocalStorage.getInstance(getActivity()).getUserId();
        HandlerCompat.createAsync(Looper.getMainLooper()).post(() -> addMaterialBinding.progressLabel.setText("Please Wait.."));

        // Creating new assignment object
        Material material = new Material(
                IdGenerator.generateRandomId(),
                _title,
                null,
                null,
                _subject.get_class(),
                userId,
                null,
                _subject.get_id(),
                null,
                _subject.get_school_ref(),
                null,
                TimeUtils.now()
        );

        if (getActivity() != null) {
            is_uploading = true;
            StorageHandler.getInstance(getActivity(), this)
                    .addMaterial(imagesUri, material);
            ViewUtils.showProgressBar(addMaterialBinding.addMaterialProgressBarContainer);
            ViewUtils.disableViews(addMaterialBinding.addMaterialButton, titleInputLayout, subjectInputLayout);
        }
    }


    /**
     * -----------------------------------------------------------------------------
     * Function to verify inputs
     */
    private boolean isInputsValid() {
        if (titleEdittext != null) _title = titleEdittext.getText().toString().trim();
        return !TextUtils.isEmpty(_title) && _subject != null;
    }

    /**
     * ---------------------------------------------------------------------------
     * Function to make edittext clear
     */
    private void clearEdittext() {
        if (addMaterialBinding == null) return;

        ViewUtils.clearEditTexts(titleEdittext, subjectEditText);
        if (imagesUri != null) {
            imagesUri.clear();
            imageAdapter.submitList(new ArrayList<>());
        }
        ViewUtils.visibleViews(addMaterialBinding.imagePickerButton);    // To Show the image picker button
    }


    /**
     * -----------------------------------------------------------------------
     * Function to show or hide bottomsheet
     */
    private void handleBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            if (viewModel.getSubjects() != null) {
                subjectAdapter.submitList(viewModel.getSubjects());
                return;
            }
            if (getActivity() != null) {
                ViewUtils.showProgressBar(bottomSheetBinding.progressBarContainer);
                QueryHandler.getInstance(this).getSubjects(LocalStorage.getInstance(getActivity()).getUserId());
            }
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    /**
     * -------------------------------------------------------------------
     * These are the methods implemented from the QueryCallbacks
     * -------------------------------------------------------------------
     */
    @Override
    public void onQuerySuccess(List<User> user, List<School> schools, List<Student> students, List<Subject> subjects, List<Assignment> assignments, List<Material> materials, List<Submission> submissions, List<Notice> notices) {
        if (addMaterialBinding == null) return;

        if (subjects != null) {
            subjectAdapter.submitList(subjects);
            viewModel.setSubjects(subjects);
            ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);
        }
    }

    @Override
    public void onQuerySuccess(User user, School school, Student student, Subject subject, Assignment assignment, Material material, Submission submission, Notice notice) {

    }

    @Override
    public void onQuerySuccess(String message) {
        if (addMaterialBinding == null) return;
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);

    }

    @Override
    public void onQueryFailure(Exception e) {
        if (addMaterialBinding == null) return;
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            ViewUtils.hideProgressBar(bottomSheetBinding.progressBarContainer);
        if (getContext() != null) NotifyUtils.showToast(getContext(), getString(R.string.went_wrong_message));
        NotifyUtils.logError(TAG, "onFailure()", e);
    }


    /**
     * ----------------------------------------------------------------------------------------
     * These are the methods implemented from the StorageCalbacks
     * @param message - success response message
     */

    @Override
    public void onSuccess(String message) {
        if (addMaterialBinding == null) return;
        ViewUtils.hideProgressBar(addMaterialBinding.addMaterialProgressBarContainer);
        ViewUtils.enableViews(addMaterialBinding.addMaterialButton, titleInputLayout, subjectInputLayout);
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Added Successfully");
        is_uploading = false;
        clearEdittext();
    }

    @Override
    public void onFailure(Exception e) {
        if (addMaterialBinding == null) return;
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Upload Failed");
        NotifyUtils.logError(TAG, "onFailure()", e);
        is_uploading = false;
        ViewUtils.hideProgressBar(addMaterialBinding.addMaterialProgressBarContainer);
        ViewUtils.enableViews(addMaterialBinding.addMaterialButton, titleInputLayout, subjectInputLayout);

    }

    /**
     * -------------------------------------------------------------------
     * This method is implemented from the ImageViewCallback
     * -------------------------------------------------------------------
     */

    @Override
    public void onRemove(int _position) {
        if (is_uploading) return;
        imagesUri.remove(_position);
        imageAdapter.notifyItemRemoved(_position);
        NotifyUtils.logDebug(TAG, imagesUri.toString());
        if (imagesUri.size() == 0) ViewUtils.visibleViews(addMaterialBinding.imagePickerButton);
    }

    /**
     * --------------------------------------------------------------
     * These are the methods overrided from the SubjectCallbacks
     */

    @Override
    public void onClick(int _position) {
        this._subject = viewModel.getSubjects().get(_position);
        if (subjectEditText != null) subjectEditText.setText(this._subject.get_name());
        handleBottomSheet();
    }

    @Override
    public void onAddLinkClick(int _position) {

    }

    @Override
    public void onAssignmentsClick(int _position) {

    }

    @Override
    public void onEditClick(int _position) {

    }

    @Override
    public void onDeleteClick(int _position) {

    }


    /*
    Overrided function to view destroy
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addMaterialBinding = null;
    }


}