package com.deba1.res2rant.manage.ui.chef;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deba1.res2rant.manage.R;
import com.deba1.res2rant.manage.models.Order;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
    private final List<Order> ordersFiltered;
    private final Fragment fragment;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrderListAdapter(List<Order> allOrders, Fragment fragment) {
        this.ordersFiltered = allOrders;
        this.fragment = fragment;
    }

    public void add(Order order) {
        ordersFiltered.add(order);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.component_order_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Order order = this.ordersFiltered.get(position);
        holder.dateView.setText(this.ordersFiltered.get(position).formatOrderTime());
        holder.statusView.setText(order.status);
        holder.layout.setOnClickListener(view -> showOrderDetails(order));
    }

    private void showOrderDetails(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        View header = View.inflate(fragment.getContext(), R.layout.dialog_order_header, null);
        TextView dateView = header.findViewById(R.id.orderHeaderDate);
        Spinner spinner = header.findViewById(R.id.orderHeaderStatus);

        dateView.setText(order.formatOrderTime());
        spinner.setSelection(order.getStatusIndex());

        View body = View.inflate(fragment.getContext(), R.layout.fragment_order_single, null);
        RecyclerView foodListView = body.findViewById(R.id.orderSingleList);
        foodListView.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        foodListView.setHasFixedSize(true);
        foodListView.setAdapter(new CartItemAdapter(order.cart));

        builder.setCustomTitle(header);
        builder.setView(body);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.edit, (dialogInterface, i) -> updateOrder(order, spinner.getSelectedItem().toString()));
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return this.ordersFiltered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateView, statusView;
        public View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.layout = itemView;
            this.dateView = itemView.findViewById(R.id.order_item_date);
            this.statusView = itemView.findViewById(R.id.order_item_status);
        }
    }

    public void updateOrder(Order order, String status) {
        order.status = status;

        db.collection("orders")
                .document(order.Id)
                .set(order)
                .addOnFailureListener(e -> Toast.makeText(fragment.getContext(), "Order Update Failed!\nCause: " + e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(aVoid -> Toast.makeText(fragment.getContext(), R.string.order_update_success, Toast.LENGTH_SHORT).show());
    }
}
