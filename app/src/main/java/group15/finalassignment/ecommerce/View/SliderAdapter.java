package group15.finalassignment.ecommerce.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import group15.finalassignment.ecommerce.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    //Arrays
    public int[] slide_images = {
            R.drawable.aboutapp,
            R.drawable.aboutus
    };

    public String[] slide_headings = {
            "About this App",
            "About us"
    };

    public String[] slide_descriptions = {
            "This app is the product for our final assignment of the course COSC2657 - Android Development. It is designed and used for E-Commerce mobile app when the growth of smartphone use has dramatically changed the retail landscape and consumer behavior in recent years.",
            "We are the 3rd-year students from RMIT University with a passion for programming \nAll members of the group 15:\n Nguyen Tran Phu - s3811248\n Le Ngoc Duy - s3757327\n Nguyen Quoc Minh - s3758994\n Nguyen Dang Truong Long - s3757333"
    };



    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desciption);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descriptions[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((RelativeLayout)object);

    }

}
