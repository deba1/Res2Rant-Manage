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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import static android.app.Activity.RESULT_OK;

public class FoodAddDialog extends DialogFragment {
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference();

    private ImageView foodImageView;
    private final int SELECT_FOOD_IMAGE = 10012;
    private Uri foodImageUri;
    private final Food food;
    private SubmitListener listener;

    public FoodAddDialog() {
        this.food = new Food();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_food);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_food_add, null);
        EditText fieldName = view1.findViewById(R.id.foodAddName);
        EditText fieldDesc = view1.findViewById(R.id.foodAddDesc);
        EditText fieldPrice = view1.findViewById(R.id.foodAddPrice);
        foodImageView = view1.findViewById(R.id.foodAddPreview);
        Button foodImageSelectButton = view1.findViewById(R.id.foodImageSelectButton);
        SwitchCompat foodAvailable = view1.findViewById(R.id.foodAddAvailable);

        foodImageSelectButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_FOOD_IMAGE);
        });
        builder.setView(view1);

        builder.setNeutralButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());

        builder.setPositiveButton(R.string.add_food, (dialogInterface, i) -> {
            food.name = fieldName.getText().toString().trim();
            food.description = fieldDesc.getText().toString().trim();
            food.price = Float.parseFloat(fieldPrice.getText().toString().trim());
            food.available = foodAvailable.isChecked();

            firebaseFirestore.collection("foods")
                    .add(food)
                    .addOnSuccessListener(documentReference -> {
                        food.id = documentReference.getId();
                        if (foodImageUri != null) {
                            firebaseStorage.child("foods/"+food.id)
                                    .putFile(foodImageUri)
                                    .addOnSuccessListener(taskSnapshot -> {
                                        food.id = documentReference.getId();
                                        if (listener != null)
                                            listener.onOk(food);
                                    })
                                    .addOnFailureListener(e -> listener.onFail(e));
                        }
                    })
            .addOnFailureListener(e -> listener.onFail(e));
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

    public void setSubmitListener(SubmitListener listener) {
        this.listener = listener;
    }

    public interface SubmitListener {
        void onOk(Food food);
        void onFail(Exception exception);
    }
}
