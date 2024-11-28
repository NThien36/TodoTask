package com.example.todolist.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.adapters.TaskAdapter;
import com.example.todolist.models.Task;
import com.example.todolist.utils.DateUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Views
    private FloatingActionButton addTaskBtn;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private TextView title;
    private String selectedDate;
    private ImageView ivNoData;

    // All tasks list
    private List<Task> taskList;

    // Filtered tasks list
    private List<Task> taskListFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        // Get today's date and filter tasks by today's date
        String todayDate = DateUtils.getCurrentDate();
        selectedDate = todayDate;
        filterTasksByDate(todayDate);  // Call filter to show today's tasks

        addTaskBtn.setOnClickListener(v -> {
            showUpsertTaskDialog(null);
        });

    }

    // Initialize views
    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addTaskBtn = findViewById(R.id.addTaskBtn);
        title = findViewById(R.id.tvTitle);
        ivNoData = findViewById(R.id.ivNoData);

        taskList = new ArrayList<>();
        taskListFilter = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add sample tasks to the list
        taskList.add(new Task("Task 1", "Description 1", false, "15/11/2024"));
        taskList.add(new Task("Task 2", "Description 2", false, "15/11/2024"));
        taskList.add(new Task("Task 3", "Description 3", false, "15/11/2024"));
        taskList.add(new Task("Task 4", "Description 4", false, "15/11/2024"));
        taskList.add(new Task("Task 5", "Description 5", true, "20/11/2024"));
        taskList.add(new Task("Task 6", "Description 6", true, "20/11/2024"));
        taskList.add(new Task("Task 7", "Description 7", false, "20/11/2024"));
        taskList.add(new Task("Task 8", "Description 8", false, "20/11/2024"));

        // Initialize the adapter and set it to the RecyclerView
        taskAdapter = new TaskAdapter(this, taskListFilter, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskUpdate(Task task, int position) {
                showUpsertTaskDialog(task);
            }

            @Override
            public void onTaskDelete(Task task, int position) {
                showDeleteConfirmationDialog(task, position);
            }
        });
        recyclerView.setAdapter(taskAdapter);
    }

    private void showUpsertTaskDialog(Task taskToEdit) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_upsert_task, null);
        dialogBuilder.setView(dialogView);

        // Set up the dialog view elements
        EditText etTaskTitle = dialogView.findViewById(R.id.etTaskTitle);
        EditText etTaskDescription = dialogView.findViewById(R.id.etTaskDescription);
        EditText etTaskDate = dialogView.findViewById(R.id.etTaskDate);
        Button btnSaveTask = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);

        // Set fields and texts for button, title
        if (taskToEdit != null) {
            etTaskTitle.setText(taskToEdit.getName());
            etTaskDescription.setText(taskToEdit.getDescription());
            etTaskDate.setText(taskToEdit.getDueDate());
            btnSaveTask.setText("UPDATE");
            tvDialogTitle.setText("Update Task");
        } else {
            btnSaveTask.setText("ADD");
            tvDialogTitle.setText("Add New Task");
        }

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.show();

        // Show DatePickerDialog when the EditText is clicked
        etTaskDate.setOnClickListener(v -> showTaskDatePickerDialog(etTaskDate));

        // Handle cancel button click
        btnCancel.setOnClickListener(v -> alertDialog.dismiss());

        // Handle save button click
        btnSaveTask.setOnClickListener(v -> {
            String taskTitle = etTaskTitle.getText().toString();
            String taskDescription = etTaskDescription.getText().toString();
            String taskDate = etTaskDate.getText().toString();

            // Validate input
            if (!taskTitle.isEmpty() && !taskDate.isEmpty() && !taskDescription.isEmpty()) {
                if (taskToEdit == null) {
                    // Add new task
                    Task newTask = new Task(taskTitle, taskDescription, false, taskDate);
                    taskList.add(newTask);
                    // Reset the current task list to show the new task
                    if (taskDate.equals(selectedDate)) {
                        resetTaskList();
                    }
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Edit existing task
                    taskToEdit.setName(taskTitle);
                    taskToEdit.setDescription(taskDescription);
                    taskToEdit.setDueDate(taskDate);

                    // Update the task in the list
                    int taskIndex = taskList.indexOf(taskToEdit);
                    if (taskIndex != -1) {
                        // Update the task in the list
                        taskList.set(taskIndex, taskToEdit);
                        // Reset the task list to show the updated task
                        resetTaskList();
                        Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
                    }
                }
                alertDialog.dismiss();
            } else {
                if (taskTitle.isEmpty()) {
                    etTaskTitle.setError("Task title is required");
                }
                if (taskDate.isEmpty()) {
                    etTaskDate.setError("Task date is required");
                }
                if (taskDescription.isEmpty()) {
                    etTaskDescription.setError("Task description is required");
                }
            }

        });
    }

    private void showDeleteConfirmationDialog(Task task, int position) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Remove the task from the main list
                    taskList.remove(task);

                    // Remove the task from the filtered list and refresh the display
                    if (taskListFilter.contains(task)) {
                        taskListFilter.remove(task);
                        resetTaskList();
                    }

                    // Notify the adapter and check if the filtered list is empty
                    taskAdapter.notifyItemRemoved(position);

                    if (taskListFilter.isEmpty()) {
                        ivNoData.setVisibility(View.VISIBLE);
                    } else {
                        ivNoData.setVisibility(View.GONE);
                    }

                    Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void filterTasksByDate(String selectedDate) {
        String todayDate = DateUtils.getCurrentDate(); // Today's date
        String tomorrowDate = DateUtils.getTomorrowDate(); // Tomorrow's date

        // Set the title based on the selected date
        if (selectedDate.equals(todayDate)) {
            title.setText("Today Tasks");
        } else if (selectedDate.equals(tomorrowDate)) {
            title.setText("Tomorrow Tasks");
        } else {
            title.setText(selectedDate + " Tasks");
        }

        // Filter tasks based on the selected date
        resetTaskList();
    }

    private void resetTaskList() {
        // Clear the filtered list and add all tasks
        taskListFilter.clear();
        for (Task task : taskList) {
            if (task.getDueDate().equals(selectedDate)) {
                taskListFilter.add(task);
            }
        }

        // Check if the filtered list is empty and update the visibility of the ImageView
        if (taskListFilter.isEmpty()) {
            ivNoData.setVisibility(View.VISIBLE);
        } else {
            taskListFilter.sort((task1, task2) -> Boolean.compare(task1.isCompleted(), task2.isCompleted()));
            ivNoData.setVisibility(View.GONE);
        }

        // Notify the adapter that the data set has changed
        taskAdapter.updateTaskList(taskListFilter);
    }

    private void showTaskDatePickerDialog(EditText etTaskDate) {
        showDatePickerDialog(etTaskDate, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
            etTaskDate.setText(selectedDate);
        });
    }

    private void showDatePickerDialog(EditText etTaskDate, DatePickerDialog.OnDateSetListener onDateSetListener) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                R.style.BlackDatePickerDialogTheme,  // Apply the custom theme here
                onDateSetListener,
                year, month, day);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.calender) {
            showDatePickerDialog(null, (view, selectedYear, selectedMonth, selectedDay) -> {
                String currentSelectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                // Update the selected date and filter tasks based on the selected date
                selectedDate = currentSelectedDate;
                filterTasksByDate(currentSelectedDate);
            });
        }
        return super.onOptionsItemSelected(item);
    }
}