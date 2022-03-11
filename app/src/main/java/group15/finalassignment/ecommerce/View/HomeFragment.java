package group15.finalassignment.ecommerce.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import group15.finalassignment.ecommerce.R;
import group15.finalassignment.ecommerce.View.model.CategoryModel;
import group15.finalassignment.ecommerce.View.model.Product;

public class HomeFragment extends Fragment {

    // LinearLayout set up while waiting for data coming
    LinearLayout linearLayout;

    // Progress Dialog while waiting for Firestore read data
    ProgressDialog progressDialog;

    RecyclerView catRecyclerView, newProductsRecyclerView, popularProductsRecyclerView;

    // Category RecyclerView
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;

    // New Products RecyclerView
    NewProductsAdapter newProductsAdapter;
    List<Product> newProductList;

    // Popular Products RecyclerView
    PopularProductsAdapter popularProductsAdapter;
    List<Product> popularProductList;

    // Firestore
    FirebaseFirestore db;

    TextView viewAllProduct;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        progressDialog = new ProgressDialog(getActivity());
        catRecyclerView = root.findViewById(R.id.rec_category);
        newProductsRecyclerView = root.findViewById(R.id.new_product_rec);
        popularProductsRecyclerView = root.findViewById(R.id.popular_rec);

        // Firestore
        db = FirebaseFirestore.getInstance();

        linearLayout = root.findViewById(R.id.home_layout);
        linearLayout.setVisibility(View.GONE);

        // ProgressDialog
        progressDialog.setTitle("Welcome to Team 15 Ecommerce App");
        progressDialog.setMessage("Please wait for us to server u!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Image Slider
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.applogo, "Discount on Men T-Shirt", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.admin_register_background, "70% OFF", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.applogo, "Sale Up to 50%", ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels);

        // Category Part
        catRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryModelList);
        catRecyclerView.setAdapter(categoryAdapter);
        viewAllProduct = (TextView) root.findViewById(R.id.category_see_all);

        viewAllProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(container.getContext(), SearchProductActivity.class);
                intent.putExtra("category", "");
                intent.putExtra("name", "");
                startActivity(intent);
            }
        });

        db.collection("Category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                categoryModelList.add(categoryModel);
                                categoryAdapter.notifyDataSetChanged();

                                // Dismiss progress dialog and show page after finishing reading data
                                linearLayout.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Category Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // New Products Part
        newProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        newProductList = new ArrayList<>();
        newProductsAdapter = new NewProductsAdapter(getContext(), newProductList);
        newProductsRecyclerView.setAdapter(newProductsAdapter);

        db.collection("NewProduct")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product newProduct = document.toObject(Product.class);
                                newProductList.add(newProduct);
                                newProductsAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "New Products Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Popular Products Part
        popularProductsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        popularProductList = new ArrayList<>();
        popularProductsAdapter = new PopularProductsAdapter(getContext(), popularProductList);
        popularProductsRecyclerView.setAdapter(popularProductsAdapter);

        db.collection("AllProducts")
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(4)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product popularProduct = document.toObject(Product.class);
                                popularProductList.add(popularProduct);
                                popularProductsAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Popular Products Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return root;
    }
}