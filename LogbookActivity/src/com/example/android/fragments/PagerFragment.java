/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import com.example.my_ui.LogActivity.LoadLogTask;

public class PagerFragment extends Fragment {
    private static final int NUM_PAGES = 100;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        setRetainInstance(true);
        if (savedInstanceState != null) { //TODO: apply this more frequently
            return this.getView();
        }
     // Instantiate a ViewPager and a PagerAdapter.
        
        
        
        return inflater.inflate(R.layout.flipper, container, false);
    }
   
public void onStart(){
    super.onStart();
    mPager = (ViewPager) getActivity().findViewById(R.id.pager);
    mPagerAdapter = new ScreenSlidePagerAdapter(this);
    mPager.setAdapter(mPagerAdapter);
    
}


  

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(android.support.v4.app.Fragment fragment) {
            super(fragment.getChildFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            ArticleFragment artFrag = new ArticleFragment();
            Bundle args = new Bundle();
            args.putInt(ArticleFragment.ARG_POSITION, position);
            artFrag.setArguments(args);
            
            return artFrag;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
    }
