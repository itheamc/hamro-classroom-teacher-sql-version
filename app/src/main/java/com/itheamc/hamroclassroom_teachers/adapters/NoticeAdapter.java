package com.itheamc.hamroclassroom_teachers.adapters;

import static com.itheamc.hamroclassroom_teachers.models.Notice.noticeItemCallback;

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
import com.itheamc.hamroclassroom_teachers.callbacks.NoticeCallbacks;
import com.itheamc.hamroclassroom_teachers.databinding.NoticeViewBinding;
import com.itheamc.hamroclassroom_teachers.models.Notice;
import com.itheamc.hamroclassroom_teachers.utils.NotifyUtils;
import com.itheamc.hamroclassroom_teachers.utils.TimeUtils;

import java.text.DateFormat;

public class NoticeAdapter extends ListAdapter<Notice, NoticeAdapter.NoticeViewHolder> {
    private static final String TAG = "NoticeAdapter";
    private final NoticeCallbacks callbacks;

    public NoticeAdapter(@NonNull NoticeCallbacks noticeCallbacks) {
        super(noticeItemCallback);
        this.callbacks = noticeCallbacks;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        NoticeViewBinding viewBinding = NoticeViewBinding.inflate(inflater, parent, false);
        return new NoticeViewHolder(viewBinding, callbacks);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice notice = getItem(position);
        String formattedDate = DateFormat.getDateInstance().format(TimeUtils.toDate(notice.get_notified_on()));
        holder.viewBinding.setDate(formattedDate);

        // Submitting to the data binding
        holder.viewBinding.setNotice(notice);
        holder.viewBinding.setDate(formattedDate);
    }

    protected static class NoticeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        private final NoticeViewBinding viewBinding;
        private final NoticeCallbacks callbacks;

        public NoticeViewHolder(@NonNull NoticeViewBinding noticeViewBinding, NoticeCallbacks noticeCallbacks) {
            super(noticeViewBinding.getRoot());
            this.viewBinding = noticeViewBinding;
            this.callbacks = noticeCallbacks;
            this.viewBinding.noticeCardView.setOnClickListener(this);
            this.viewBinding.menuBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int _id = v.getId();
            if (_id == viewBinding.noticeCardView.getId())
                callbacks.onClick(getAdapterPosition());
            else if (_id == viewBinding.menuBtn.getId()) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.notice_menu);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true);
                }
                popupMenu.show();
            } else NotifyUtils.logDebug(TAG, "Unspecified view is clicked!!");
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int id = menuItem.getItemId();
            if (id == R.id.notice_menu_edit) {
                callbacks.onEditClick(getAdapterPosition());
                return true;
            } else if (id == R.id.notice_menu_delete) {
                callbacks.onDeleteClick(getAdapterPosition());
                return true;
            } else {
                return false;
            }
        }
    }
}
