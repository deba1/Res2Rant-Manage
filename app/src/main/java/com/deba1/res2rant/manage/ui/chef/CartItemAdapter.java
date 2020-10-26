package com.deba1.res2rant.manage.ui.chef;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.deba1.res2rant.manage.R;
import com.deba1.res2rant.manage.models.Cart;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private final List<Cart.CartItem> items;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    //private FirebaseStorage storage = FirebaseStorage.getInstance();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodNameView;
        public TextView noteView;
        public ImageView foodItemImage;
        public TextView countView;
        public TextView tableNoView;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            foodNameView = v.findViewById(R.id.cart_item_title);
            noteView = v.findViewById(R.id.cart_item_note);
            foodItemImage = v.findViewById(R.id.cart_item_image);
            countView = v.findViewById(R.id.cart_item_count);
            tableNoView = v.findViewById(R.id.cart_item_table);
        }
    }

    public void add(Cart.CartItem food) {
        items.add(getItemCount(), food);
        notifyItemChanged(getItemCount());
    }

    public void add(int index, Cart.CartItem food) {
        items.add(index, food);
        notifyItemInserted(index);
    }

    public void remove(int index) {
        items.remove(index);
        notifyItemRemoved(index);
    }

    public void deleteItem(final int index) {
        db.collection("/carts")
                .document(auth.getUid())
                .update("items", FieldValue.arrayRemove(items.get(index)))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        remove(index);
                    }
                });
    }

    public CartItemAdapter(List<Cart.CartItem> foodList) {
        items = foodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.component_cart_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.foodNameView.setText(items.get(position).foodName);
        holder.noteView.setText(items.get(position).note);
        holder.countView.setText(String.format("Count: %s", items.get(position).count));
        holder.tableNoView.setText(items.get(position).table);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
