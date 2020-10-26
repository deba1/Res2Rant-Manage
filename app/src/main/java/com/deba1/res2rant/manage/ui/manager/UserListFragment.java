package com.deba1.res2rant.manage.ui.manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.deba1.res2rant.manage.R;
import com.deba1.res2rant.manage.models.User;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserListFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<User> userList = new ArrayList<>();
    private UserListAdapter mAdapter;
    private RecyclerView userContainer;
    private ProgressBar loading;
    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_users, container, false);
        userContainer = root.findViewById(R.id.usersRecyclerView);
        userContainer.setHasFixedSize(true);
        loading = root.findViewById(R.id.usersLoading);
        SearchView searchView = root.findViewById(R.id.searchView);
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot snapshot :
                         queryDocumentSnapshots) {
                        User user = snapshot.toObject(User.class);
                        if (!user.role.equals("ADMIN")) {
                            user.id = snapshot.getId();
                            userList.add(user);
                        }
                    }
                    mAdapter = new UserListAdapter(userList, getContext());
                    userContainer.setLayoutManager(new LinearLayoutManager(getContext()));
                    userContainer.setAdapter(mAdapter);
                    loading.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Snackbar.make(root, R.string.error_default, BaseTransientBottomBar.LENGTH_SHORT).show());
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
        return root;
    }
}
