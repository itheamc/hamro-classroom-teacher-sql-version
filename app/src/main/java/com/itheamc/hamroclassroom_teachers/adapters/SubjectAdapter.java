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
import com.itheamc.hamroclassroom_teachers.callbacks.SubjectCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.SubjectViewBinding;
import com.itheamc.hamroclassroom_teachers.models.Subject;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;

import org.jetbrains.annotations.NotNull;

import static com.itheamc.hamroclassroom_teachers.models.Subject.subjectItemCallback;

public class SubjectAdapter extends ListAdapter<Subject, SubjectAdapter.SubjectViewHolder> {
    private static final String TAG = "SubjectAdapter";
    private final SubjectCallbacks subjectViewCallbacks;

    public SubjectAdapter(@NonNull @NotNull SubjectCallbacks callbacks) {
        super(subjectItemCallback);
        this.subjectViewCallbacks = callbacks;
    }

    @NonNull
    @NotNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SubjectViewBinding viewBinding = SubjectViewBinding.inflate(inflater, parent, false);
        return new SubjectViewHolder(viewBinding, subjectViewCallbacks);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SubjectViewHolder holder, int position) {
        Subject subject = getItem(position);
        holder.viewBinding.setSubject(subject);
    }

    protected static class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private final SubjectViewBinding viewBinding;
        private final SubjectCallbacks callbacks;

        public SubjectViewHolder(@NonNull @NotNull SubjectViewBinding viewBinding, @NonNull SubjectCallbacks callbacks) {
            super(viewBinding.getRoot());
            this.viewBinding = viewBinding;
            this.callbacks = callbacks;
            this.viewBinding.addLinkButton.setOnClickListener(this);
            this.viewBinding.assignmentsButton.setOnClickListener(this);
            this.viewBinding.mainCardView.setOnClickListener(this);
            this.viewBinding.menuBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int _id = v.getId();
            if (_id == viewBinding.addLinkButton.getId()) callbacks.onAddLinkClick(getAdapterPosition());
            else if (_id == viewBinding.assignmentsButton.getId()) callbacks.onAssignmentsClick(getAdapterPosition());
            else if (_id == viewBinding.mainCardView.getId()) callbacks.onClick(getAdapterPosition());
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
