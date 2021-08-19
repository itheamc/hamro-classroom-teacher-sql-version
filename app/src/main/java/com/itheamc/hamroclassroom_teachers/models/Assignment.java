package com.itheamc.hamroclassroom_teachers.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Arrays;
import java.util.Objects;

public class Assignment {
    private String _id;
    private String _title;
    private String _desc;
    private String[] _images;
    private String[] _docs;
    private String _class;
    private String _teacher_ref;
    private User _teacher;
    private String _subject_ref;
    private Subject _subject;
    private String _school_ref;
    private School _school;
    private String _assigned_date;
    private String _due_date;

    // Constructor
    public Assignment() {
    }

    // Constructor with parameters
    public Assignment(String _id, String _title, String _desc, String[] _images, String[] _docs, String _class, String _teacher_ref, User _teacher, String _subject_ref, Subject _subject, String _school_ref, School _school, String _assigned_date, String _due_date) {
        this._id = _id;
        this._title = _title;
        this._desc = _desc;
        this._images = _images;
        this._docs = _docs;
        this._class = _class;
        this._teacher_ref = _teacher_ref;
        this._teacher = _teacher;
        this._subject_ref = _subject_ref;
        this._subject = _subject;
        this._school_ref = _school_ref;
        this._school = _school;
        this._assigned_date = _assigned_date;
        this._due_date = _due_date;
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

    public String get_desc() {
        return _desc;
    }

    public void set_desc(String _desc) {
        this._desc = _desc;
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

    public String get_assigned_date() {
        return _assigned_date;
    }

    public void set_assigned_date(String _assigned_date) {
        this._assigned_date = _assigned_date;
    }

    public String get_due_date() {
        return _due_date;
    }

    public void set_due_date(String _due_date) {
        this._due_date = _due_date;
    }

    // Overriding toString() method
    @Override
    public String toString() {
        return "Assignment{" +
                "_id='" + _id + '\'' +
                ", _title='" + _title + '\'' +
                ", _desc='" + _desc + '\'' +
                ", _images=" + Arrays.toString(_images) +
                ", _docs=" + Arrays.toString(_docs) +
                ", _class='" + _class + '\'' +
                ", _teacher_ref='" + _teacher_ref + '\'' +
                ", _teacher=" + _teacher +
                ", _subject_ref='" + _subject_ref + '\'' +
                ", _subject=" + _subject +
                ", _school_ref='" + _school_ref + '\'' +
                ", _school=" + _school +
                ", _assigned_date='" + _assigned_date + '\'' +
                ", _due_date='" + _due_date + '\'' +
                '}';
    }

    // Overriding equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(_id, that._id) &&
                Objects.equals(_title, that._title) &&
                Objects.equals(_desc, that._desc) &&
                Arrays.equals(_images, that._images) &&
                Arrays.equals(_docs, that._docs) &&
                Objects.equals(_class, that._class) &&
                Objects.equals(_teacher_ref, that._teacher_ref) &&
                Objects.equals(_teacher, that._teacher) &&
                Objects.equals(_subject_ref, that._subject_ref) &&
                Objects.equals(_subject, that._subject) &&
                Objects.equals(_school_ref, that._school_ref) &&
                Objects.equals(_school, that._school) &&
                Objects.equals(_assigned_date, that._assigned_date) &&
                Objects.equals(_due_date, that._due_date);
    }



    // DiffUtil.ItemCallback
    public static DiffUtil.ItemCallback<Assignment> assignmentItemCallback = new DiffUtil.ItemCallback<Assignment>() {
        @Override
        public boolean areItemsTheSame(@NonNull Assignment oldItem, @NonNull Assignment newItem) {
            return newItem.equals(oldItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Assignment oldItem, @NonNull Assignment newItem) {
            return false;
        }
    };
}
