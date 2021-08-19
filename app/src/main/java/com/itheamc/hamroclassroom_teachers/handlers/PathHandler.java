package com.itheamc.hamroclassroom_teachers.handlers;

import com.squareup.okhttp.Request;

public class PathHandler {
    /*
    Base Path
     */
    public static final String BASE_PATH = "https://hamroclassroom.amcbizprojects.co.in/";

    /*
    Paths for School
     */
    public static final String SCHOOLS_PATH = BASE_PATH + "schools/";

    /*
    Paths for Teacher
     */
    public static final String TEACHERS_PATH = BASE_PATH + "teachers/";

    /*
    Paths for Student
     */
    public static final String STUDENTS_PATH = BASE_PATH + "students/";
    public static final String STUDENTS_SUBJECT_PATH = STUDENTS_PATH + "subject/";

    /*
    Paths for Subject
     */
    public static final String SUBJECTS_PATH = BASE_PATH + "subjects/";

    /*
    Paths for Assignment
     */
    public static final String ASSIGNMENTS_PATH = BASE_PATH + "assignments/";

    /*
    Paths for Submission
     */
    public static final String SUBMISSIONS_PATH = BASE_PATH + "submissions/";

    /*
    Paths for Notice
     */
    public static final String NOTICES_PATH = BASE_PATH + "notices/";
}
