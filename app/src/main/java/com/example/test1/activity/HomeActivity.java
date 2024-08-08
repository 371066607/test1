package com.example.test1.activity;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.test1.R;
import com.example.test1.adpter.MyPagerAdapter;
import com.example.test1.entity.TabEntity;
import com.example.test1.fragment.HomeFragment;
import com.example.test1.fragment.MyFragment;
import com.example.test1.fragment.NewsFragment;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {


    private String[] mTitles = {"首页", "资讯", "我的"};
    private int[] mIconUnselectIds = {
            R.drawable.home_unselect, R.drawable.collect_unselect,
            R.drawable.my_unselect};
    private int[] mIconSelectIds = {
            R.drawable.home_selected, R.drawable.collect_select,
            R.drawable.my_selected};

    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private ViewPager viewPager;
    private CommonTabLayout commonTabLayout;

    @Override
    protected void initView() {
        viewPager = findViewById(R.id.viewPager);
        commonTabLayout = findViewById(R.id.commonTabLayout);
    }

    @Override
    protected void initData() {
        mFragments.add(HomeFragment.newInstance());
        mFragments.add(NewsFragment.newInstance());
        mFragments.add(MyFragment.newInstance());
        for (int i = 0; i < mTitles.length; i++){
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        commonTabLayout.setTabData(mTabEntities);
        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }
            @Override
            public void onTabReselect(int position) {}
        });
        viewPager.setOffscreenPageLimit(mFragments.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                commonTabLayout.setCurrentTab(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(),mTitles,mFragments));
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_home;
    }
}