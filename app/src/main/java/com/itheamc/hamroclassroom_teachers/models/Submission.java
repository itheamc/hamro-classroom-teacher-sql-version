package com.itheamc.hamroclassroom_teachers.models;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Arrays;
import java.util.Objects;

public class Submission {
    private String _id;
    private String[] _images;
    private String[] _docs;
    private String _texts;
    private String _assignment_ref;
    private Assignment _assignment;
    private String _student_ref;
    private Student _student;
    private String _submitted_date;
    private String _checked_date;
    private boolean _checked;
    private String _comment;

    // Constructor
    public Submission() {
    }

    // Constructor with parameters
    public Submission(String _id, String[] _images, String[] _docs, String _texts, String _assignment_ref, Assignment _assignment, String _student_ref, Student _student, String _submitted_date, String _checked_date, boolean _checked, String _comment) {
        this._id = _id;
        this._images = _images;
        this._docs = _docs;
        this._texts = _texts;
        this._assignment_ref = _assignment_ref;
        this._assignment = _assignment;
        this._student_ref = _student_ref;
        this._student = _student;
        this._submitted_date = _submitted_date;
        this._checked_date = _checked_date;
        this._checked = _checked;
        this._comment = _comment;
    }

    // Getters and Setters
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String get_texts() {
        return _texts;
    }

    public void set_texts(String _texts) {
        this._texts = _texts;
    }

    public String get_assignment_ref() {
        return _assignment_ref;
    }

    public void set_assignment_ref(String _assignment_ref) {
        this._assignment_ref = _assignment_ref;
    }

    public Assignment get_assignment() {
        return _assignment;
    }

    public void set_assignment(Assignment _assignment) {
        this._assignment = _assignment;
    }

    public String get_student_ref() {
        return _student_ref;
    }

    public void set_student_ref(String _student_ref) {
        this._student_ref = _student_ref;
    }

    public Student get_student() {
        return _student;
    }

    public void set_student(Student _student) {
        this._student = _student;
    }

    public String get_submitted_date() {
        return _submitted_date;
    }

    public void set_submitted_date(String _submitted_date) {
        this._submitted_date = _submitted_date;
    }

    public String get_checked_date() {
        return _checked_date;
    }

    public void set_checked_date(String _checked_date) {
        this._checked_date = _checked_date;
    }

    public boolean is_checked() {
        return _checked;
    }

    public void set_checked(boolean _checked) {
        this._checked = _checked;
    }

    public String get_comment() {
        return _comment;
    }

    public void set_comment(String _comment) {
        this._comment = _comment;
    }

    // Overriding toString() method
    @Override
    public String toString() {
        return "Submission{" +
                "_id='" + _id + '\'' +
                ", _images=" + Arrays.toString(_images) +
                ", _docs=" + Arrays.toString(_docs) +
                ", _texts='" + _texts + '\'' +
                ", _assignment_ref='" + _assignment_ref + '\'' +
                ", _assignment=" + _assignment +
                ", _student_ref='" + _student_ref + '\'' +
                ", _student=" + _student +
                ", _submitted_date='" + _submitted_date + '\'' +
                ", _checked_date='" + _checked_date + '\'' +
                ", _checked=" + _checked +
                ", _comment='" + _comment + '\'' +
                '}';
    }

    // Overriding equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Submission that = (Submission) o;
        return _checked == that._checked &&
                Objects.equals(_id, that._id) &&
                Arrays.equals(_images, that._images) &&
                Arrays.equals(_docs, that._docs) &&
                Objects.equals(_texts, that._texts) &&
                Objects.equals(_assignment_ref, that._assignment_ref) &&
                Objects.equals(_assignment, that._assignment) &&
                Objects.equals(_student_ref, that._student_ref) &&
                Objects.equals(_student, that._student) &&
                Objects.equals(_submitted_date, that._submitted_date) &&
                Objects.equals(_checked_date, that._checked_date) &&
                Objects.equals(_comment, that._comment);
    }



    // DiffUtil.ItemCallback
    public static DiffUtil.ItemCallback<Submission> submissionItemCallback = new DiffUtil.ItemCallback<Submission>() {
        @Override
        public boolean areItemsTheSame(@NonNull Submission oldItem, @NonNull Submission newItem) {
            return newItem.equals(oldItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Submission oldItem, @NonNull Submission newItem) {
            return false;
        }
    };

    // Binding adapter to handle the view visibility
    @BindingAdapter("android:is_visible")
    public static void setVisibility(View view, boolean bool) {
        view.setVisibility(bool ? View.VISIBLE : View.GONE);
    }
}
