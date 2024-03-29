package com.itheamc.hamroclassroom_teachers.adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.itheamc.hamroclassroom_teachers.R;
import com.itheamc.hamroclassroom_teachers.callbacks.AssignmentCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.AssignmentViewBinding;
import com.itheamc.hamroclassroom_teachers.models.Assignment;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import static com.itheamc.hamroclassroom_teachers.models.Assignment.assignmentItemCallback;

import java.text.DateFormat;

public class AssignmentAdapter extends ListAdapter<Assignment, AssignmentAdapter.AssignmentViewHolder> {
    private static final String TAG = "AssignmentAdapter";
    private final AssignmentCallbacks callbacks;

    public AssignmentAdapter(@NonNull AssignmentCallbacks callbacks) {
        super(assignmentItemCallback);
        this.callbacks = callbacks;
    }

    @NonNull
    @NotNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AssignmentViewBinding viewBinding = AssignmentViewBinding.inflate(inflater, parent, false);
        return new AssignmentViewHolder(viewBinding, callbacks);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AssignmentViewHolder holder, int position) {
        Assignment assignment = getItem(position);
        holder.viewBinding.setAssignment(assignment);
        holder.viewBinding.setNumber(String.valueOf(position + 1));
        String formattedDate = DateFormat.getDateInstance().format(TimeUtils.toDate(assignment.get_assigned_date()));
        holder.viewBinding.setDate(formattedDate);
    }

    protected static class AssignmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private final AssignmentViewBinding viewBinding;
        private final AssignmentCallbacks callbacks;

        public AssignmentViewHolder(@NonNull @NotNull AssignmentViewBinding assignmentViewBinding, AssignmentCallbacks callbacks) {
            super(assignmentViewBinding.getRoot());
            this.callbacks = callbacks;
            this.viewBinding = assignmentViewBinding;
            this.viewBinding.assignmentCardView.setOnClickListener(this);
            this.viewBinding.submissionsButton.setOnClickListener(this);
            this.viewBinding.menuBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int _id = v.getId();
            if (_id == viewBinding.assignmentCardView.getId())
                callbacks.onClick(getAdapterPosition());
            else if (_id == viewBinding.submissionsButton.getId())
                callbacks.onSubmissionsClick(getAdapterPosition());
            else if (_id == viewBinding.menuBtn.getId()) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.assignment_menu);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true);
                }
                popupMenu.show();
            } else NotifyUtils.logDebug(TAG, "Unspecified view is clicked!!");
            
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.assignment_menu_edit) {
                callbacks.onEditClick(getAdapterPosition());
                return true;
            } else if (id == R.id.assignment_menu_delete) {
                callbacks.onDeleteClick(getAdapterPosition());
                return true;
            } else {
                return false;
            }
        }
    }
}
