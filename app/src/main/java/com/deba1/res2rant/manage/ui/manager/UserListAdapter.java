package com.deba1.res2rant.manage.ui.manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.deba1.res2rant.manage.R;
import com.deba1.res2rant.manage.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> implements Filterable {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<User> userList;
    private List<User> filteredUserList;
    private Context context;

    public UserListAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.filteredUserList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.component_user_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.fullNameView.setText(filteredUserList.get(position).name);
        holder.numberView.setText(String.format("%s", filteredUserList.get(position).mobileNo));
        holder.discountView.setText(String.format("Discount: %s%%", filteredUserList.get(position).discount));

        holder.layout.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = View.inflate(context, R.layout.dialog_user_list, null);
            TextView userNameView = dialogView.findViewById(R.id.userDetailsName);
            TextView userMobileView = dialogView.findViewById(R.id.userDetailsMobile);
            final TextView userDiscountView = dialogView.findViewById(R.id.userDetailsDiscount);
            final SeekBar discountSeekBar = dialogView.findViewById(R.id.discountSeekBar);

            userNameView.setText(filteredUserList.get(position).name);
            userDiscountView.setText(String.format("%s%%", filteredUserList.get(position).discount));
            userMobileView.setText(String.format("%s", filteredUserList.get(position).mobileNo));
            discountSeekBar.setProgress((int)filteredUserList.get(position).discount);
            discountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    userDiscountView.setText(String.format("%s%%", (float) i));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            builder.setView(dialogView);
            builder.setTitle(R.string.discount);
            builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());
            builder.setPositiveButton(R.string.edit, (dialogInterface, i) -> db.collection("users")
                    .document(filteredUserList.get(position).id)
                    .update("discount", discountSeekBar.getProgress())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                        filteredUserList.get(position).discount = discountSeekBar.getProgress();
                        notifyDataSetChanged();
                    }));
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return filteredUserList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String keyword = charSequence.toString();
                if (keyword.isEmpty())
                    filteredUserList = userList;
                else {
                    List<User> filteredList = new ArrayList<>();
                    for (User row : userList) {
                        if (row.name.toLowerCase().contains(keyword.toLowerCase()) || String.format("%s", row.mobileNo).contains(keyword))
                            filteredList.add(row);
                    }
                    filteredUserList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredUserList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredUserList = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fullNameView;
        public TextView numberView;
        public TextView discountView;
        public View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.layout = itemView;
            this.fullNameView = itemView.findViewById(R.id.user_item_name);
            this.numberView = itemView.findViewById(R.id.user_item_mobile);
            this.discountView = itemView.findViewById(R.id.user_item_discount);
        }
    }

    public void add(User user) {
        this.userList.add(user);
        notifyDataSetChanged();
    }
    public void remove(int index) {
        this.userList.remove(index);
        notifyDataSetChanged();
    }
}
