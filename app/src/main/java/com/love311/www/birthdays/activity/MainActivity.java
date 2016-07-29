package com.love311.www.birthdays.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.love311.www.birthdays.R;
import com.love311.www.birthdays.fragment.BirthdayFragment;
import com.love311.www.birthdays.fragment.GiftFragment;
import com.love311.www.birthdays.fragment.MsgFragment;
import com.love311.www.birthdays.webview.BackHandledFragment;
import com.love311.www.birthdays.webview.BackHandledInterface;
import com.zhy.autolayout.AutoLayoutActivity;

public class MainActivity extends AutoLayoutActivity implements BackHandledInterface{

    private TabLayout mTablayout;
    private ViewPager mViewPager;

    private TabLayout.Tab mBirth;
    private TabLayout.Tab mMsg;
    private TabLayout.Tab mGift;
    private TextView mAddBirth;
    private TextView mTopText;
    private BackHandledFragment mBackHandedFragment;
    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvents();
    }

    //
    @Override
    public void onBackPressed() {
        if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                if((System.currentTimeMillis()-exitTime) > 2000){
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
            }else{
                getSupportFragmentManager().popBackStack(); //fragment 出栈
        }
        }
    }
    private void initEvents() {

        mTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == mTablayout.getTabAt(0)) {
                    mBirth.setIcon(getResources().getDrawable(R.mipmap.birthday_blue));
                    mViewPager.setCurrentItem(0);
                    mTopText.setText("生日管家");
                    mAddBirth.setVisibility(View.VISIBLE);
                } else if (tab == mTablayout.getTabAt(1)) {
                    mMsg.setIcon(getResources().getDrawable(R.mipmap.love_msg_blue));
                    mViewPager.setCurrentItem(1);
                    mTopText.setText("祝福短信");
                    mAddBirth.setVisibility(View.INVISIBLE);
                } else if (tab == mTablayout.getTabAt(2)) {
                    mGift.setIcon(getResources().getDrawable(R.mipmap.gift_blue));
                    mViewPager.setCurrentItem(2);
                    mTopText.setText("礼物选择");
                    mAddBirth.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab == mTablayout.getTabAt(0)) {
                    mBirth.setIcon(getResources().getDrawable(R.mipmap.birthday));
                } else if (tab == mTablayout.getTabAt(1)) {
                    mMsg.setIcon(getResources().getDrawable(R.mipmap.love_msg));
                } else if (tab == mTablayout.getTabAt(2)) {
                    mGift.setIcon(getResources().getDrawable(R.mipmap.gift));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
//            if((System.currentTimeMillis()-exitTime) > 2000){
//                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    private void initViews() {

        mTablayout= (TabLayout) findViewById(R.id.tabLayout);
        mViewPager= (ViewPager) findViewById(R.id.view_pager);
        mAddBirth = (TextView) findViewById(R.id.add_birth);
        mTopText = (TextView) findViewById(R.id.tv_top);
        mAddBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddBirthActivity.class);
                startActivity(intent);
            }
        });
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            private String[] mTitles = new String[]{"生日", "祝福短信", "礼物"};

            @Override
            public Fragment getItem(int position) {
                if (position == 1) {
                    return new MsgFragment();
                } else if (position == 2) {
                    return new GiftFragment();
                }
                return new BirthdayFragment();
            }

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }

        });

        mTablayout.setupWithViewPager(mViewPager);
        mTablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mBirth = mTablayout.getTabAt(0);
        mMsg = mTablayout.getTabAt(1);
        mGift = mTablayout.getTabAt(2);

        mBirth.setIcon(getResources().getDrawable(R.mipmap.birthday_blue));
        mMsg.setIcon(getResources().getDrawable(R.mipmap.love_msg));
        mGift.setIcon(getResources().getDrawable(R.mipmap.gift));
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }
}
