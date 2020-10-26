package com.deba1.res2rant.manage.ui.manager;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.deba1.res2rant.manage.R;
import com.deba1.res2rant.manage.models.Food;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> implements Filterable {
    private List<Food> foods;
    private List<Food> ogFoods;
    private Fragment fragment;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                String keyword = charSequence.toString();
                if (keyword.isEmpty())
                    foods = ogFoods;
                else {
                    List<Food> filteredList = new ArrayList<>();
                    for (Food food:
                         ogFoods) {
                        if (food.name.toLowerCase().contains(keyword.toLowerCase()))
                            filteredList.add(food);
                    }
                    foods = filteredList;
                }
                results.values = foods;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                foods = (ArrayList<Food>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodItemName;
        public TextView foodItemDesc;
        public ImageView foodItemImage;
        public TextView foodItemPrice;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            foodItemName = v.findViewById(R.id.foodItemName);
            foodItemDesc = v.findViewById(R.id.foodItemDesc);
            foodItemImage = v.findViewById(R.id.foodItemImage);
            foodItemPrice = v.findViewById(R.id.foodItemPrice);
        }
    }

    public void add(int index, Food food) {
        foods.add(index, food);
        notifyItemInserted(index);
    }

    public void remove(int index) {
        foods.remove(index);
        notifyItemRemoved(index);
    }

    public FoodListAdapter(List<Food> foodList, Fragment manager) {
        this.foods = foodList;
        this.ogFoods = foodList;
        fragment = manager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.component_food_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.foodItemName.setText(foods.get(position).name);
        holder.foodItemDesc.setText(foods.get(position).description);
        holder.foodItemPrice.setText(String.format("৳ %s", foods.get(position).price));

        storage.getReference(foods.get(position).imagePath)
                .getBytes(1048576)
                .addOnSuccessListener(bytes -> {
                    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                    Drawable image = Drawable.createFromStream(stream, "foodImage");
                    holder.foodItemImage.setImageDrawable(image);

                    holder.layout.setOnClickListener(view -> displayAlert(foods.get(position), image, position));
                });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    private void displayAlert(Food food, Drawable image, int position) {
        DialogFragment dialog = new FoodDetailsDialog(this, food, image, position);
        dialog.show(fragment.getParentFragmentManager(), FoodListAdapter.class.getSimpleName());
    }
}
