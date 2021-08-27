package com.itheamc.hamroclassroom_teachers.callbacks;

import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;

import java.util.List;

public interface QueryCallbacks {
    void onQuerySuccess(List<User> user,
                        List<School> schools,
                        List<Student> students,
                        List<Subject> subjects,
                        List<Assignment> assignments,
                        List<Submission> submissions,
                        List<Notice> notices);

    void onQuerySuccess(User user,
                        School school,
                        Student student,
                        Subject subject,
                        Assignment assignment,
                        Submission submission,
                        Notice notice);

    void onQuerySuccess(String message);

    void onQueryFailure(Exception e);
}
