package com.example.splashactivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomePageAdapter extends RecyclerView.Adapter {

    private List<HomePageModel> homePageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private int lastPosition = -1;

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                return HomePageModel.BANNER_SLIDER;
            case 1:
                return HomePageModel.STRIP_AD_BANNER;
            case 2:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;
            case 3:
                return HomePageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case HomePageModel.BANNER_SLIDER:
                View bannerSliderView = inflater.inflate(R.layout.sliding_ad_layout, parent, false);
                return new BannerSliderViewholder(bannerSliderView);
            case HomePageModel.STRIP_AD_BANNER:
                View stripAdview = inflater.inflate(R.layout.strip_ad_layout, parent, false);
                return new StripAdBannerViewHolder(stripAdview);
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View HorizontalProductview = inflater.inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new HorizontalProductViewHolder(HorizontalProductview);
            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridProductView = inflater.inflate(R.layout.grid_product_layout, parent, false);
                return new GridProductViewHolder(gridProductView);
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int  position) {


        switch (homePageModelList.get(position).getType()) {
            case HomePageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModelList();
                ((BannerSliderViewholder) holder).setBannerSliderViewPager(sliderModelList);
                break;
            case HomePageModel.STRIP_AD_BANNER:
                String resource = homePageModelList.get(position).getResource();
                String color = homePageModelList.get(position).getBackgroundColor();
                ((StripAdBannerViewHolder) holder).setStripAd(resource, color);
                break;
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                String layoutColor = homePageModelList.get(position).getBackgroundColor();
                String horizontalLayouttitle = homePageModelList.get(position).getTitle();
                List<WishlistModel> viewAllProductList = homePageModelList.get(position).getViewAllProductList();
                List<HorizontalProductScrollModel> horizontalProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((HorizontalProductViewHolder) holder).setHorizontalProductLayout(horizontalProductScrollModelList, horizontalLayouttitle, layoutColor, viewAllProductList);
                break;
            case HomePageModel.GRID_PRODUCT_VIEW:
                String gridLayoutColor = homePageModelList.get(position).getBackgroundColor();
                String GridLayouttitle = homePageModelList.get(position).getTitle();
                List<HorizontalProductScrollModel> GridProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((GridProductViewHolder) holder).setGridProductLayout(GridProductScrollModelList, GridLayouttitle, gridLayoutColor);
                break;

            default:
                return;
        }
        if (lastPosition < position) {

            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }


    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    public class BannerSliderViewholder extends RecyclerView.ViewHolder {

        private ViewPager bannerSliderViewPager;
        private int currentPage;
        private Timer timer;
        final private long DELAY_TIME = 3000;
        final private long PERIOD_TIME = 3000;
        private List<SliderModel> arrangedList;

        public BannerSliderViewholder(@NonNull View itemView) {
            super(itemView);
            bannerSliderViewPager = itemView.findViewById(R.id.banner_slider_view_pager);


        }


        private void setBannerSliderViewPager(final List<SliderModel> sliderModelList) {
            currentPage = 2;
            if (timer != null) {
                timer.cancel();
            }
            arrangedList = new ArrayList<>();
            for (int x = 0; x < sliderModelList.size(); x++) {
                arrangedList.add(x, sliderModelList.get(x));
            }

            arrangedList.add(0, sliderModelList.get(sliderModelList.size() - 2));
            arrangedList.add(1, sliderModelList.get(sliderModelList.size() - 1));
            arrangedList.add(sliderModelList.get(0));
            arrangedList.add(sliderModelList.get(1));

            SliderAdapter sliderAdapter = new SliderAdapter(arrangedList);
            bannerSliderViewPager.setAdapter(sliderAdapter);
            bannerSliderViewPager.setClipToPadding(false);
            bannerSliderViewPager.setPageMargin(20);

            bannerSliderViewPager.setCurrentItem(currentPage);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    currentPage = i;
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                    if (i == ViewPager.SCROLL_STATE_IDLE) {
                        pageLooper(arrangedList);
                    }
                }
            };
            bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);
            startBannerSlideShow(arrangedList);

            bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {


                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pageLooper(arrangedList);
                    stopBannerSlideShow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startBannerSlideShow(arrangedList);
                    }
                    return false;
                }
            });
        }

        private void pageLooper(List<SliderModel> sliderModelList) {
            if (currentPage == sliderModelList.size() - 2) {
                currentPage = 2;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }
            if (currentPage == 1) {
                currentPage = sliderModelList.size() - 3;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }
        }

        private void startBannerSlideShow(List<SliderModel> sliderModelList) {
            Handler handler = new Handler();
            Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= sliderModelList.size()) {
                        currentPage = 1;
                    }
                    bannerSliderViewPager.setCurrentItem(currentPage++, true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);
        }

        private void stopBannerSlideShow() {
            timer.cancel();

        }
    }

    private class StripAdBannerViewHolder extends RecyclerView.ViewHolder {
        private ImageView stripAdImage;
        private ConstraintLayout stripAdContainer;

        public StripAdBannerViewHolder(@NonNull View itemView) {
            super(itemView);

            stripAdImage = itemView.findViewById(R.id.strip_ad__image);
            stripAdContainer = itemView.findViewById(R.id.strip_ad_container);
        }

        private void setStripAd(String resource, String color) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.img)).into(stripAdImage);
            stripAdContainer.setBackgroundColor(Color.parseColor(color));

        }
    }

    public class HorizontalProductViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout container;
        private TextView horizontalLayoutTitle;
        private Button horizontalLayoutViewAllBtn;
        private RecyclerView horizontalRecyclerView;

        public HorizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            horizontalLayoutTitle = itemView.findViewById(R.id.horizontal_scroll_layout_title);
            horizontalLayoutViewAllBtn = itemView.findViewById(R.id.horizontal_scroll_view_all_button);
            horizontalRecyclerView = itemView.findViewById(R.id.horizontal_scroll_layout_recyclerview);
            horizontalRecyclerView.setRecycledViewPool(recycledViewPool);
        }

        private void setHorizontalProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, String title, String color, List<WishlistModel> viewAllProductList) {
            // Check if horizontalLayoutTitle is null
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            horizontalLayoutTitle.setText(title);

            for (HorizontalProductScrollModel model : horizontalProductScrollModelList) {
                if (!model.getProductID().isEmpty() && model.getProductTitle().isEmpty()) {
                    firebaseFirestore.collection("PRODUCTS")
                            .document(model.getProductID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        model.setProductTitle(task.getResult().getString("product_title"));
                                        model.setProductImage(task.getResult().getString("product_image_1"));
                                        model.setProductPrice(task.getResult().getString("product_price"));
                                        WishlistModel wishlistModel = viewAllProductList.get(horizontalProductScrollModelList.indexOf(model));

                                        wishlistModel.setTotalRating(task.getResult().getLong("total_ratings"));
                                        wishlistModel.setRating(task.getResult().getString("average_rating"));
                                        wishlistModel.setProductTitle(task.getResult().getString("product_title"));
                                        wishlistModel.setProductPrice(task.getResult().getString("product_price"));
                                        wishlistModel.setProductImage(task.getResult().getString("product_image_1"));
                                        wishlistModel.setFreeCoupens(task.getResult().getLong("free_coupens"));
                                        wishlistModel.setCuttedPrice(task.getResult().getString("cutted_price"));
                                        wishlistModel.setCOD(task.getResult().getBoolean("COD"));
                                        wishlistModel.setInStock(task.getResult().getLong("stock_quantity") > 0);

                                        if (horizontalProductScrollModelList.indexOf(model) == horizontalProductScrollModelList.size() - 1) {
                                            if (horizontalRecyclerView.getAdapter() != null) {
                                                horizontalRecyclerView.getAdapter().notifyDataSetChanged();
                                            }
                                        }
                                    } else {
                                        ///do nothing
                                    }
                                }
                            });
                }
                if (horizontalProductScrollModelList.size() > 8) {
                    horizontalLayoutViewAllBtn.setVisibility(View.VISIBLE);
                    horizontalLayoutViewAllBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ViewAllActivity.wishlistModelList = viewAllProductList;
                            Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                            viewAllIntent.putExtra("layout_code", 0);
                            viewAllIntent.putExtra("title", title);
                            itemView.getContext().startActivity(viewAllIntent);
                        }
                    });
                } else {
                    horizontalLayoutViewAllBtn.setVisibility(View.INVISIBLE);
                }
            }
            // Check if horizontalRecyclerView is null
            if (horizontalRecyclerView != null) {
                HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                horizontalRecyclerView.setLayoutManager(linearLayoutManager);
                horizontalRecyclerView.setAdapter(horizontalProductScrollAdapter);
                horizontalProductScrollAdapter.notifyDataSetChanged();
            }
        }
    }

    public class GridProductViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout container;
        private TextView gridLayoutTitle;
        private Button gridLayoutViewAllbtn;

        private GridLayout gridProductLayout;

        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            gridLayoutTitle = itemView.findViewById(R.id.grid_product_layout_title);
            gridLayoutViewAllbtn = itemView.findViewById(R.id.grid_product_layout_viewall_btn);
            gridProductLayout = itemView.findViewById(R.id.grid_layout);

        }

        private void setGridProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, String title, String color) {

            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            gridLayoutTitle.setText(title);

            for (HorizontalProductScrollModel model : horizontalProductScrollModelList) {
                if (!model.getProductID().isEmpty() && model.getProductTitle().isEmpty()) {
                    firebaseFirestore.collection("PRODUCTS")
                            .document(model.getProductID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        model.setProductTitle(task.getResult().getString("product_title"));
                                        model.setProductImage(task.getResult().getString("product_image_1"));
                                        model.setProductPrice(task.getResult().getString("product_price"));


                                        if (horizontalProductScrollModelList.indexOf(model) == horizontalProductScrollModelList.size() - 1) {
                                            setGridData(title, horizontalProductScrollModelList);
                                            if (!title.equals("")) {
                                                gridLayoutViewAllbtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        ViewAllActivity.horizontalProductScrollModelList = horizontalProductScrollModelList;
                                                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                                                        viewAllIntent.putExtra("layout_code", 1);
                                                        viewAllIntent.putExtra("title", title);
                                                        itemView.getContext().startActivity(viewAllIntent);
                                                    }
                                                });
                                            }

                                        }
                                    } else {
                                        ///do nothing
                                    }
                                }
                            });
                }
            }

            setGridData(title, horizontalProductScrollModelList);
        }

        private void setGridData(String title, List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
            for (int x = 0; x < 4; x++) {
                ImageView productImage = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_image);
                TextView productTitle = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_title);
                TextView productDescription = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_description);
                TextView productPrice = gridProductLayout.getChildAt(x).findViewById(R.id.h_s_product_price);

                Glide.with(itemView.getContext()).load(horizontalProductScrollModelList.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.img)).into(productImage);
                productDescription.setText(horizontalProductScrollModelList.get(x).getProductDescription());
                productPrice.setText("Rs." + horizontalProductScrollModelList.get(x).getProductPrice() + "/-");
                productTitle.setText(horizontalProductScrollModelList.get(x).getProductTitle());
                gridProductLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#FFFFFF"));
                if (!title.equals("")) {
                    int finalX = x;
                    gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                            productDetailsIntent.putExtra("PRODUCT_ID", horizontalProductScrollModelList.get(finalX).getProductID());
                            itemView.getContext().startActivity(productDetailsIntent);
                        }
                    });
                }
            }

        }

    }
}
