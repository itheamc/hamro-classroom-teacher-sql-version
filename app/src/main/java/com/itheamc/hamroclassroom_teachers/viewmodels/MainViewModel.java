package com.itheamc.hamroclassroom_teachers.viewmodels;

import android.os.Build;

import androidx.lifecycle.ViewModel;

import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {
    /*
    Objects
     */
    private User user;
    private Student student;
    private Subject subject;
    private Assignment assignment;
    private Submission submission;
    private School school;

    /*
    Lists
     */
    private List<School> schools;
    private List<Student> students;
    private List<Subject> subjects;
    private List<Assignment> assignments;
    private List<Submission> submissions;
    private List<Notice> notices;

    /*
   Boolean
    */
    private boolean isSubjectUpdating;

    /*
    Map
     */
    private Map<String, List<Assignment>> assignmentsHashMap;
    private Map<String, List<Submission>> submissionsHashMap;

    /*
    Getters and Setters
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public List<School> getSchools() {
        return schools;
    }

    public void setSchools(List<School> schools) {
        this.schools = schools;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }

    public boolean isSubjectUpdating() {
        return isSubjectUpdating;
    }

    public void setSubjectUpdating(boolean subjectUpdating) {
        isSubjectUpdating = subjectUpdating;
    }

    public Map<String, List<Submission>> getSubmissionsHashMap() {
        return submissionsHashMap;
    }


    public void setSubmissionsHashMap(List<Submission> _submissions) {
        if (this.submissionsHashMap == null) submissionsHashMap = new HashMap<>();
        this.submissionsHashMap.put(_submissions.get(0).get_assignment_ref(), _submissions);
    }


    public Map<String, List<Assignment>> getAssignmentsHashMap() {
        return assignmentsHashMap;
    }

    public void setAssignmentsHashMap(List<Assignment> _assignments) {
        if (this.assignmentsHashMap == null) assignmentsHashMap = new HashMap<>();
        this.assignmentsHashMap.put(_assignments.get(0).get_subject_ref(), _assignments);
    }


    /*
          Function to add school on the subject and return the Subject List
           */
    public List<Subject> getUpdatedSubjects(List<School> __schools) {
        List<Subject> updatedSubjects = new ArrayList<>();
        for (Subject s: subjects) {
            for (School sc: __schools) {
                if (!s.get_school_ref().equals(sc.get_id())) continue;

                s.set_school(sc);
                break;
            }

            updatedSubjects.add(s);
        }

        this.subjects = new ArrayList<>();
        this.subjects.addAll(updatedSubjects);
        return updatedSubjects;
    }


    /*
    Function to add student on the submission and return the submissions
     */
    public List<Submission> getUpdatedSubmissions(String assignment_ref, List<Student> _students) {
        // Add student in submission
        List<Submission> updatedSubmissions = new ArrayList<>();
        List<Submission> oldSubmissions = submissionsHashMap.get(assignment_ref);

        assert oldSubmissions != null;
        for (Submission sub: oldSubmissions) {
            for (Student st: _students) {
                if (!sub.get_student_ref().equals(st.get_id())) continue;

                sub.set_student(st);
                break;
            }
            updatedSubmissions.add(sub);
        }

        submissionsHashMap.put(assignment_ref, updatedSubmissions);
        return updatedSubmissions;
    }

}