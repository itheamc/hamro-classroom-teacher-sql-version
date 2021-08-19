package com.itheamc.hamroclassroom_teachers.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class School {
    private String _id;
    private String _name;
    private String _phone;
    private String _email;
    private String _address;
    private String _website;
    private String _icon;
    private String _principal_ref;
    private User _principal;
    private String _established_on;
    private String _joined_on;

    // Constructor
    public School() {
    }

    // Constructor with parameters
    public School(String _id, String _name, String _phone, String _email, String _address, String _website, String _icon, String _principal_ref, User _principal, String _established_on, String _joined_on) {
        this._id = _id;
        this._name = _name;
        this._phone = _phone;
        this._email = _email;
        this._address = _address;
        this._website = _website;
        this._icon = _icon;
        this._principal_ref = _principal_ref;
        this._principal = _principal;
        this._established_on = _established_on;
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

    public String get_website() {
        return _website;
    }

    public void set_website(String _website) {
        this._website = _website;
    }

    public String get_icon() {
        return _icon;
    }

    public void set_icon(String _icon) {
        this._icon = _icon;
    }

    public String get_principal_ref() {
        return _principal_ref;
    }

    public void set_principal_ref(String _principal_ref) {
        this._principal_ref = _principal_ref;
    }

    public User get_principal() {
        return _principal;
    }

    public void set_principal(User _principal) {
        this._principal = _principal;
    }

    public String get_established_on() {
        return _established_on;
    }

    public void set_established_on(String _established_on) {
        this._established_on = _established_on;
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
        return "School{" +
                "_id='" + _id + '\'' +
                ", _name='" + _name + '\'' +
                ", _phone='" + _phone + '\'' +
                ", _email='" + _email + '\'' +
                ", _address='" + _address + '\'' +
                ", _website='" + _website + '\'' +
                ", _icon='" + _icon + '\'' +
                ", _principal_ref='" + _principal_ref + '\'' +
                ", _principal=" + _principal +
                ", _established_on='" + _established_on + '\'' +
                ", _joined_on='" + _joined_on + '\'' +
                '}';
    }

    // Overriding equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        School school = (School) o;
        return Objects.equals(_id, school._id) &&
                Objects.equals(_name, school._name) &&
                Objects.equals(_phone, school._phone) &&
                Objects.equals(_email, school._email) &&
                Objects.equals(_address, school._address) &&
                Objects.equals(_website, school._website) &&
                Objects.equals(_icon, school._icon) &&
                Objects.equals(_principal_ref, school._principal_ref) &&
                Objects.equals(_principal, school._principal) &&
                Objects.equals(_established_on, school._established_on) &&
                Objects.equals(_joined_on, school._joined_on);
    }


    // DiffUtil.ItemCallback object
    public static DiffUtil.ItemCallback<School> schoolItemCallback = new DiffUtil.ItemCallback<School>() {
        @Override
        public boolean areItemsTheSame(@NonNull School oldItem, @NonNull School newItem) {
            return newItem.equals(oldItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull School oldItem, @NonNull School newItem) {
            return false;
        }
    };
}
