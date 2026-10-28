package com.clinic.app.data.repository;



import android.app.Application;

import com.clinic.app.data.dao.UserDao;

import com.clinic.app.data.db.ClinicDatabase;

import com.clinic.app.data.entities.User;

import com.clinic.app.data.entities.UserRole;

import com.clinic.app.domain.repository.UserRepository;



import java.util.List;

import java.util.concurrent.Callable;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import java.util.concurrent.Future;



public class UserRepositoryImpl implements UserRepository {



    private final UserDao userDao;

    private final ExecutorService executorService;



    public UserRepositoryImpl(Application application) {

        ClinicDatabase db = ClinicDatabase.getDatabase(application);

        userDao = db.userDao();

        executorService = Executors.newSingleThreadExecutor();

    }



    @Override

    public User getUserById(int userId) {

        Future<User> future = executorService.submit(() -> userDao.getUser(userId));

        try {

            return future.get();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }



    @Override

    public User findByEmail(String email) {

        Future<User> future = executorService.submit(() -> userDao.findByEmail(email));

        try {

            return future.get();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }



    @Override

    public List<User> getAllUsers() {

        Future<List<User>> future = executorService.submit(userDao::getAllUsers);

        try {

            return future.get();

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }



    @Override

    public void insert(User user) {

        executorService.execute(() -> userDao.insert(user));

    }



    @Override

    public void update(User user) {

        executorService.execute(() -> userDao.update(user));

    }



    @Override

    public void delete(User user) {

        executorService.execute(() -> userDao.delete(user));

    }



    @Override

    public int countUsersByRole(UserRole role) {

        Future<Integer> future = executorService.submit(() -> userDao.countUsersByRole(role));

        try {

            return future.get();

        } catch (Exception e) {

            e.printStackTrace();

            return 0;

        }

    }

}