package com.itheamc.hamroclassroom_teachers.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Arrays;
import java.util.Objects;

public class Material {
    private String _id;
    private String _title;
    private String[] _images;
    private String[] _docs;
    private String _class;
    private String _teacher_ref;
    private User _teacher;
    private String _subject_ref;
    private Subject _subject;
    private String _school_ref;
    private School _school;
    private String _added_date;

    // Constructor
    public Material() {
    }

    // Constructor with parameters
    public Material(String _id, String _title, String[] _images, String[] _docs, String _class, String _teacher_ref, User _teacher, String _subject_ref, Subject _subject, String _school_ref, School _school, String _added_date) {
        this._id = _id;
        this._title = _title;
        this._images = _images;
        this._docs = _docs;
        this._class = _class;
        this._teacher_ref = _teacher_ref;
        this._teacher = _teacher;
        this._subject_ref = _subject_ref;
        this._subject = _subject;
        this._school_ref = _school_ref;
        this._school = _school;
        this._added_date = _added_date;
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String[] get_images() {
        return _images;
    }

    public void set_images(String[] _images) {
        this._images = _images;
    }

    public String[] get_docs() {
        return _docs;
    }

    public void set_docs(String[] _docs) {
        this._docs = _docs;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String get_teacher_ref() {
        return _teacher_ref;
    }

    public void set_teacher_ref(String _teacher_ref) {
        this._teacher_ref = _teacher_ref;
    }

    public User get_teacher() {
        return _teacher;
    }

    public void set_teacher(User _teacher) {
        this._teacher = _teacher;
    }

    public String get_subject_ref() {
        return _subject_ref;
    }

    public void set_subject_ref(String _subject_ref) {
        this._subject_ref = _subject_ref;
    }

    public Subject get_subject() {
        return _subject;
    }

    public void set_subject(Subject _subject) {
        this._subject = _subject;
    }

    public String get_school_ref() {
        return _school_ref;
    }

    public void set_school_ref(String _school_ref) {
        this._school_ref = _school_ref;
    }

    public School get_school() {
        return _school;
    }

    public void set_school(School _school) {
        this._school = _school;
    }

    public String get_added_date() {
        return _added_date;
    }

    public void set_added_date(String _added_date) {
        this._added_date = _added_date;
    }


    // toString() method
    @Override
    public String toString() {
        return "Material{" +
                "_id='" + _id + '\'' +
                ", _title='" + _title + '\'' +
                ", _images=" + Arrays.toString(_images) +
                ", _docs=" + Arrays.toString(_docs) +
                ", _class='" + _class + '\'' +
                ", _teacher_ref='" + _teacher_ref + '\'' +
                ", _teacher=" + _teacher +
                ", _subject_ref='" + _subject_ref + '\'' +
                ", _subject=" + _subject +
                ", _school_ref='" + _school_ref + '\'' +
                ", _school=" + _school +
                ", _added_date='" + _added_date + '\'' +
                '}';
    }


    // equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return Objects.equals(_id, material._id) && Objects.equals(_title, material._title) && Arrays.equals(_images, material._images) && Arrays.equals(_docs, material._docs) && Objects.equals(_class, material._class) && Objects.equals(_teacher_ref, material._teacher_ref) && Objects.equals(_teacher, material._teacher) && Objects.equals(_subject_ref, material._subject_ref) && Objects.equals(_subject, material._subject) && Objects.equals(_school_ref, material._school_ref) && Objects.equals(_school, material._school) && Objects.equals(_added_date, material._added_date);
    }


    // DiffUtil.ItemCallbacks
    public static DiffUtil.ItemCallback<Material> materialItemCallback = new DiffUtil.ItemCallback<Material>() {
        @Override
        public boolean areItemsTheSame(@NonNull Material oldItem, @NonNull Material newItem) {
            return newItem.equals(oldItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Material oldItem, @NonNull Material newItem) {
            return false;
        }
    };

}
