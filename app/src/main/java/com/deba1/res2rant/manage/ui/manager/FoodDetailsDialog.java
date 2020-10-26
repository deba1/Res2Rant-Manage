package com.deba1.res2rant.manage.ui.manager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.deba1.res2rant.manage.R;
import com.deba1.res2rant.manage.models.Food;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import static android.app.Activity.RESULT_OK;

public class FoodDetailsDialog extends DialogFragment {
    private final FoodListAdapter mAdapter;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

    private ImageView foodImageView;
    private final Drawable foodImage;
    private final int SELECT_FOOD_IMAGE = 10011;
    private Uri foodImageUri = null;
    private final Food food;
    private final int position;

    public FoodDetailsDialog(FoodListAdapter adapter, Food food, Drawable foodImage, int position) {
        this.mAdapter = adapter;
        this.food = food;
        this.foodImage = foodImage;
        this.position = position;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.edit);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_food_edit, null);
        EditText fieldName = view1.findViewById(R.id.foodEditName);
        EditText fieldDesc = view1.findViewById(R.id.foodEditDesc);
        EditText fieldPrice = view1.findViewById(R.id.foodEditPrice);
        foodImageView = view1.findViewById(R.id.foodEditPreview);
        Button foodImageSelectButton = view1.findViewById(R.id.foodImageSelectButton);
        SwitchCompat foodAvailable = view1.findViewById(R.id.foodEditAvailable);

        fieldName.setText(food.name);
        fieldDesc.setText(food.description);
        fieldPrice.setText(String.valueOf(food.price));
        foodImageView.setImageDrawable(foodImage);
        foodAvailable.setChecked(food.available);

        foodImageSelectButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FOOD_IMAGE);
        });
        builder.setView(view1);

        builder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setNegativeButton(R.string.delete, (dialogInterface, i) -> firebaseFirestore.collection("foods")
                .document(food.id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), R.string.food_deleted, Toast.LENGTH_SHORT).show();
                    mAdapter.notifyItemRemoved(position);
                }));

        builder.setPositiveButton(R.string.edit, (dialogInterface, i) -> {
            food.name = fieldName.getText().toString().trim();
            food.description = fieldDesc.getText().toString().trim();
            food.price = Float.parseFloat(fieldPrice.getText().toString().trim());

            firebaseFirestore.collection("foods")
                    .document(food.id)
                    .set(food)
                    .addOnSuccessListener(aVoid -> {
                        if (foodImageUri != null) {
                            firebaseStorage.child("foods/"+food.id)
                                    .putFile(foodImageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        mAdapter.notifyItemChanged(position);
                                        Toast.makeText(getContext(), R.string.food_updated, Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), R.string.error_default, Toast.LENGTH_SHORT).show());
                        }
                    });
        });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_FOOD_IMAGE && resultCode == RESULT_OK && data != null) {
            foodImageUri = data.getData();
            foodImageView.setImageURI(foodImageUri);
        }
    }
}
