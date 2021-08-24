package com.itheamc.hamroclassroom_teachers.handlers;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;

import com.itheamc.hamroclassroom_teachers.callbacks.QueryCallbacks;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.models.School;
import com.itheamc.hamroclassroom_teachers.models.Student;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.models.Submission;
import com.itheamc.hamroclassroom_teachers.models.User;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class QueryHandler {
    private static final String TAG = "QueryHandler";
    private final Handler handler;
    private final ExecutorService executorService;
    private final QueryCallbacks callbacks;
    private final OkHttpClient client;

    // Constructor
    private QueryHandler(@NonNull QueryCallbacks callbacks) {
        this.handler = HandlerCompat.createAsync(Looper.getMainLooper());
        this.executorService = Executors.newFixedThreadPool(4);
        this.callbacks = callbacks;
        this.client = new OkHttpClient();
    }

    // Getter for QueryHandler Instance
    public static QueryHandler getInstance(@NonNull QueryCallbacks queryCallbacks) {
        return new QueryHandler(queryCallbacks);
    }

    /**
     * Function to get user info from the database
     * --------------------------------------------------------------------------------------
     */
    public void getUser(String userId) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.teacherGetRequestById(userId)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            User user = JsonHandler.getUser(jsonObject);
                            notifySuccess(user,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to store user in the database
     * --------------------------------------------------------------------------------------
     */
    public void storeUser(User user) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.teacherPostRequest(user)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }

    /**
     * Function to update user in the database
     * --------------------------------------------------------------------------------------
     */
    public void updateUser(String _uid, Map<String, Object> data) {

    }


    /**
     * Function to get subjects list from the cloud database
     * --------------------------------------------------------------------------------------
     */
    public void getSubjects(String teacherId) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.subjectGetRequestByTeacherId(teacherId)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<Subject> subjects = JsonHandler.getSubjects(jsonObject);
                            notifySuccess(null,
                                    null,
                                    null,
                                    subjects,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }

    /**
     * Function to add subject in the database
     * --------------------------------------------------------------------------------------
     */
    public void addSubject(Subject subject) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.subjectPostRequest(subject)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }
                        notifyFailure(new Exception("Unable to store"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }

    /**
     * Function to update subject in the database
     * --------------------------------------------------------------------------------------
     */
    public void updateSubject(Subject subject) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.subjectPatchRequest(subject)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }
                        notifyFailure(new Exception("Unable to update"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }

    /**
     * Function to update Meeting app link in subject
     * --------------------------------------------------------------------------------------
     */
    public void updateLink(String _sid, String _link) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.linkPatchRequest(_sid, _link)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }
                        notifyFailure(new Exception("Unable to update"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to delete subject
     * --------------------------------------------------------------------------------------
     */
    public void deleteSubject(String _id) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.subjectDeleteRequest(_id)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }
                        notifyFailure(new Exception("Unable to update"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to get assignments list from the cloud database
     * --------------------------------------------------------------------------------------
     */
    public void getAssignments(String subject_ref) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.assignmentGetRequestBySubjectId(subject_ref)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        NotifyUtils.logDebug(TAG, jsonObject.toString());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<Assignment> assignments = JsonHandler.getAssignments(jsonObject);
                            notifySuccess(null,
                                    null,
                                    null,
                                    null,
                                    assignments,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to add assignment in the database
     * --------------------------------------------------------------------------------------
     */
    public void addAssignment(Assignment assignment) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.assignmentPostRequest(assignment)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }
                        notifyFailure(new Exception("Unable to add"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to update assignment title
     * --------------------------------------------------------------------------------------
     */
    public void updateAssignmentTitle(String assignmentId, String updatedTitle) {
    }


    /**
     * Function to delete assignment
     * --------------------------------------------------------------------------------------
     */
    public void deleteAssignment(String _id) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.assignmentDeleteRequest(_id)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }
                        notifyFailure(new Exception("Unable to update"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }



    /**
     * Function to get submissions list from the cloud database
     * --------------------------------------------------------------------------------------
     */
    public void getSubmissions(String assignmentId) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.submissionGetRequestByAssignmentId(assignmentId)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<Submission> submissions = JsonHandler.getSubmissions(jsonObject);
                            notifySuccess(null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    submissions,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }



    /**
     * Function to update submission in the database
     * --------------------------------------------------------------------------------------
     */
    public void updateSubmission(String submissionId, String _comment) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.submissionStatusUpdateRequest(submissionId, _comment)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }
                        notifyFailure(new Exception("Unable to update"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }

    /**
     * Function to get Student from the database
     * --------------------------------------------------------------------------------------
     */
    public void getStudents(String subjectId) {
    }

    /**
     * Function to add notice to the cloud database
     * --------------------------------------------------------------------------------------
     */
    public void addNotice(Notice notice) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.noticePostRequest(notice)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }
                        notifyFailure(new Exception("Unable to add"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to get notice list from the cloud database
     * --------------------------------------------------------------------------------------
     */
    public void getNotices(String teacher_ref) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.noticeGetRequestById(teacher_ref)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<Notice> notices = JsonHandler.getNotices(jsonObject);
                            notifySuccess(null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    notices);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }



    /**
     * Function to delete subject
     * --------------------------------------------------------------------------------------
     */
    public void deleteNotice(String _id) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.noticeDeleteRequest(_id)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.getString("message").equals("success")) {
                                notifySuccess(jsonObject.getString("message"));
                            } else {
                                notifyFailure(new Exception(jsonObject.getString("message")));
                            }
                            return;
                        }
                        notifyFailure(new Exception("Unable to update"));
                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }



    /**
     * Function to get schools list from the cloud database
     * --------------------------------------------------------------------------------------
     */
    public void getSchools() {
        executorService.execute(() -> {
            client.newCall(RequestHandler.GET_REQUEST_SCHOOLS).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            List<School> schools = JsonHandler.getSchools(jsonObject);
                            notifySuccess(null,
                                    schools,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }

    /**
     * Function to get schools list from the cloud database
     * --------------------------------------------------------------------------------------
     */
    public void getSchool(String _schoolId) {
        executorService.execute(() -> {
            client.newCall(RequestHandler.schoolGetRequestById(_schoolId)).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    notifyFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (response.isSuccessful()) {
                            if (jsonObject.has("message")) {
                                notifySuccess(jsonObject.getString("message"));
                                return;
                            }

                            School school = JsonHandler.getSchool(jsonObject);
                            List<School> schools = new ArrayList<>();
                            schools.add(school);
                            notifySuccess(null,
                                    schools,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null);
                            return;
                        }

                        notifyFailure(new Exception(jsonObject.getString("message")));

                    } catch (Exception e) {
                        notifyFailure(e);
                    }
                }
            });
        });
    }


    /**
     * Function to notify whether getUser() is success or failure
     * --------------------------------------------------------------------------------------
     */
    private void notifySuccess(User user,
                                 List<School> schools,
                                 List<Student> students,
                                 List<Subject> subjects,
                                 List<Assignment> assignments,
                                 List<Submission> submissions,
                                 List<Notice> notices) {
        handler.post(() -> {
            callbacks.onQuerySuccess(user, schools, students, subjects, assignments, submissions, notices);
        });
    }

    private void notifySuccess(String message) {
        handler.post(() -> {
            callbacks.onQuerySuccess(message);
        });
    }

    private void notifyFailure(Exception e) {
        handler.post(() -> {
            callbacks.onQueryFailure(e);
        });
    }
}
