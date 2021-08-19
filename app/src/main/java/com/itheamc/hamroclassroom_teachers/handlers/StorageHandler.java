package com.itheamc.hamroclassroom_teachers.handlers;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itheamc.hamroclassroom_teachers.callbacks.StorageCallbacks;
import com.itheamc.hamroclassroom_teachers.utils.ImageUtils;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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
    private Handler handler;

    /**
     * ----------------------------------------------------------------------
     * ---------------------- Constructor for Cloud Storage------------------
     *
     * @param storageCallback -> instance of the StorageCallbacks
     */

    // Constructor
    private StorageHandler(@NonNull StorageCallbacks storageCallback, @NonNull FragmentActivity activity) {
        this.storageCallback = storageCallback;
        this.activity = activity;
        this.executorService = Executors.newFixedThreadPool(4);
        this.handler = HandlerCompat.createAsync(Looper.getMainLooper());
    }

    // Instance for Cloud Storage
    public static StorageHandler getInstance(@NonNull StorageCallbacks storageCallback, @NonNull FragmentActivity activity) {
        if (instance == null) {
            instance = new StorageHandler(storageCallback, activity);
        }
        return instance;
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
    public void uploadImage(@NonNull @NotNull List<Uri> imagesUri) {

        // Creating the instance of ImageUtils
        ImageUtils imageUtils = ImageUtils.getInstance(activity.getContentResolver());

        // Handling all the image processing and uploads in background
        Executors.newFixedThreadPool(4).execute(() -> {

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            for (Uri uri : imagesUri) {
                if (uri == null) continue;

                String s = imageUtils.getFilePath(uri);
                File file = new File(activity.getCacheDir(), s);
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    byte[] bytes = imageUtils.getByteArray(uri, 40);
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

            RequestBody requestBody = builder.build();
            Log.d(TAG, "uploadImage: " + requestBody.toString());

            // Outside for loop
            Request request = new Request.Builder()
                    .url(PathHandler.IMAGES_UPLOAD_PATH)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .post(requestBody)
                    .build();


            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.has("urls")) {
                            String[] urls = JsonHandler.getImages(jsonObject);
                            notifySuccess(urls);
                            return;
                        }

                        notifyFailure(new Exception("Unable to upload"));
                    } catch (JSONException e) {
                        notifyFailure(e);
                        e.printStackTrace();
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
     * - canceled
     * - progress changed
     */
    private void notifySuccess(String[] urls) {
        handler.post(() -> {
            storageCallback.onSuccess(urls);
        });
    }

    private void notifyFailure(Exception e) {
        handler.post(() -> {
            storageCallback.onFailure(e);
        });
    }
}
