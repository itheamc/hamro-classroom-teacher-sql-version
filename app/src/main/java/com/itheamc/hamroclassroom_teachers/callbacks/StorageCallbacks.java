package com.itheamc.hamroclassroom_teachers.callbacks;

public interface StorageCallbacks {
    void onSuccess(String imageUrl);
    void onFailure(Exception e);
    void onCanceled();
    void onProgress();
}
