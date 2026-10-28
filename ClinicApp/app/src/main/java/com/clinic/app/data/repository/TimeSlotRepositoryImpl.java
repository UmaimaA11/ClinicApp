package com.clinic.app.data.repository;

import android.app.Application;
import com.clinic.app.data.dao.TimeSlotDao;
import com.clinic.app.data.db.ClinicDatabase;
import com.clinic.app.data.entities.TimeSlot;
import com.clinic.app.domain.repository.TimeSlotRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TimeSlotRepositoryImpl implements TimeSlotRepository {

    private final TimeSlotDao timeSlotDao;
    private final ExecutorService executorService;

    public TimeSlotRepositoryImpl(Application application) {
        ClinicDatabase db = ClinicDatabase.getDatabase(application);
        timeSlotDao = db.timeSlotDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void insert(TimeSlot timeSlot) {
        executorService.execute(() -> timeSlotDao.insert(timeSlot));
    }

    @Override
    public void update(TimeSlot timeSlot) {
        executorService.execute(() -> timeSlotDao.update(timeSlot));
    }

    @Override
    public void delete(TimeSlot timeSlot) {
        executorService.execute(() -> timeSlotDao.delete(timeSlot));
    }

    @Override
    public List<TimeSlot> getTimeSlotsForDoctor(int doctorId) {
        Future<List<TimeSlot>> future = executorService.submit(() -> timeSlotDao.getTimeSlotsForDoctor(doctorId));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public TimeSlot getTimeSlotById(long timeSlotId) {
        Future<TimeSlot> future = executorService.submit(() -> timeSlotDao.getTimeSlotById(timeSlotId));
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<TimeSlot> getAllTimeSlots() {
        Future<List<TimeSlot>> future = executorService.submit(timeSlotDao::getAllTimeSlots);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
