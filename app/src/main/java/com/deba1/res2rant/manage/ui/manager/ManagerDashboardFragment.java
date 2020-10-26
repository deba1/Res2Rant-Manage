package com.deba1.res2rant.manage.ui.manager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deba1.res2rant.manage.R;
import com.deba1.res2rant.manage.models.Cart;
import com.deba1.res2rant.manage.models.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ManagerDashboardFragment extends Fragment {
    private DatePickerDialog datePicker;
    private Calendar fromDate, toDate;
    private TextView totalOrders, totalEarnings;
    private int orderCount;
    private long earningAmount;
    EditText fromDateView, toDateView;
    private ProgressBar loading;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        fromDateView = root.findViewById(R.id.dashboard_date_from);
        toDateView = root.findViewById(R.id.dashboard_date_to);
        totalOrders = root.findViewById(R.id.dashboard_total_orders);
        totalEarnings = root.findViewById(R.id.dashboard_total_earnings);
        loading = root.findViewById(R.id.dashboard_loading);
        fromDate = Calendar.getInstance();
        toDate = Calendar.getInstance();
        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);
        fromDateView.setText(String.format("%s/%s/%s", day, month+1, year));
        toDateView.setText(String.format("%s/%s/%s", day, month+1, year));

        fromDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                datePicker = new DatePickerDialog(getContext(), (datePicker, yy, mm, dd) -> {
                    fromDate.set(yy, mm, dd, 0, 0);
                    fromDateView.setText(String.format("%s/%s/%s", dd, mm+1, yy));
                    CheckData();
                }, year, month, day);

                datePicker.show();
            }
        });
        toDateView.setOnClickListener(view -> {
            datePicker = new DatePickerDialog(getContext(), (datePicker, i, i1, i2) -> {
                toDate.set(i, i1, i2, 23, 59);
                toDateView.setText(String.format("%s/%s/%s", i2, i1+1, i));
                CheckData();
            }, year, month, day);
            DatePicker picker = datePicker.getDatePicker();
            picker.setMinDate(fromDate.getTimeInMillis());
            datePicker.show();
        });

        CheckData();
        return root;
    }

    public void CheckData() {
        loading.setVisibility(View.VISIBLE);
        Date dateFrom = new Date(), dateTo = new Date();
        try {
            dateFrom = new SimpleDateFormat("d/M/yyyy", Locale.ENGLISH).parse(fromDateView.getText().toString());
            dateFrom.setHours(0);
            dateTo = new SimpleDateFormat("d/M/yyyy", Locale.ENGLISH).parse(toDateView.getText().toString());
            dateTo.setHours(23);
            dateTo.setMinutes(59);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db.collection("orders")
                .whereGreaterThanOrEqualTo("orderedOn", dateFrom)
                .whereLessThanOrEqualTo("orderedOn", dateTo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderCount = queryDocumentSnapshots.size();
                    totalOrders.setText(String.valueOf(orderCount));
                    for (QueryDocumentSnapshot snapshot :
                            queryDocumentSnapshots) {
                        Order order = snapshot.toObject(Order.class);
                        earningAmount += order.price;
                    }
                    totalEarnings.setText(String.format("BDT %s", earningAmount));
                    loading.setVisibility(View.INVISIBLE);
                });
    }
}
