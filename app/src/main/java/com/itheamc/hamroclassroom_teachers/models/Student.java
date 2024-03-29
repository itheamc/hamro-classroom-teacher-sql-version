package com.itheamc.hamroclassroom_teachers.models;

import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class Student {
    private String _id;
    private String _name;
    private String _gender;
    private String _image;
    private String _phone;
    private String _email;
    private String _address;
    private String _guardian;
    private String _class;
    private String _section;
    private String _roll_number;
    private String _school_ref;
    private School _school;
    private String _joined_on;

    // Constructor
    public Student() {
    }


    // Constructor with parameters
    public Student(String _id, String _name, String _gender, String _image, String _phone, String _email, String _address, String _guardian, String _class, String _section, String _roll_number, String _school_ref, School _school, String _joined_on) {
        this._id = _id;
        this._name = _name;
        this._gender = _gender;
        this._image = _image;
        this._phone = _phone;
        this._email = _email;
        this._address = _address;
        this._guardian = _guardian;
        this._class = _class;
        this._section = _section;
        this._roll_number = _roll_number;
        this._school_ref = _school_ref;
        this._school = _school;
        this._joined_on = _joined_on;
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_gender() {
        return _gender;
    }

    public void set_gender(String _gender) {
        this._gender = _gender;
    }

    public String get_image() {
        return _image;
    }

    public void set_image(String _image) {
        this._image = _image;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String get_guardian() {
        return _guardian;
    }

    public void set_guardian(String _guardian) {
        this._guardian = _guardian;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String get_section() {
        return _section;
    }

    public void set_section(String _section) {
        this._section = _section;
    }

    public String get_roll_number() {
        return _roll_number;
    }

    public void set_roll_number(String _roll_number) {
        this._roll_number = _roll_number;
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

    public String get_joined_on() {
        return _joined_on;
    }

    public void set_joined_on(String _joined_on) {
        this._joined_on = _joined_on;
    }

    // Overriding toString() method
    @Override
    public String toString() {
        return "Student{" +
                "_id='" + _id + '\'' +
                ", _name='" + _name + '\'' +
                ", _gender='" + _gender + '\'' +
                ", _image='" + _image + '\'' +
                ", _phone='" + _phone + '\'' +
                ", _email='" + _email + '\'' +
                ", _address='" + _address + '\'' +
                ", _guardian='" + _guardian + '\'' +
                ", _class='" + _class + '\'' +
                ", _section='" + _section + '\'' +
                ", _roll_number='" + _roll_number + '\'' +
                ", _school_ref='" + _school_ref + '\'' +
                ", _school=" + _school +
                ", _joined_on='" + _joined_on + '\'' +
                '}';
    }


    // equals() Method


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(_id, student._id) &&
                Objects.equals(_name, student._name) &&
                Objects.equals(_gender, student._gender) &&
                Objects.equals(_image, student._image) &&
                Objects.equals(_phone, student._phone) &&
                Objects.equals(_email, student._email) &&
                Objects.equals(_address, student._address) &&
                Objects.equals(_guardian, student._guardian) &&
                Objects.equals(_class, student._class) &&
                Objects.equals(_section, student._section) &&
                Objects.equals(_roll_number, student._roll_number) &&
                Objects.equals(_school_ref, student._school_ref) &&
                Objects.equals(_school, student._school) &&
                Objects.equals(_joined_on, student._joined_on);
    }


    // DiffUtil.ItemCallbacks
    public static DiffUtil.ItemCallback<Student> studentItemCallback = new DiffUtil.ItemCallback<Student>() {
        @Override
        public boolean areItemsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
            return newItem.equals(oldItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
            return false;
        }
    };


    // Binding Adapter
    @BindingAdapter("android:imageButtonSrc")
    public static void setImage(ImageButton imageButton, String imageUrl) {
        Picasso.get()
                .load(imageUrl)
                .into(imageButton);
    }
}
