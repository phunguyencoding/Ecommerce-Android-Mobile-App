package group15.finalassignment.ecommerce.View;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import group15.finalassignment.ecommerce.R;

public class OnBoardingScreenActivity extends AppCompatActivity{

    private ViewPager mSlideViewPaper;
    private LinearLayout mDotLayout;

    private TextView[] mDots;

    private SliderAdapter sliderAdapter;

    private Button mNextBtn;
    private Button mBackBtn;
    private Button mFinishBtn;

    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screen);

        mSlideViewPaper = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        mNextBtn = (Button) findViewById(R.id.nextBtn);
        mBackBtn = (Button) findViewById(R.id.prevBtn);
        mFinishBtn = (Button) findViewById(R.id.finishBtn);

        sliderAdapter = new SliderAdapter(this);

        mSlideViewPaper.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPaper.addOnPageChangeListener(viewListener);

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSlideViewPaper.setCurrentItem(mCurrentPage + 1);

            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSlideViewPaper.setCurrentItem(mCurrentPage - 1);

            }
        });

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveToMainActivity = new Intent(OnBoardingScreenActivity.this, MainActivity.class);
                startActivity(moveToMainActivity);
                finish();
            }
        });

    }


    public void addDotsIndicator(int position) {

        mDots = new TextView[2];
        mDotLayout.removeAllViews();

        mDots = new TextView[2];
        for (int i = 0; i< mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.black_light));

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.grey_light));
        }

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;

            if (position == 0) {
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mFinishBtn.setEnabled(false);
                mNextBtn.setVisibility(View.VISIBLE);
                mBackBtn.setVisibility(View.INVISIBLE);
                mFinishBtn.setVisibility(View.INVISIBLE);


                mNextBtn.setText("Next");
                mBackBtn.setText("");
                mFinishBtn.setText("");

            } else if(position == mDots.length - 1) {

                mNextBtn.setEnabled(false);
                mBackBtn.setEnabled(true);
                mFinishBtn.setEnabled(true);
                mNextBtn.setVisibility(View.INVISIBLE);
                mBackBtn.setVisibility(View.VISIBLE);
                mFinishBtn.setVisibility(View.VISIBLE);


                mNextBtn.setText("");
                mBackBtn.setText("Back");
                mFinishBtn.setText("Finish");

            } else {

                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mFinishBtn.setEnabled(false);
                mBackBtn.setVisibility(View.VISIBLE);
                mFinishBtn.setVisibility(View.INVISIBLE);

                mNextBtn.setText("Next");
                mBackBtn.setText("Back");
                mFinishBtn.setText("");
            }


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


}
