package com.midtermproject.mytaskmanagementApp.ui.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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
    private Map<Integer, Integer> taskCountMap = new HashMap<>();
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;
    private OnEditClickListener editListener;

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Category category);
    }

    public interface OnEditClickListener {
        void onEditClick(Category category);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editListener = listener;
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

    public Category getCategoryAt(int position) {
        return categories.get(position);
    }

    public void updateTaskCounts(List<Task> allTasks) {
        taskCountMap.clear();
        for (Task task : allTasks) {
            int categoryId = task.getCategoryId();
            taskCountMap.put(categoryId, taskCountMap.getOrDefault(categoryId, 0) + 1);
        }
        notifyDataSetChanged();
    }

    public int getTaskCountForCategory(int categoryId) {
        return taskCountMap.getOrDefault(categoryId, 0);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName, tvCategoryDescription, tvTaskCount, tvCreatedDate;
        public View cardForeground;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvCategoryDescription = itemView.findViewById(R.id.tv_category_description);
            tvTaskCount = itemView.findViewById(R.id.tv_task_count);
            cardForeground = itemView.findViewById(R.id.card_foreground);
            tvCreatedDate = itemView.findViewById(R.id.tv_created_date);

            // Click to view tasks
            cardForeground.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(categories.get(position));
                }
            });

            // Long press for edit/delete menu
            cardForeground.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(itemView.getContext(), cardForeground, Gravity.END);
                    popup.getMenuInflater().inflate(R.menu.menu_category_item, popup.getMenu());

                    popup.setOnMenuItemClickListener(item -> {
                        int id = item.getItemId();
                        if (id == R.id.action_edit) {
                            if (editListener != null) editListener.onEditClick(categories.get(position));
                            return true;
                        } else if (id == R.id.action_delete) {
                            if (deleteListener != null) deleteListener.onDeleteClick(categories.get(position));
                            return true;
                        }
                        return false;
                    });
                    popup.show();
                }
                return true;
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

            int taskCount = getTaskCountForCategory(category.getCategoryId());

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

            if (category.getCreatedDate() != null) {
                tvCreatedDate.setText("Created: " + category.getCreatedDate());
                tvCreatedDate.setVisibility(View.VISIBLE);
            } else {
                tvCreatedDate.setVisibility(View.GONE);
            }
        }
    }
}