package com.deba1.res2rant.manage.ui.manager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.deba1.res2rant.manage.R;
import com.deba1.res2rant.manage.models.Food;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FoodFragment extends Fragment {
    private RecyclerView foodListView;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FoodListAdapter mAdapter;
    final List<Food> allFoods = new ArrayList<>();
    private View mainView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_food_list, container, false);
        foodListView = mainView.findViewById(R.id.foodListRecyclerView);
        foodListView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        foodListView.setLayoutManager(layoutManager);
        SearchView searchView = mainView.findViewById(R.id.foodListSearchView);
        FloatingActionButton addButton = mainView.findViewById(R.id.foodListAddButton);
        addButton.setOnClickListener(view -> onAddButtonClick(view));

        db.collection("foods")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        allFoods.add(new Food(snapshot));
                    }

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
        mainView.findViewById(R.id.foodListLoadingContainer).setVisibility(View.VISIBLE);
        db.collection("foods")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allFoods.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                        allFoods.add(new Food(snapshot));
                    mainView.findViewById(R.id.foodListLoadingContainer).setVisibility(View.GONE);
                });
    }

    public void onAddButtonClick(View view) {
        FoodAddDialog dialog = new FoodAddDialog();
        dialog.setSubmitListener(new FoodAddDialog.SubmitListener() {
            @Override
            public void onOk(Food food) {
                allFoods.add(food);
                mAdapter.notifyDataSetChanged();
                Snackbar.make(view, R.string.food_added, BaseTransientBottomBar.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(Exception exception) {
                Snackbar.make(view, "Failed to add food!\nCause: " + exception.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
        dialog.show(getParentFragmentManager(), FoodFragment.class.getSimpleName());
    }

}
