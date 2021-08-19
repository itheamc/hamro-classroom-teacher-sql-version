package com.itheamc.hamroclassroom_teachers.handlers;

import androidx.annotation.NonNull;

import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
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
    public static final Request GET_REQUEST_SCHOOLS = new Request.Builder().url(PathHandler.SCHOOLS_PATH).get().build();

    // GET REQUEST
    public static Request schoolGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.SCHOOLS_PATH + _id).get().build();
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

        return new Request.Builder().url(PathHandler.SCHOOLS_PATH).post(requestBody).build();
    }


    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Teachers
     */

    // GET REQUEST
    public static final Request GET_REQUEST_TEACHERS = new Request.Builder().url(PathHandler.TEACHERS_PATH).get().build();

    // GET REQUEST
    public static Request teacherGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.TEACHERS_PATH + _id).get().build();
    }

    // POST REQUEST
    public static Request teacherPostRequest(@NonNull User user) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", user.get_id())
                .add("_name", user.get_name())
                .add("_gender", user.get_gender())
                .add("_image", user.get_image())
                .add("_phone", user.get_phone())
                .add("_email", user.get_email())
                .add("_address", user.get_address())
                .add("_school", user.get_schools_ref())
                .add("_joined_on", user.get_joined_on())
                .build();

        return new Request.Builder().url(PathHandler.TEACHERS_PATH).post(requestBody).build();
    }


    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Students
     */

    // GET REQUEST
    public static final Request GET_REQUEST_STUDENTS = new Request.Builder().url(PathHandler.TEACHERS_PATH).get().build();

    // GET REQUEST
    public static Request studentGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.STUDENTS_PATH + _id).get().build();
    }

    // POST REQUEST
    public static Request studentPostRequest(@NonNull Student student) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", student.get_id())
                .add("_name", student.get_name())
                .add("_gender", student.get_gender())
                .add("_image", student.get_image())
                .add("_phone", student.get_phone())
                .add("_email", student.get_email())
                .add("_address", student.get_address())
                .add("_guardian", student.get_guardian())
                .add("_class", student.get_class())
                .add("_section", student.get_section())
                .add("_roll_number", student.get_roll_number())
                .add("_school", student.get_school_ref())
                .add("_joined_on", student.get_joined_on())
                .build();

        return new Request.Builder().url(PathHandler.STUDENTS_PATH).post(requestBody).build();
    }


    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Subjects
     */

    // GET REQUEST
    public static final Request GET_REQUEST_SUBJECTS = new Request.Builder().url(PathHandler.SUBJECTS_PATH).get().build();

    // GET REQUEST
    public static Request subjectGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.SUBJECTS_PATH + _id).addHeader("by", "id").get().build();
    }

    // GET REQUEST
    public static Request subjectGetRequestByTeacherId(@NonNull String _teacherId) {
        return new Request.Builder().url(PathHandler.SUBJECTS_PATH + _teacherId).addHeader("by", "teacher").get().build();
    }

    // GET REQUEST
    public static Request subjectGetRequestBySchoolIdAndClass(@NonNull String _schoolId, String _class) {
        return new Request.Builder().url(PathHandler.SUBJECTS_PATH + _schoolId + "___" + _class).addHeader("by", "school").get().build();
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

        return new Request.Builder().url(PathHandler.SUBJECTS_PATH).post(requestBody).build();
    }

    // PATCH REQUEST
    public static Request subjectPatchRequest(Subject subject) {
        FormBody.Builder builder = new FormBody.Builder();
        if (subject.get_id() != null) builder.add("_id", subject.get_id());
        if (subject.get_name() != null) builder.add("_name", subject.get_name());
        if (subject.get_school_ref() != null) builder.add("_school", subject.get_school_ref());
        if (subject.get_start_time() != null) builder.add("_start_time", subject.get_start_time());

        RequestBody requestBody = builder.build();

        return new Request.Builder().url(PathHandler.SUBJECTS_PATH).patch(requestBody).build();
    }

    // PATCH REQUEST LINK
    public static Request linkPatchRequest(String _subId, String _link) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", _subId)
                .add("_join_url", _link)
                .build();

        return new Request.Builder().url(PathHandler.SUBJECTS_PATH).patch(requestBody).build();
    }

    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Assignments
     */

    // GET REQUEST
    public static final Request GET_REQUEST_ASSIGNMENTS = new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH).get().build();

    // GET REQUEST
    public static Request assignmentGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH + _id).addHeader("by", "id").get().build();
    }

    // GET REQUEST
    public static Request assignmentGetRequestByTeacherId(@NonNull String _teacherId) {
        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH + _teacherId).addHeader("by", "teacher").get().build();
    }

    // GET REQUEST
    public static Request assignmentGetRequestBySubjectId(@NonNull String _subjectId) {
        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH + _subjectId).addHeader("by", "subject").get().build();
    }

    // GET REQUEST
    public static Request assignmentGetRequestBySchoolIdAndClass(@NonNull String _schoolId, String _class) {
        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH + _schoolId + "___" + _class).addHeader("by", "school").get().build();
    }

    // POST REQUEST
    public static Request assignmentPostRequest(@NonNull Assignment assignment) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", assignment.get_id())
                .add("_title", assignment.get_title())
                .add("_desc", assignment.get_desc())
                .add("_images", ArrayUtils.toString(assignment.get_images()))
                .add("_docs", ArrayUtils.toString(assignment.get_docs()))
                .add("_class", assignment.get_class())
                .add("_teacher", assignment.get_teacher_ref())
                .add("_subject", assignment.get_subject_ref())
                .add("_school", assignment.get_school_ref())
                .add("_assigned_date", assignment.get_assigned_date())
                .add("_due_date", assignment.get_due_date())
                .build();

        return new Request.Builder().url(PathHandler.ASSIGNMENTS_PATH).post(requestBody).build();
    }


    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Requests for Submissions
     */

    // GET REQUEST
    public static final Request GET_REQUEST_SUBMISSIONS = new Request.Builder().url(PathHandler.SUBMISSIONS_PATH).get().build();

    // GET REQUEST
    public static Request submissionGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH + _id).addHeader("by", "id").get().build();
    }

    // GET REQUEST
    public static Request submissionGetRequestByAssignmentId(@NonNull String _assignmentId) {
        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH + _assignmentId).addHeader("by", "assignment").get().build();
    }

    // GET REQUEST
    public static Request submissionGetRequestByStudentId(@NonNull String _studentId) {
        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH + _studentId).addHeader("by", "student").get().build();
    }


    // POST REQUEST
    public static Request submissionPostRequest(@NonNull Submission submission) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", submission.get_id())
                .add("_texts", submission.get_texts())
                .add("_images", ArrayUtils.toString(submission.get_images()))
                .add("_docs", ArrayUtils.toString(submission.get_docs()))
                .add("_assignment", submission.get_assignment_ref())
                .add("_student", submission.get_student_ref())
                .add("_submitted_date", submission.get_submitted_date())
                .add("_checked_date", submission.get_checked_date())
                .add("_checked", String.valueOf(submission.is_checked()))
                .add("_comment", submission.get_comment())
                .build();

        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH).post(requestBody).build();
    }

    // PATCH REQUEST
    public static Request submissionStatusUpdateRequest(String _id, String _comment) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", _id)
                .add("_checked_date", TimeUtils.now())
                .add("_comment", _comment)
                .build();

        return new Request.Builder().url(PathHandler.SUBMISSIONS_PATH).patch(requestBody).build();
    }


    /*
   ------------------------------------------------------------------------------------------------
   ------------------------------------------------------------------------------------------------
   Requests for Notices
    */

    // GET REQUEST
    public static final Request GET_REQUEST_NOTICES = new Request.Builder().url(PathHandler.NOTICES_PATH).get().build();

    // GET REQUEST
    public static Request noticeGetRequestById(@NonNull String _id) {
        return new Request.Builder().url(PathHandler.NOTICES_PATH + _id).get().build();
    }

    // POST REQUEST
    public static Request noticePostRequest(@NonNull Notice notice) {
        RequestBody requestBody = new FormBody.Builder()
                .add("_id", notice.get_id())
                .add("_title", notice.get_title())
                .add("_desc", notice.get_desc())
                .add("_school", notice.get_school_ref())
                .add("_classes", ArrayUtils.toString(notice.get_classes()))
                .add("_notifier", notice.get_teacher_ref())
                .add("_notified_on", notice.get_notified_on())
                .build();

        return new Request.Builder().url(PathHandler.STUDENTS_PATH).post(requestBody).build();
    }


}
