package com.clinic.app.domain.model;

public class DashboardStats {
    private final int patientCount;
    private final int doctorCount;

    public DashboardStats(int patientCount, int doctorCount) {
        this.patientCount = patientCount;
        this.doctorCount = doctorCount;
    }

    public int getPatientCount() {
        return patientCount;
    }

    public int getDoctorCount() {
        return doctorCount;
    }
}
