package com.simats.triageai

import com.simats.triageai.models.BackendPatient
import com.simats.triageai.models.DashboardPriorityResponse
import com.simats.triageai.models.DoctorHistory
import com.simats.triageai.models.PatientRequest
import com.simats.triageai.models.PatientResponse
import com.simats.triageai.models.ParamedicWaitingCountResponse
import com.simats.triageai.models.Notification
import com.simats.triageai.models.AdminPendingCasesResponse
import com.simats.triageai.models.BackendVitals
import com.simats.triageai.models.PatientActionLog
import com.simats.triageai.models.NearbyDoctorResponse
import com.simats.triageai.models.PendingAssignmentResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==========================
    // AUTH
    // ==========================

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<GenericResponse>

    @POST("forgot-password/send-password")
    fun forgotPassword(@Body request: ForgotPasswordEmailRequest): Call<GenericResponse>

    // ==========================
    // PATIENTS
    // ==========================

    @POST("patients/add")
    suspend fun addPatient(
        @Query("admin_id") adminId: Int,
        @Body request: PatientRequest
    ): Response<PatientResponse>

    @GET("patients")
    fun getPatients(): Call<List<BackendPatient>>

    @GET("patients/{id}")
    suspend fun getPatientProfile(@Path("id") patientId: Int): Response<BackendPatient>

    // ==========================
    // VITALS & DASHBOARD
    // ==========================

    @POST("vitals/add")
    suspend fun addVitals(
        @Query("patient_id") patientId: Int,
        @Query("paramedic_id") paramedicId: Int,
        @Body request: AddVitalsRequest
    ): Response<GenericResponse>

    @GET("vitals/latest/{patient_id}")
    suspend fun getLatestVitals(@Path("patient_id") patientId: Int): Response<LatestVitalsResponse>


    // ==========================
    // DOCTOR ACTIONS
    // ==========================

    @POST("doctor/assign")
    suspend fun assignPatient(
        @Query("doctor_id") doctorId: Int,
        @Query("patient_id") patientId: Int
    ): Response<GenericResponse>

    @POST("doctor/administer-medication")
    suspend fun administerMedication(
        @Query("doctor_id") doctorId: Int,
        @Query("paramedic_id") paramedicId: Int,
        @Query("patient_id") patientId: Int,
        @Query("notes") notes: String
    ): Response<GenericResponse>

    @POST("doctor/begin-assessment")
    suspend fun beginAssessment(
        @Query("doctor_id") doctorId: Int,
        @Query("paramedic_id") paramedicId: Int,
        @Query("patient_id") patientId: Int,
        @Query("db") db: String? = null // Backend doesn't really need db but it was in signature
    ): Response<GenericResponse>

    @POST("notifications/send")
    suspend fun sendNotification(
        @Query("sender_id") sender_id: Int,
        @Query("receiver_id") receiver_id: Int,
        @Query("message") message: String,
        @Query("type") type: String,
        @Query("patient_id") patient_id: Int? = null
    ): Response<GenericResponse>

    @POST("doctor/take-action")
    suspend fun takeAction(
        @Query("doctor_id") doctorId: Int,
        @Query("patient_id") patientId: Int,
        @Query("action_type") actionType: String,
        @Query("notes") notes: String? = null
    ): Response<GenericResponse>

    @POST("doctor/toggle-status")
    suspend fun toggleDoctorStatus(
        @Query("doctor_id") doctorId: Int
    ): Response<ToggleStatusResponse>

    @POST("doctor/complete")
    fun completeCase(
        @Body request: CompleteCaseRequest
    ): Call<GenericResponse>

    @GET("doctor/my-patients/{doctor_id}")
    fun getMyPatients(@Path("doctor_id") doctorId: Int): Call<List<BackendPatient>>

    // ==========================
    // NOTIFICATIONS
    // ==========================

    @GET("notifications/{user_id}")
    suspend fun getNotifications(@Path("user_id") userId: Int): Response<List<Notification>>

    @POST("doctor/accept-case")
    suspend fun acceptCase(
        @Query("notification_id") notificationId: Int,
        @Query("doctor_id") doctorId: Int
    ): Response<GenericResponse>

    @POST("doctor/reject-case")
    suspend fun rejectCase(
        @Query("notification_id") notificationId: Int,
        @Query("doctor_id") doctorId: Int
    ): Response<GenericResponse>

    @PUT("notifications/read/{id}")
    suspend fun markNotificationAsRead(@Path("id") notificationId: Int): Response<GenericResponse>

    @POST("notifications/mark-all-read/{user_id}")
    suspend fun markAllNotificationsAsRead(@Path("user_id") userId: Int): Response<GenericResponse>

    @GET("doctor/pending-assignment/{doctor_id}")
    suspend fun getPendingAssignment(@Path("doctor_id") doctorId: Int): Response<PendingAssignmentResponse>

    // ==========================
    // USER PROFILE
    // ==========================

    @GET("profile/{user_id}")
    fun getUserProfile(@Path("user_id") userId: Int): Call<UserProfileResponse>

    @PUT("profile/{user_id}")
    fun updateProfile(
        @Path("user_id") userId: Int,
        @Body request: UpdateProfileRequest
    ): Call<GenericResponse>

    @Multipart
    @POST("profile/{user_id}/upload-photo")
    fun uploadPhoto(
        @Path("user_id") userId: Int,
        @Part file: MultipartBody.Part
    ): Call<PhotoUploadResponse>

    @PUT("profile/{user_id}/change-password")
    fun changePassword(
        @Path("user_id") userId: Int,
        @Body request: ChangePasswordRequest
    ): Call<GenericResponse>

    // ==========================
    // ADMIN
    // ==========================

    @POST("admin/add-user")
    fun adminAddUser(
        @Query("admin_id") adminId: Int,
        @Body request: RegisterRequest
    ): Call<GenericResponse>

    @GET("admin/staff/{admin_id}")
    suspend fun getAllStaff(@Path("admin_id") adminId: Int): Response<List<UserProfileResponse>>

    @GET("admin/internal-doctors")
    suspend fun getInternalDoctors(@Query("admin_id") adminId: Int): Response<List<UserProfileResponse>>

    @GET("admin/pending-cases/{admin_id}")
    suspend fun getPendingCases(@Path("admin_id") adminId: Int): Response<AdminPendingCasesResponse>

    @DELETE("admin/delete-user/{admin_id}/{user_id}")
    suspend fun deleteStaff(
        @Path("admin_id") adminId: Int,
        @Path("user_id") userId: Int
    ): Response<GenericResponse>

    @PUT("admin/update-user/{admin_id}/{user_id}")
    suspend fun updateStaff(
        @Path("admin_id") adminId: Int,
        @Path("user_id") userId: Int,
        @Body request: UpdateStaffRequest
    ): Response<GenericResponse>

    @GET("admin/dashboard-stats/{admin_id}")
    suspend fun getAdminDashboardStats(@Path("admin_id") adminId: Int): Response<AdminDashboardStats>

    @GET("admin/search-nearby-doctors")
    suspend fun searchNearbyDoctors(
        @Query("admin_id") adminId: Int,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius_km") radiusKm: Double = 10.0
    ): Response<NearbyDoctorResponse>

    @POST("admin/assign-case")
    suspend fun assignCase(
        @Query("admin_id") adminId: Int,
        @Query("patient_id") patientId: Int,
        @Query("doctor_id") doctorId: Int
    ): Response<GenericResponse>

    // ==========================
    // DASHBOARD/PRIORITY
    // ==========================
    @GET("dashboard/priority")
    suspend fun getPriorityCases(
        @Query("doctor_id") doctorId: Int
    ): Response<DashboardPriorityResponse>

    // ==========================
    // DOCTOR/HISTORY
    // ==========================
    @GET("doctor/history/{doctor_id}")
    suspend fun getDoctorHistory(
        @Path("doctor_id") doctorId: Int
    ): Response<List<DoctorHistory>>

    // ==========================
    // PARAMEDIC
    // ==========================

    @GET("paramedic/my-patients/{paramedic_id}")
    fun getParamedicPatients(@Path("paramedic_id") paramedicId: Int): Call<List<BackendPatient>>

    @GET("paramedic/waiting-count/{paramedic_id}")
    fun getParamedicWaitingCount(@Path("paramedic_id") paramedicId: Int): Call<ParamedicWaitingCountResponse>

    @GET("patients/{patient_id}/vitals-history")
    suspend fun getVitalsHistory(@Path("patient_id") patientId: Int): Response<List<BackendVitals>>

    @GET("patients/{patient_id}/timeline")
    suspend fun getPatientTimeline(@Path("patient_id") patientId: Int): Response<List<PatientActionLog>>

    @POST("doctor/update-location")
    suspend fun updateLocation(
        @Query("doctor_id") doctorId: Int,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): Response<GenericResponse>
}
