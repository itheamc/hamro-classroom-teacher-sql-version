package com.itheamc.hamroclassroom_teachers.callbacks;

public interface StorageCallbacks {
    void onSuccess(String[] urls);
    void onFailure(Exception e);
}
