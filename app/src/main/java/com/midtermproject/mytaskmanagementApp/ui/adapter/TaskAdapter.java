package com.midtermproject.mytaskmanagementApp.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.midtermproject.mytaskmanagementApp.R;
import com.midtermproject.mytaskmanagementApp.data.model.Task;
import com.midtermproject.mytaskmanagementApp.util.Constants;
import com.midtermproject.mytaskmanagementApp.util.DateTimeUtil;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private OnItemClickListener listener;
    private OnTaskCompletionListener completionListener;

    // Click listener interface
    public interface OnItemClickListener {
        void onItemClick(Task task);
    }

    // Completion listener interface
    public interface OnTaskCompletionListener {
        void onTaskCompleted(Task task, boolean isCompleted);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnTaskCompletionListener(OnTaskCompletionListener listener) {
        this.completionListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public Task getTaskAt(int position) {
        return tasks.get(position);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskName, tvDueDate, tvPriority;
        private CheckBox cbCompleted;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskName = itemView.findViewById(R.id.tv_task_name);
            tvDueDate = itemView.findViewById(R.id.tv_due_date);
            tvPriority = itemView.findViewById(R.id.tv_priority);
            cbCompleted = itemView.findViewById(R.id.cb_completed);

            // Click listener for entire task item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(tasks.get(position));
                }
            });

            // Checkbox listener
            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Task task = tasks.get(position);
                    task.setCompleted(isChecked);

                    // Update database via listener
                    if (completionListener != null) {
                        completionListener.onTaskCompleted(task, isChecked);
                    }
                }
            });
        }

        void bind(Task task) {
            // Remove listener temporarily to avoid triggering while setting
            cbCompleted.setOnCheckedChangeListener(null);

            tvTaskName.setText(task.getTaskName());
            tvDueDate.setText(DateTimeUtil.formatDateForDisplay(task.getDueDate()));
            tvPriority.setText(task.getPriority());
            cbCompleted.setChecked(task.isTaskCompleted());

            // Restore listener
            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Task currentTask = tasks.get(position);
                    currentTask.setCompleted(isChecked);

                    if (completionListener != null) {
                        completionListener.onTaskCompleted(currentTask, isChecked);
                    }
                }
            });

            // Set priority color
            if (task.getPriority().equals(Constants.PRIORITY_HIGH)) {
                tvPriority.setBackgroundColor(Color.RED);
                tvPriority.setTextColor(Color.WHITE);
            } else if (task.getPriority().equals(Constants.PRIORITY_MEDIUM)) {
                tvPriority.setBackgroundColor(Color.YELLOW);
                tvPriority.setTextColor(Color.BLACK);
            } else {
                tvPriority.setBackgroundColor(Color.GREEN);
                tvPriority.setTextColor(Color.BLACK);
            }

            // Strike-through text if completed
            if (task.isTaskCompleted()) {
                tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                tvDueDate.setPaintFlags(tvDueDate.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                tvDueDate.setPaintFlags(tvDueDate.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }
}