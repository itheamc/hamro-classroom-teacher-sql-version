package com.itheamc.hamroclassroom_teachers.handlers;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.FragmentActivity;

import com.itheamc.hamroclassroom_teachers.callbacks.StorageCallbacks;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.utils.ImageUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StorageHandler {
    private static final String TAG = "StorageHandler";
    private static StorageHandler instance;
    private final StorageCallbacks storageCallback;
    private final FragmentActivity activity;
    private final ExecutorService executorService;
    private final Handler handler;

    /**
     * ----------------------------------------------------------------------
     * ---------------------- Constructor for Cloud Storage------------------
     *
     * @param storageCallback -> instance of the StorageCallbacks
     */

    // Constructor
    private StorageHandler(@NonNull FragmentActivity activity, @NonNull StorageCallbacks storageCallback) {
        this.storageCallback = storageCallback;
        this.activity = activity;
        this.executorService = Executors.newFixedThreadPool(4);
        this.handler = HandlerCompat.createAsync(Looper.getMainLooper());
    }

    // Instance for Cloud Storage
    public static StorageHandler getInstance(@NonNull FragmentActivity activity, @NonNull StorageCallbacks storageCallback) {
        return new StorageHandler(activity, storageCallback);
    }


    /**
     * -------------------------------------------------------------------------------------------
     * -------------------------------------------------------------------------------------------
     * -------------------------------------------------------------------------------------------
     * ----------------------- THESE ARE THE FUNCTIONS FOR CLOUD STORAGE--------------------------
     * -------------------------------------------------------------------------------------------
     */

    /**
     * ---------------------------------------------
     * Function to upload image on the cloud storage
     */
    public void uploadImage(List<Uri> imagesUri, @NonNull Assignment assignment) {

        // Handling all the image processing and uploads in background
        executorService.execute(() -> {

            Request.Builder requestBuilder = new Request.Builder()
                    .url(PathHandler.ASSIGNMENTS_PATH);

            // If images are selected for upload
            if (imagesUri != null && imagesUri.size() > 0) {
                // Creating the instance of ImageUtils
                ImageUtils imageUtils = ImageUtils.getInstance(activity.getContentResolver());

                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);

                for (Uri uri : imagesUri) {
                    if (uri == null) continue;

                    String s = imageUtils.getFilePath(uri);
                    File file = new File(activity.getCacheDir(), s);
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file);
                        byte[] bytes = imageUtils.getByteArray(uri, 15);
                        outputStream.write(bytes);
                        outputStream.close();
                        Uri uris = Uri.fromFile(file);
                        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uris.toString());
                        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
                        String imageName = file.getName();

                        builder.addFormDataPart("file", imageName, RequestBody.create(file, MediaType.parse(mime)));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                // Outside for loop
                RequestBody requestBody = builder
                        .addFormDataPart("_id", assignment.get_id())
                        .addFormDataPart("_title", assignment.get_title())
                        .addFormDataPart("_desc", assignment.get_desc())
                        .addFormDataPart("_images", "")
                        .addFormDataPart("_docs", "")
                        .addFormDataPart("_class", assignment.get_class())
                        .addFormDataPart("_teacher", assignment.get_teacher_ref())
                        .addFormDataPart("_subject", assignment.get_subject_ref())
                        .addFormDataPart("_school", assignment.get_school_ref())
                        .addFormDataPart("_assigned_date", assignment.get_assigned_date())
                        .addFormDataPart("_due_date", assignment.get_due_date())
                        .build();

                requestBuilder
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .post(requestBody)
                        .build();
            } else {
                // If images are not selected for upload
                RequestBody rb = new FormBody.Builder()
                        .add("_id", assignment.get_id())
                        .add("_title", assignment.get_title())
                        .add("_desc", assignment.get_desc())
                        .add("_images", "")
                        .add("_docs", "")
                        .add("_class", assignment.get_class())
                        .add("_teacher", assignment.get_teacher_ref())
                        .add("_subject", assignment.get_subject_ref())
                        .add("_school", assignment.get_school_ref())
                        .add("_assigned_date", assignment.get_assigned_date())
                        .add("_due_date", assignment.get_due_date())
                        .build();

                requestBuilder
                        .post(rb)
                        .build();
            }

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .callTimeout(300, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();


            okHttpClient.newCall(requestBuilder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }
                        notifyFailure(new Exception("Unable to add"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * ------------------------------------------------------------------------------------
     * Function to notify the image upload status
     * - success
     * - failure
     */
    private void notifySuccess(String urls) {
        executorService.shutdown();
        handler.post(() -> {
            storageCallback.onSuccess(urls);
        });
    }

    private void notifyFailure(Exception e) {
        executorService.shutdown();
        handler.post(() -> {
            storageCallback.onFailure(e);
        });
    }
}
