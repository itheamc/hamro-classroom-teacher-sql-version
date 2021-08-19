package com.itheamc.hamroclassroom_teachers.models;

import java.util.Arrays;

public class Notice {
    private String _id;
    private String _title;
    private String _desc;
    private String _school_ref;     // School ref
    private School _school;
    private String[] _classes;      // list of classes to show this notice
    private String _teacher_ref;
    private User _teacher;
    private String _notified_on;


    // Constructor
    public Notice() {
    }


    // Constructor with parameters
    public Notice(String _id, String _title, String _desc, String _school_ref, School _school, String[] _classes, String _teacher_ref, User _teacher, String _notified_on) {
        this._id = _id;
        this._title = _title;
        this._desc = _desc;
        this._school_ref = _school_ref;
        this._school = _school;
        this._classes = _classes;
        this._teacher_ref = _teacher_ref;
        this._teacher = _teacher;
        this._notified_on = _notified_on;
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

    public String[] get_classes() {
        return _classes;
    }

    public void set_classes(String[] _classes) {
        this._classes = _classes;
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

    public String get_notified_on() {
        return _notified_on;
    }

    public void set_notified_on(String _notified_on) {
        this._notified_on = _notified_on;
    }

    // Overriding toString() method
    @Override
    public String toString() {
        return "Notice{" +
                "_id='" + _id + '\'' +
                ", _title='" + _title + '\'' +
                ", _desc='" + _desc + '\'' +
                ", _school_ref='" + _school_ref + '\'' +
                ", _school=" + _school +
                ", _classes=" + Arrays.toString(_classes) +
                ", _teacher_ref='" + _teacher_ref + '\'' +
                ", _teacher=" + _teacher +
                ", _notified_on='" + _notified_on + '\'' +
                '}';
    }
}
