package com.clinic.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.clinic.app.R;
import com.clinic.app.data.entities.User;
import com.clinic.app.data.entities.UserRole;
import com.clinic.app.data.repository.UserRepositoryImpl;
import com.clinic.app.domain.usecase.admin.CreateUserByAdminUseCase;
import com.clinic.app.domain.usecase.admin.DeleteUserUseCase;
import com.clinic.app.domain.usecase.admin.GetAllUsersUseCase;
import com.clinic.app.domain.usecase.admin.UpdateUserUseCase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private UserAdapter adapter;
    private GetAllUsersUseCase getAllUsersUseCase;
    private DeleteUserUseCase deleteUserUseCase;
    private CreateUserByAdminUseCase createUserUseCase;
    private UpdateUserUseCase updateUserUseCase;
    
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        UserRepositoryImpl userRepository = new UserRepositoryImpl(getApplication());
        getAllUsersUseCase = new GetAllUsersUseCase(userRepository);
        deleteUserUseCase = new DeleteUserUseCase(userRepository);
        createUserUseCase = new CreateUserByAdminUseCase(userRepository);
        updateUserUseCase = new UpdateUserUseCase(userRepository);

        rvUsers = findViewById(R.id.rv_users);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(new ArrayList<>(), this::onDeleteUser, this::showAddEditDialog);
        rvUsers.setAdapter(adapter);

        FloatingActionButton fabAddUser = findViewById(R.id.fab_add_user);
        fabAddUser.setOnClickListener(v -> showAddEditDialog(null));

        loadUsers();
    }

    private void loadUsers() {
        executor.execute(() -> {
            List<User> users = getAllUsersUseCase.execute();
            handler.post(() -> adapter.updateUsers(users));
        });
    }

    private void onDeleteUser(User user) {
        new AlertDialog.Builder(this)
            .setTitle("Supprimer l'utilisateur")
            .setMessage("Voulez-vous vraiment supprimer " + user.getName() + " ?")
            .setPositiveButton("Supprimer", (dialog, which) -> {
                executor.execute(() -> {
                    deleteUserUseCase.execute(user);
                    handler.post(() -> {
                        Toast.makeText(this, "Utilisateur supprimé", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    });
                });
            })
            .setNegativeButton("Annuler", null)
            .show();
    }

    private void showAddEditDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_user, null);
        builder.setView(view);

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        EditText etName = view.findViewById(R.id.et_name);
        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etPhone = view.findViewById(R.id.et_phone);
        EditText etPassword = view.findViewById(R.id.et_password);
        Spinner spinnerRole = view.findViewById(R.id.spinner_role);
        Button btnSave = view.findViewById(R.id.btn_save);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        ArrayAdapter<UserRole> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, UserRole.values());
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        final AlertDialog dialog = builder.create();

        if (user != null) {
            tvTitle.setText("Modifier l'Utilisateur");
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPhone.setText(String.valueOf(user.getPhone()));
            etPassword.setText(user.getPassword());
            spinnerRole.setSelection(roleAdapter.getPosition(user.getRole()));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phoneStr = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString();
            UserRole role = (UserRole) spinnerRole.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || phoneStr.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            int phone = Integer.parseInt(phoneStr);

            executor.execute(() -> {
                if (user == null) {
                    User newUser = new User(name, email, phone, password, role);
                    createUserUseCase.execute(newUser);
                } else {
                    user.setName(name);
                    user.setEmail(email);
                    user.setPhone(phone);
                    user.setPassword(password);
                    user.setRole(role);
                    updateUserUseCase.execute(user);
                }
                handler.post(() -> {
                    Toast.makeText(this, "Opération réussie", Toast.LENGTH_SHORT).show();
                    loadUsers();
                    dialog.dismiss();
                });
            });
        });

        dialog.show();
    }

    private static class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private List<User> users;
        private final OnDeleteListener deleteListener;
        private final OnEditListener editListener;

        interface OnDeleteListener { void onDelete(User user); }
        interface OnEditListener { void onEdit(User user); }

        UserAdapter(List<User> users, OnDeleteListener deleteListener, OnEditListener editListener) {
            this.users = users;
            this.deleteListener = deleteListener;
            this.editListener = editListener;
        }

        void updateUsers(List<User> newUsers) {
            this.users = newUsers;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = users.get(position);
            holder.tvName.setText(user.getName());
            holder.tvEmail.setText(user.getEmail());
            holder.tvRole.setText(user.getRole().toString());
            holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(user));
            holder.btnEdit.setOnClickListener(v -> editListener.onEdit(user));
        }

        @Override
        public int getItemCount() { return users.size(); }

        static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail, tvRole;
            ImageButton btnEdit, btnDelete;

            UserViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_user_name);
                tvEmail = itemView.findViewById(R.id.tv_user_email);
                tvRole = itemView.findViewById(R.id.tv_user_role);
                btnEdit = itemView.findViewById(R.id.btn_edit_user);
                btnDelete = itemView.findViewById(R.id.btn_delete_user);
            }
        }
    }
}
