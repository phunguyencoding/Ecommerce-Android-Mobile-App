package group15.finalassignment.ecommerce.View.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group15.finalassignment.ecommerce.R;
import group15.finalassignment.ecommerce.View.model.Cart;
import group15.finalassignment.ecommerce.View.model.CartItem;

public class NotificationService extends Service {
    /*
    * Display notification to current sign in user when the price of one of the product in the user
    * cart reduced
    * */
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private static final int MY_PERMISSION_REQUEST_LOCATION = 99;
    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_ID_FIREBASE = "2";
    private NotificationManager notificationManager;
    private Map<String, Long> productPriceMap = new HashMap<>();
    private Cart cart = new Cart();
    private final List<ListenerRegistration> listenerRegistrationList = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        detachListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onCreate();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        createNotificationChannel(CHANNEL_ID);
        createNotificationChannel(CHANNEL_ID_FIREBASE);
        Notification notification = composeNotification("RMIT Store Notification Service", "Notification for " + auth.getCurrentUser().getEmail(), CHANNEL_ID);
        fetchUserCart();

        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    // Firebase
    private void fetchUserCart() {
        db.collection("accounts")
                .document(auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            cart.mapCartItemListFromDocument((ArrayList<HashMap<String, Object>>) document.get("cart"));
                            for (CartItem cartItem : cart.getItemList()) {
                                fetchProductByName(cartItem.getProductName());
                            }
                        }
                    }
                });
    }

    private void fetchProductByName(String name) {
        db.collection("AllProducts")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document:task.getResult()) {
                               productPriceMap.put(name, document.getLong("price"));
                               attachListener(document.getId());
                            }
                        }
                    }
                });
    }

    private void attachListener(String productId) {
        ListenerRegistration listenerRegistration = db.collection("AllProducts")
                .document(productId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            String productName = snapshot.getString("name");
                            if (snapshot.getLong("price") < productPriceMap.get(productName)) {
                                createNotification("Product Sales", productName + " in your cart is now reduce", 2, CHANNEL_ID_FIREBASE);
                                productPriceMap.put(productName, snapshot.getLong("price"));
                            }
                        }
                    }
                });
        listenerRegistrationList.add(listenerRegistration);
    }

    private void detachListener() {
        for (ListenerRegistration listenerRegistration : listenerRegistrationList) {
            listenerRegistration.remove();
        }
        listenerRegistrationList.clear();
    }

    // Notification methods
    private void createNotificationChannel(String channelID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "RMIT Store Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(name);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification composeNotification(String title, String content, String id) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(NotificationService.this, CHANNEL_ID);
        notifyBuilder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.app_icon)
                .setPriority(android.app.Notification.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setChannelId(id);
        return notifyBuilder.build();
    }

    private void createNotification(String title, String content, int id, String channelID) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(NotificationService.this, channelID);
        notifyBuilder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.app_icon)
                .setPriority(android.app.Notification.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setChannelId(channelID)
                .setAutoCancel(true);

        notificationManager.notify(id, notifyBuilder.build());
    }
}
