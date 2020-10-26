package com.deba1.res2rant.manage.ui.chef;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.deba1.res2rant.manage.R;
import com.deba1.res2rant.manage.models.Order;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrdersFragment extends Fragment {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar loading;
    private OrderListAdapter mAdapter;
    private final List<Order> orders = new ArrayList<>();

    private final Handler handler = new Handler();
    private Runnable runnable;
    private final long delay = 15000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_orders, container, false);
        loading = mainView.findViewById(R.id.orders_loading);
        RecyclerView listContainer = mainView.findViewById(R.id.orders_list);

        listContainer.setLayoutManager(new LinearLayoutManager(getContext()));
        listContainer.setHasFixedSize(true);
        mAdapter = new OrderListAdapter(orders, OrdersFragment.this);
        listContainer.setAdapter(mAdapter);

        updateOrder();

        return mainView;
    }

    @Override
    public void onResume() {
        handler.postDelayed(runnable = () -> {
            handler.postDelayed(runnable, delay);
            updateOrder();
            Log.d("OrderService", "Fetching new orders");
        }, delay);
        super.onResume();
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    private void updateOrder() {
        db.collection("orders")
                .orderBy("orderedOn", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orders.clear();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Order order = snapshot.toObject(Order.class);
                        order.Id = snapshot.getId();
                        mAdapter.add(order);
                    }
                    loading.setVisibility(View.GONE);
                });
    }
}
