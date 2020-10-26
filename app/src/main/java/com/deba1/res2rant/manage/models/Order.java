package com.deba1.res2rant.manage.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Order {
    public String Id;
    public List<Cart.CartItem> cart;
    public String userId;
    public Timestamp orderedOn;
    public String status;
    public float price;

    public Order() {}

    public Order(QueryDocumentSnapshot snapshot) {
        Order o = snapshot.toObject(Order.class);
        this.Id = snapshot.getId();
        this.status = o.status;
        this.price = o.price;
        this.userId = o.userId;
        this.cart = o.cart;
    }

    public String formatOrderTime() {
        return new SimpleDateFormat("dd/MM/yy - hh:mm aa", Locale.ENGLISH).format(this.orderedOn.toDate());
    }

    public int getStatusIndex() {
        for (int i = 0; i < OrderState.values().length; i++) {
            if (status.equals(OrderState.values()[i].name()))
                return i;
        }
        return 0;
    }
}
