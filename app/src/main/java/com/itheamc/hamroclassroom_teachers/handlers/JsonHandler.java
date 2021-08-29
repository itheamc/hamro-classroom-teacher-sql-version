package com.itheamc.hamroclassroom_teachers.handlers;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Material;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonHandler {


    /*
    -----------------------------------------------------------------------------------
    -----------------------------------------------------------------------------------
    Function to handle single Object (User, Student, School, Subject, Assignment,
    Submission, Notice etc)
    -----------------------------------------------------------------------------------
    -----------------------------------------------------------------------------------
     */

    // School Response Handler
    public static School getSchool(@NonNull JSONObject jsonObject) throws JSONException {
        JSONObject schoolObj;

        if (jsonObject.has("school")) schoolObj = jsonObject.getJSONObject("school");
        else if (jsonObject.has("_school")) schoolObj = jsonObject.getJSONObject("_school");
        else schoolObj = jsonObject;

        return new School(
                schoolObj.getString("_id"),
                schoolObj.getString("_name"),
                schoolObj.getString("_phone"),
                schoolObj.getString("_email"),
                schoolObj.getString("_address"),
                schoolObj.getString("_website"),
                schoolObj.getString("_logo"),
                schoolObj.getString("_principal"),
                null,
                schoolObj.getString("_established_on"),
                schoolObj.getString("_joined_on")
        );
    }


    // Teacher Response Handler
    public static User getUser(@NonNull JSONObject jsonObject) throws JSONException {
        JSONObject userObj;

        if (jsonObject.has("teacher")) userObj = jsonObject.getJSONObject("teacher");
        else if (jsonObject.has("_teacher")) userObj = jsonObject.getJSONObject("_teacher");
        else userObj = jsonObject;

        // Getting school obj

        School school = getSchool(userObj);

        // Creating new user and returning it
        return new User(
                userObj.getString("_id"),
                userObj.getString("_name"),
                userObj.getString("_gender"),
                userObj.getString("_image"),
                userObj.getString("_phone"),
                userObj.getString("_email"),
                userObj.getString("_address"),
                school.get_id(),
                school,
                userObj.getString("_joined_on")
        );
    }


    // Student Response Handler
    public static Student getStudent(@NonNull JSONObject jsonObject) throws JSONException {
        JSONObject studentObj;

        if (jsonObject.has("student")) studentObj = jsonObject.getJSONObject("student");
        else if (jsonObject.has("_student")) studentObj = jsonObject.getJSONObject("_student");
        else studentObj = jsonObject;


        // Getting school obj
        School school = getSchool(studentObj);

        // Creating new student and returning it
        return new Student(
                studentObj.getString("_id"),
                studentObj.getString("_name"),
                studentObj.getString("_gender"),
                studentObj.getString("_image"),
                studentObj.getString("_phone"),
                studentObj.getString("_email"),
                studentObj.getString("_address"),
                studentObj.getString("_guardian"),
                String.valueOf(studentObj.getInt("_class")),
                studentObj.getString("_section"),
                String.valueOf(studentObj.getInt("_roll_number")),
                school.get_id(),
                school,
                studentObj.getString("_joined_on")
        );
    }

    // Subject Response Handler
    public static Subject getSubject(@NonNull JSONObject jsonObject) throws JSONException {
        JSONObject subjectObj;

        if (jsonObject.has("subject")) subjectObj = jsonObject.getJSONObject("subject");
        else if (jsonObject.has("_subject")) subjectObj = jsonObject.getJSONObject("_subject");
        else subjectObj = jsonObject;


        // Getting teacher obj
        User user = getUser(subjectObj);
        // Getting school obj
        School school = getSchool(subjectObj);

        return new Subject(
                subjectObj.getString("_id"),
                subjectObj.getString("_name"),
                String.valueOf(subjectObj.getInt("_class")),
                user.get_id(),
                user,
                school.get_id(),
                school,
                subjectObj.getString("_join_url"),
                subjectObj.getString("_start_time"),
                subjectObj.getString("_added_on"),
                subjectObj.getInt("_hidden") == 1,
                false
        );

    }


    // Assignment Response Handler
    public static Assignment getAssignment(@NonNull JSONObject jsonObject) throws JSONException {
        JSONObject assignmentObj;

        if (jsonObject.has("assignment")) assignmentObj = jsonObject.getJSONObject("assignment");
        else if (jsonObject.has("_assignment")) assignmentObj = jsonObject.getJSONObject("_assignment");
        else assignmentObj = jsonObject;


        // Getting subject
        Subject subject = getSubject(assignmentObj);

        // Getting Assignment and returning it
        return new Assignment(
                assignmentObj.getString("_id"),
                assignmentObj.getString("_title"),
                assignmentObj.getString("_desc"),
                ArrayUtils.toArray(assignmentObj.getString("_images"), ", "),
                ArrayUtils.toArray(assignmentObj.getString("_docs"), ", "),
                String.valueOf(assignmentObj.getInt("_class")),
                subject.get_teacher_ref(),
                subject.get_teacher(),
                subject.get_id(),
                subject,
                subject.get_school_ref(),
                subject.get_school(),
                assignmentObj.getString("_assigned_date"),
                assignmentObj.getString("_due_date")
        );

    }


    // Material Response Handler
    public static Material getMaterial(@NonNull JSONObject jsonObject) throws JSONException {
        JSONObject materialOnj;

        if (jsonObject.has("material")) materialOnj = jsonObject.getJSONObject("material");
        else if (jsonObject.has("_material")) materialOnj = jsonObject.getJSONObject("_material");
        else materialOnj = jsonObject;


        // Getting subject
        Subject subject = getSubject(materialOnj);

        // Getting Material and returning it
        return new Material(
                materialOnj.getString("_id"),
                materialOnj.getString("_title"),
                ArrayUtils.toArray(materialOnj.getString("_images"), ", "),
                ArrayUtils.toArray(materialOnj.getString("_docs"), ", "),
                String.valueOf(materialOnj.getInt("_class")),
                subject.get_teacher_ref(),
                subject.get_teacher(),
                subject.get_id(),
                subject,
                subject.get_school_ref(),
                subject.get_school(),
                materialOnj.getString("_added_date")
        );

    }


    // Submissions Response Handler
    public static Submission getSubmission(@NonNull JSONObject jsonObject) throws JSONException {
        JSONObject submissionObj;

        if (jsonObject.has("submission")) submissionObj = jsonObject.getJSONObject("submission");
        else if (jsonObject.has("_submission")) submissionObj = jsonObject.getJSONObject("_submission");
        else submissionObj = jsonObject;


        // Getting Assignment
        Assignment assignment = getAssignment(submissionObj);

        // Getting Student
        Student student = getStudent(submissionObj);

        return new Submission(
                submissionObj.getString("_id"),
                ArrayUtils.toArray(submissionObj.getString("_images"), ", "),
                ArrayUtils.toArray(submissionObj.getString("_docs"), ", "),
                submissionObj.getString("_texts"),
                assignment.get_id(),
                assignment,
                student.get_id(),
                student,
                submissionObj.getString("_submitted_date"),
                submissionObj.getString("_checked_date"),
                submissionObj.getInt("_checked") == 1,
                submissionObj.getString("_comment")
        );
    }


    // Notice Response Handler
    public static Notice getNotice(@NonNull JSONObject jsonObject) throws JSONException {
        JSONObject noticeObj;

        if (jsonObject.has("notice")) noticeObj = jsonObject.getJSONObject("notice");
        else if (jsonObject.has("_notice")) noticeObj = jsonObject.getJSONObject("_notice");
        else noticeObj = jsonObject;


        // Getting Assignment
        User user = getUser(noticeObj);
        School school = getSchool(noticeObj);

        return new Notice(
                noticeObj.getString("_id"),
                noticeObj.getString("_title"),
                noticeObj.getString("_desc"),
                school.get_id(),
                school,
                ArrayUtils.toArray(noticeObj.getString("_classes"), ", "),
                user.get_id(),
                user,
                noticeObj.getString("_notified_on")
        );
    }

    /*
    -----------------------------------------------------------------------------------
    -----------------------------------------------------------------------------------
    Function to handle Object List (List<User>, List<Student>, List<School>,
    List<Subject>, List<Assignment>, List<Submission>, List<Notice> etc)
    -----------------------------------------------------------------------------------
    -----------------------------------------------------------------------------------
     */

    // Schools Response Handler
    public static List<School> getSchools(@NonNull JSONObject jsonObject) throws JSONException {
        List<School> schools = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("schools");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);

            // Adding School
            schools.add(getSchool(jo));
        }

        return schools;
    }


    // Subjects Response Handler
    public static List<Subject> getSubjects(@NonNull JSONObject jsonObject) throws JSONException {
        List<Subject> subjects = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("subjects");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);

            // Adding Subject
            subjects.add(getSubject(jo));
        }

        return subjects;
    }

    // Assignments Response Handler
    public static List<Assignment> getAssignments(@NonNull JSONObject jsonObject) throws JSONException {
        List<Assignment> assignments = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("assignments");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);

            // Adding Assignment
            assignments.add(getAssignment(jo));
        }

        return assignments;
    }


    // Materials Response Handler
    public static List<Material> getMaterials(@NonNull JSONObject jsonObject) throws JSONException {
        List<Material> materials = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("materials");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);

            // Adding Assignment
            materials.add(getMaterial(jo));
        }

        return materials;
    }


    // Submissions Response Handler
    public static List<Submission> getSubmissions(@NonNull JSONObject jsonObject) throws JSONException {
        List<Submission> submissions = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("submissions");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);

            // Adding Submission
            submissions.add(getSubmission(jo));
        }

        return submissions;
    }


    // Notices Response Handler
    public static List<Notice> getNotices(@NonNull JSONObject jsonObject) throws JSONException {
        List<Notice> notices = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("notices");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);

            // Adding Submission
            notices.add(getNotice(jo));
        }

        return notices;
    }

    // Images Urls
    public static String[] getImages(@NonNull JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = jsonObject.getJSONArray("urls");
        String[] urls = new String[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            urls[i] = jsonArray.get(i).toString();
        }

        return urls;
    }

}
