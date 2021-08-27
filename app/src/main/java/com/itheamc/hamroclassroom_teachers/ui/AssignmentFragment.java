package com.itheamc.hamroclassroom_teachers.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.itheamc.hamroclassroom_teachers.adapters.ImageAdapter;
import com.itheamc.hamroclassroom_teachers.callbacks.ImageCallbacks;
import com.itheamc.hamroclassroom_teachers.callbacks.StorageCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.FragmentAssignmentBinding;
import com.itheamc.hamroclassroom_teachers.handlers.StorageHandler;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.utils.IdGenerator;
import com.itheamc.hamroclassroom_teachers.utils.LocalStorage;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.TimeUtils;
import com.itheamc.hamroclassroom_teachers.utils.ViewUtils;
import com.itheamc.hamroclassroom_teachers.viewmodels.MainViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AssignmentFragment extends Fragment implements StorageCallbacks, ImageCallbacks {
    private static final String TAG = "AssignmentFragment";
    private FragmentAssignmentBinding assignmentBinding;
    private NavController navController;
    private ActivityResultLauncher<Intent> imagePickerResultLauncher;
    private List<Uri> imagesUri;
    private ImageAdapter imageAdapter;
    private MainViewModel viewModel;


    /*
   Array to store the uploaded image url
    */
    private String[] images;

    /*
    TextInputLayout
     */
    private TextInputLayout titleInputLayout;
    private TextInputLayout descInputLayout;

    /*
    EditTexts
     */
    private EditText titleEdittext;
    private EditText descEditText;

    /*
    Strings
     */
    private String _title = "";
    private String _desc = "";

    /*
    Boolean
     */
    private boolean is_uploading = false;   //To handle the image remove


    // Constructor
    public AssignmentFragment() {
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
        assignmentBinding = FragmentAssignmentBinding.inflate(inflater, container, false);
        return assignmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initializing NavController and MainViewModel
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initializing Image adapter and setting to the recycler view
        imageAdapter = new ImageAdapter(this);
        assignmentBinding.assignmentRecyclerView.setAdapter(imageAdapter);

        // Initializing InputLayout and Edittext
        titleInputLayout = assignmentBinding.titleInputLayout;
        descInputLayout = assignmentBinding.descInputLayout;

        titleEdittext = titleInputLayout.getEditText();
        descEditText = descInputLayout.getEditText();

        // Activity Result launcher to listen the result of the multi image picker
        imagePickerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (assignmentBinding == null) return;
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data == null) return;

                        ViewUtils.hideViews(assignmentBinding.imagePickerButton);     // To hide the image picker
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
        assignmentBinding.imagePickerButton.setOnClickListener(v -> showImagePicker());
        assignmentBinding.addAssignmentButton.setOnClickListener(v -> {
            if (!isInputsValid()) {
                if (getContext() != null) NotifyUtils.showToast(getContext(), "Please enter title");
                return;
            }

            ViewUtils.showProgressBar(assignmentBinding.progressBarContainer);
            ViewUtils.disableViews(assignmentBinding.addAssignmentButton, titleInputLayout, descInputLayout);
            storeOnDatabase();
            is_uploading = true;
        });


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
     * Function to handle image upload to cloud storage
     * It will be triggered continuously until all the images will be uploaded
     */
//    private void handleImageUpload() {
//        if (getActivity() == null || imagesUri == null || imagesUri.size() == 0) {
//            ViewUtils.hideProgressBar(assignmentBinding.progressBarContainer);
//            ViewUtils.enableViews(assignmentBinding.addAssignmentButton, titleInputLayout, descInputLayout);
//            is_uploading = false;
//            return;
//        }
//        StorageHandler.getInstance(getActivity(), this).uploadImage(imagesUri);
//        HandlerCompat.createAsync(Looper.getMainLooper()).post(() -> assignmentBinding.uploadedProgress.setText(R.string.uploading_images));
//    }

    /**
     * --------------------------------------------------------------------------
     * Function to handle Firestore upload
     * It will bi triggered only after all the images uploaded
     */
    private void storeOnDatabase() {
        if (getActivity() == null) return;
        Subject subject = viewModel.getSubject();
        String userId = LocalStorage.getInstance(getActivity()).getUserId();
        HandlerCompat.createAsync(Looper.getMainLooper()).post(() -> assignmentBinding.uploadedProgress.setText("Please Wait.."));

        // Creating new assignment object
        Assignment assignment = new Assignment(
                IdGenerator.generateRandomId(),
                _title,
                _desc,
                null,
                null,
                subject.get_class(),
                userId,
                null,
                subject.get_id(),
                null,
                subject.get_school_ref(),
                null,
                TimeUtils.now(),
                TimeUtils.later(2)
        );

        if (getActivity() != null) StorageHandler.getInstance(getActivity(), this)
                .uploadImage(imagesUri, assignment);
    }



    /**
     * -----------------------------------------------------------------------------
     * Function to verify inputs
     */
    private boolean isInputsValid() {
        if (titleEdittext != null) _title = titleEdittext.getText().toString().trim();
        if (descEditText != null) _desc = descEditText.getText().toString().trim();

        return !TextUtils.isEmpty(_title);
    }

    /**
     * ---------------------------------------------------------------------------
     * Function to make edittext clear
     */
    private void clearEdittext() {
        if (assignmentBinding == null) return;

        ViewUtils.clearEditTexts(titleEdittext, descEditText);
        if (imagesUri != null) {
            imagesUri.clear();
            imageAdapter.submitList(new ArrayList<>());
        }
        ViewUtils.visibleViews(assignmentBinding.imagePickerButton);    // To Show the image picker button
    }


    /**
     * ----------------------------------------------------------------------------------------
     * These are the methods implemented from the StorageCalbacks
     * @param message - success response message
     */

    @Override
    public void onSuccess(String message) {
        if (assignmentBinding == null) return;
        ViewUtils.hideProgressBar(assignmentBinding.progressBarContainer);
        ViewUtils.enableViews(assignmentBinding.addAssignmentButton, titleInputLayout, descInputLayout);
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Added Successfully");
        is_uploading = false;
        clearEdittext();
    }

    @Override
    public void onFailure(Exception e) {
        if (assignmentBinding == null) return;
        if (getContext() != null) NotifyUtils.showToast(getContext(), "Upload Failed");
        NotifyUtils.logError(TAG, "onFailure()", e);
        is_uploading = false;
        ViewUtils.hideProgressBar(assignmentBinding.progressBarContainer);
        ViewUtils.enableViews(assignmentBinding.addAssignmentButton, titleInputLayout, descInputLayout);

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
        if (imagesUri.size() == 0) ViewUtils.visibleViews(assignmentBinding.imagePickerButton);
    }


    /*
    Overrided function to view destroy
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        assignmentBinding = null;
    }
}