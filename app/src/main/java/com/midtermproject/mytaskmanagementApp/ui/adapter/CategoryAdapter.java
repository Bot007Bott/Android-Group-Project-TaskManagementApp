package com.midtermproject.mytaskmanagementApp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.midtermproject.mytaskmanagementApp.R;
import com.midtermproject.mytaskmanagementApp.data.model.Category;
import com.midtermproject.mytaskmanagementApp.data.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private Map<Integer, Integer> taskCountMap = new HashMap<>(); // categoryId -> taskCount
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Category category);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    // NEW METHOD: Update task counts
    public void updateTaskCounts(List<Task> allTasks) {
        taskCountMap.clear();

        // Count tasks for each category
        for (Task task : allTasks) {
            int categoryId = task.getCategoryId();
            taskCountMap.put(categoryId, taskCountMap.getOrDefault(categoryId, 0) + 1);
        }

        notifyDataSetChanged(); // Refresh to show updated counts
    }

    // NEW METHOD: Get task count for a category
    public int getTaskCountForCategory(int categoryId) {
        return taskCountMap.getOrDefault(categoryId, 0);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName, tvCategoryDescription, tvTaskCount;
        private Button btnDelete;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvCategoryDescription = itemView.findViewById(R.id.tv_category_description);
            tvTaskCount = itemView.findViewById(R.id.tv_task_count);
            btnDelete = itemView.findViewById(R.id.btn_delete_category);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(categories.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && deleteListener != null) {
                    deleteListener.onDeleteClick(categories.get(position));
                }
            });
        }

        void bind(Category category) {
            tvCategoryName.setText(category.getCategoryName());

            if (category.getDescription() != null && !category.getDescription().isEmpty()) {
                tvCategoryDescription.setText(category.getDescription());
                tvCategoryDescription.setVisibility(View.VISIBLE);
            } else {
                tvCategoryDescription.setVisibility(View.GONE);
            }

            // Get task count for this category
            int taskCount = getTaskCountForCategory(category.getCategoryId());

            // Format the task count text
            if (taskCount == 0) {
                tvTaskCount.setText("No tasks");
                tvTaskCount.setTextColor(itemView.getContext().getResources()
                        .getColor(android.R.color.darker_gray));
            } else if (taskCount == 1) {
                tvTaskCount.setText("1 task");
                tvTaskCount.setTextColor(itemView.getContext().getResources()
                        .getColor(R.color.purple_500));
            } else {
                tvTaskCount.setText(taskCount + " tasks");
                tvTaskCount.setTextColor(itemView.getContext().getResources()
                        .getColor(R.color.purple_500));
            }

            // Show warning if category has tasks (for delete button)
            if (taskCount > 0) {
                btnDelete.setText("Delete (" + taskCount + ")");
            } else {
                btnDelete.setText("Delete");
            }
        }
    }
}