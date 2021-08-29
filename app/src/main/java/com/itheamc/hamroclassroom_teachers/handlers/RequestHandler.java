package com.itheamc.hamroclassroom_teachers.handlers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Material;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.Amcryption;
import com.itheamc.hamroclassroom_teachers.utils.ArrayUtils;
import com.itheamc.hamroclassroom_teachers.utils.TimeUtils;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;


public class RequestHandler {
    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Schools
     */

    // GET REQUEST
    public static final Request GET_REQUEST_SCHOOLS = new Request.Builder().url(PathHandler.SCHOOLS_PATH).headers(AuthHandler.authHeaders()).get().build();

    // GET REQUEST
    public static Request schoolGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.SCHOOLS_PATH + _id).headers(AuthHandler.authHeaders()).get().build();
    }

    // POST REQUEST
    public static Request schoolPostRequest(@NonNull School school) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", school.get_id())
                .add("_name", school.get_name())
                .add("_phone", school.get_phone())
                .add("_email", school.get_email())
                .add("_address", school.get_address())
                .add("_website", school.get_website())
                .add("_logo", school.get_icon())
                .add("_principal", school.get_principal_ref())
                .add("_established_on", school.get_established_on())
                .add("_joined_on", school.get_joined_on())
                .build();

        return new Request.Builder().url(PathHandler.SCHOOLS_PATH).headers(AuthHandler.authHeaders()).post(requestBody).build();
    }


    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Teachers
     */

    // GET REQUEST
    public static final Request GET_REQUEST_TEACHERS = new Request.Builder().url(PathHandler.TEACHERS_PATH).headers(AuthHandler.authHeaders()).get().build();

    // GET REQUEST
    public static Request teacherGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.TEACHERS_PATH + _id).addHeader("by", "id").headers(AuthHandler.authHeaders()).get().build();
    }

    // POST REQUEST
    public static Request teacherPostRequest(@NonNull User user) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", user.get_id())
                .add("_name", Amcryption.getEncoder().encode(user.get_name()))
                .add("_gender", Amcryption.getEncoder().encode(user.get_gender()))
                .add("_image", user.get_image())
                .add("_phone", Amcryption.getEncoder().encode(user.get_phone()))
                .add("_email", Amcryption.getEncoder().encode(user.get_email()))
                .add("_address", Amcryption.getEncoder().encode(user.get_address()))
                .add("_school", user.get_schools_ref())
                .add("_joined_on", user.get_joined_on())
                .build();

        return new Request.Builder().url(PathHandler.TEACHERS_PATH).headers(AuthHandler.authHeaders()).post(requestBody).build();
    }


    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Students
     */

    // GET REQUEST
    public static final Request GET_REQUEST_STUDENTS = new Request.Builder().url(PathHandler.TEACHERS_PATH).headers(AuthHandler.authHeaders()).get().build();

    // GET REQUEST
    public static Request studentGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.STUDENTS_PATH + _id).headers(AuthHandler.authHeaders()).get().build();
    }



    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Subjects
     */

    // GET REQUEST
    public static final Request GET_REQUEST_SUBJECTS = new Request.Builder().url(PathHandler.SUBJECTS_PATH).headers(AuthHandler.authHeaders()).get().build();

    // GET REQUEST
    public static Request subjectGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.SUBJECTS_PATH + _id).addHeader("by", "id").headers(AuthHandler.authHeaders()).get().build();
    }

    // GET REQUEST
    public static Request subjectGetRequestByTeacherId(@NonNull String _teacherId) {
        return new Request.Builder().url(PathHandler.SUBJECTS_PATH + _teacherId).addHeader("by", "teacher").headers(AuthHandler.authHeaders()).get().build();
    }

    // GET REQUEST
    public static Request subjectGetRequestBySchoolIdAndClass(@NonNull String _schoolId, String _class) {
        return new Request.Builder().url(PathHandler.SUBJECTS_PATH + _schoolId + "___" + _class).addHeader("by", "school").headers(AuthHandler.authHeaders()).get().build();
    }

    // POST REQUEST
    public static Request subjectPostRequest(@NonNull Subject subject) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", subject.get_id())
                .add("_name", subject.get_name())
                .add("_class", subject.get_class())
                .add("_teacher", subject.get_teacher_ref())
                .add("_school", subject.get_school_ref())
                .add("_join_url", subject.get_join_url())
                .add("_start_time", subject.get_start_time())
                .add("_added_on", subject.get_added_on())
                .add("_hidden", "0")
                .build();

        return new Request.Builder().url(PathHandler.SUBJECTS_PATH).headers(AuthHandler.authHeaders()).post(requestBody).build();
    }

    // PATCH REQUEST
    public static Request subjectPatchRequest(Subject subject) {
        FormBody.Builder builder = new FormBody.Builder();
        if (subject.get_id() != null) builder.add("_id", subject.get_id());
        if (subject.get_name() != null) builder.add("_name", subject.get_name());
        if (subject.get_class() != null) builder.add("_class", subject.get_class());
        if (subject.get_school_ref() != null) builder.add("_school", subject.get_school_ref());
        if (subject.get_start_time() != null) builder.add("_start_time", subject.get_start_time());

        RequestBody requestBody = builder.build();

        return new Request.Builder().url(PathHandler.SUBJECTS_PATH).headers(AuthHandler.authHeaders()).patch(requestBody).build();
    }

    // PATCH REQUEST LINK
    public static Request linkPatchRequest(String _subId, String _link) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", _subId)
                .add("_join_url", _link)
                .build();

        return new Request.Builder().url(PathHandler.SUBJECTS_PATH).headers(AuthHandler.authHeaders()).patch(requestBody).build();
    }


    // DELETE REQUEST
    public static Request subjectDeleteRequest(String _id) {
        return new Request.Builder().url(PathHandler.SUBJECTS_PATH + _id).headers(AuthHandler.authHeaders()).delete().build();
    }

    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Assignments
     */

    // GET REQUEST
    public static final Request GET_REQUEST_ASSIGNMENTS = new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH).headers(AuthHandler.authHeaders()).get().build();

    // GET REQUEST
    public static Request assignmentGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH + _id).addHeader("by", "id").headers(AuthHandler.authHeaders()).get().build();
    }

    // GET REQUEST
    public static Request assignmentGetRequestBySubjectId(@NonNull String _ref, @NonNull boolean isBySubjectId) {
        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH + _ref).addHeader("by", isBySubjectId ? "subject" : "teacher").headers(AuthHandler.authHeaders()).get().build();
    }

    // DELETE REQUEST
    public static Request assignmentDeleteRequest(String _id) {
        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH + _id).headers(AuthHandler.authHeaders()).delete().build();
    }


    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Submissions
     */

    // GET REQUEST
    public static final Request GET_REQUEST_SUBMISSIONS = new Request.Builder().url(PathHandler.SUBMISSIONS_PATH).headers(AuthHandler.authHeaders()).get().build();

    // GET REQUEST
    public static Request submissionGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH + _id).addHeader("by", "id").headers(AuthHandler.authHeaders()).get().build();
    }

    // GET REQUEST
    public static Request submissionGetRequestByAssignmentId(@NonNull String _assignmentId) {
        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH + _assignmentId).addHeader("by", "assignment").headers(AuthHandler.authHeaders()).get().build();
    }


    // PATCH REQUEST
    public static Request submissionStatusUpdateRequest(String _id, String _comment) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", _id)
                .add("_checked_date", TimeUtils.now())
                .add("_comment", _comment)
                .build();

        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH).headers(AuthHandler.authHeaders()).patch(requestBody).build();
    }

    /*
   ------------------------------------------------------------------------------------------------
   ------------------------------------------------------------------------------------------------
   Requests for Notices
    */

    // GET REQUEST
    public static final Request GET_REQUEST_NOTICES = new Request.Builder().url(PathHandler.NOTICES_PATH).headers(AuthHandler.authHeaders()).get().build();

    // GET REQUEST
    public static Request noticeGetRequestById(@NonNull String _id, boolean isById) {
        return new Request.Builder().url(PathHandler.NOTICES_PATH + _id).addHeader("by", isById ? "id" : "teacher").headers(AuthHandler.authHeaders()).get().build();
    }

    // POST REQUEST
    public static Request noticePostRequest(@NonNull Notice notice) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", notice.get_id())
                .add("_title", notice.get_title())
                .add("_desc", notice.get_desc())
                .add("_school", notice.get_school_ref())
                .add("_classes", ArrayUtils.toString(notice.get_classes()))
                .add("_teacher", notice.get_teacher_ref())
                .add("_notified_on", notice.get_notified_on())
                .build();
        Log.d("TESTING", "noticePostRequest: " + notice.toString());

        return new Request.Builder().url(PathHandler.NOTICES_PATH).headers(AuthHandler.authHeaders()).post(requestBody).build();
    }


    // DELETE REQUEST
    public static Request noticeDeleteRequest(String _id) {
        return new Request.Builder().url(PathHandler.NOTICES_PATH + _id).headers(AuthHandler.authHeaders()).delete().build();
    }



     /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Materials
     */

    // GET REQUEST
    public static final Request  GET_REQUEST_MATERIALS= new Request.Builder().url(PathHandler.MATERIALS_PATH).headers(AuthHandler.authHeaders()).get().build();

    // GET REQUEST
    public static Request materialsGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.MATERIALS_PATH + _id).addHeader("by", "id").headers(AuthHandler.authHeaders()).get().build();
    }

    // GET REQUEST
    public static Request materialsGetRequestByRef(@NonNull String _ref, @NonNull boolean isBySubjectId) {
        return new Request.Builder().url(PathHandler.MATERIALS_PATH + _ref).addHeader("by", isBySubjectId ? "subject" : "teacher").headers(AuthHandler.authHeaders()).get().build();
    }

    // DELETE REQUEST
    public static Request materialDeleteRequest(String _id) {
        return new Request.Builder().url(PathHandler.MATERIALS_PATH + _id).headers(AuthHandler.authHeaders()).delete().build();
    }


}
