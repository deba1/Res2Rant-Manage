package com.deba1.res2rant.manage.ui.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.deba1.res2rant.manage.R;
import com.deba1.res2rant.manage.ui.manager.FoodListAdapter;
import com.deba1.res2rant.manage.models.*;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FoodFragment extends Fragment {
    private RecyclerView foodListView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SearchView searchView;
    final List<Food> allFoods = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.fragment_foods, container, false);
        foodListView = mainView.findViewById(R.id.foodListView);
        foodListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        foodListView.setLayoutManager(layoutManager);
        searchView = mainView.findViewById(R.id.foodSearchView);

        db.collection("foods")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        allFoods.add(new Food(snapshot));
                    }

                    mainView.findViewById(R.id.foodListLoading).setVisibility(View.GONE);
                    mAdapter = new FoodListAdapter(allFoods, FoodFragment.this);
                    foodListView.setAdapter(mAdapter);
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });

        return mainView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateFood();
    }

    public void updateFood() {
        db.collection("foods")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        allFoods.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                            allFoods.add(new Food(snapshot));
                    }
                });
    }
}
