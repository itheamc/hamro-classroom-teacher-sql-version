package com.itheamc.hamroclassroom_teachers.adapters;

import static com.itheamc.hamroclassroom_teachers.models.Material.materialItemCallback;

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
import com.itheamc.hamroclassroom_teachers.callbacks.MaterialCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.MaterialViewBinding;
import com.itheamc.hamroclassroom_teachers.models.Material;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.TimeUtils;

import java.text.DateFormat;

public class MaterialAdapter extends ListAdapter<Material, MaterialAdapter.MaterialViewHolder> {
    private static final String TAG = "MaterialAdapter";
    private final MaterialCallbacks callbacks;

    public MaterialAdapter(@NonNull MaterialCallbacks materialCallbacks) {
        super(materialItemCallback);
        this.callbacks = materialCallbacks;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MaterialViewBinding materialViewBinding = MaterialViewBinding.inflate(inflater, parent, false);
        return new MaterialViewHolder(materialViewBinding, callbacks);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        Material material = getItem(position);
        holder.viewBinding.setMaterial(material);
        holder.viewBinding.setNumber(String.valueOf(position + 1));
        String formattedDate = DateFormat.getDateInstance().format(TimeUtils.toDate(material.get_added_date()));
        holder.viewBinding.setDate(formattedDate);
    }

    public static class MaterialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private final MaterialCallbacks callbacks;
        private final MaterialViewBinding viewBinding;

        public MaterialViewHolder(@NonNull MaterialViewBinding materialViewBinding, @NonNull MaterialCallbacks materialCallbacks) {
            super(materialViewBinding.getRoot());
            this.callbacks = materialCallbacks;
            this.viewBinding = materialViewBinding;
            this.viewBinding.materialCardView.setOnClickListener(this);
            this.viewBinding.menuBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int _id = v.getId();
            if (_id == viewBinding.materialCardView.getId())
                callbacks.onClick(getAdapterPosition());
            else if (_id == viewBinding.menuBtn.getId()) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.material_menu);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true);
                }
                popupMenu.show();
            } else NotifyUtils.logDebug(TAG, "Unspecified view is clicked!!");
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int id = menuItem.getItemId();
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
