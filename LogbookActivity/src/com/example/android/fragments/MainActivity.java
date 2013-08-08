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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

public class MainActivity extends SherlockFragmentActivity 
        implements LogbookFragment.OnLogbookSelectedListener, HeadlinesFragment.OnHeadlineSelectedListener, SearchView.OnQueryTextListener, ArticleFragment.OnPageFlipListener {
        private LogbookFragment firstFragment;
        private HeadlinesFragment secondFragment;
        private HeadlinesFragment thirdFragment;
        private ArticleFragment articleFrag;
        boolean article_flag = false;
        String logbooks = "";
        String logbook = "*";
        String FILTERS = "";
        int page = 1;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_articles);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        if (findViewById(R.id.fragment_container)!=null && savedInstanceState != null) {
            page = savedInstanceState.getInt("PAGE", 1);
            logbook = savedInstanceState.getString("LOGBOOK");
            if(logbook==null)
                logbook="*";
            if(thirdFragment!=null){
                thirdFragment.setLogbook(logbook);
                thirdFragment.setPage(page);
            }
//            
//            return;
//            if(fm.findFragmentByTag("")!=null){
//                
//            }
        }
        if (findViewById(R.id.fragment_container) == null && findViewById(R.id.left_bin) != null) { //Tablet, bins available
//            if(fm.findFragmentByTag("HEADLINE1")==null){
                firstFragment = new LogbookFragment();
                firstFragment.setArguments(getIntent().getExtras());
                secondFragment = new HeadlinesFragment();
                secondFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.left_bin, firstFragment,"LOGBOOK").replace(R.id.right_bin, secondFragment, "HEADLINE1").commit();
//            }else{
//                firstFragment = (LogbookFragment) fm.findFragmentByTag("LOGBOOK");
//                secondFragment = (HeadlinesFragment) fm.findFragmentByTag("HEADLINE1");
//            }
            
        }else if(findViewById(R.id.left_bin) == null){ //Handheld, bins not available
//            if(fm.findFragmentByTag("LOGBOOK") == null){
                firstFragment = new LogbookFragment();
                firstFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, firstFragment, "LOGBOOK").commit();
//            } else{
//                firstFragment = (LogbookFragment) fm.findFragmentByTag("LOGBOOK");
//            }
            
        }
       
    }
  

    public void onArticleSelected(int position) {
        /**
        ArticleFragment articleFrag = (ArticleFragment) getSupportFragmentManager().findFragmentById(R.id.article_fragment);

            /// POSITION = NUM FOR ARTICLE ID
        if (articleFrag != null) {
            articleFrag.updateArticleView(position);

        } else {
            
            ArticleFragment newFragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putInt(ArticleFragment.ARG_POSITION, position);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }**/
    }
    
    /**
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if(outState!=null){
            boolean[] fraglist = {false,false,false,false};
            
            if(firstFragment!=null){
                fraglist[0]=true;
            }
            if(secondFragment!=null){
                fraglist[1]=true;
                //outState.putStringArray("SECONDFRAG", secondFragment.getMeta_Values());
                //outState.putString("SECONDFRAG_IDS", secondFragment.getIdString());
            }
            if(thirdFragment!=null){
                fraglist[2]=true;
                outState.putStringArray("THIRDFRAG", thirdFragment.getMeta_Values());
                outState.putString("THIRDFRAG_IDS", thirdFragment.getIdString());
            }
            if(articleFrag!=null){
                fraglist[3]=true;
            }
            outState.putBooleanArray("FRAGLIST", fraglist);
            
        }
    }
    @Override
    public void onRestoreInstanceState(Bundle inState){
        super.onRestoreInstanceState(inState);
        if(inState!=null){
            boolean[] fraglist = inState.getBooleanArray("FRAGLIST");
            if(fraglist[0]){
                firstFragment = new LogbookFragment();
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.left_bin, firstFragment).commit();
            }
            if(fraglist[1] && inState.getStringArray("SECONDFRAG")!=null){
                secondFragment = new HeadlinesFragment();
                secondFragment.buildList(inState.getStringArray("SECONDFRAG"));
                secondFragment.setIdString(inState.getString("SECONDFRAG_IDS"));
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.right_bin, secondFragment).commit();
            }
        }
    }
    
    **/
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
       getSupportActionBar().setHomeButtonEnabled(true);
       
       SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
       searchView.setQueryHint("Search logs…");
       searchView.setOnQueryTextListener(this);
       
       menu.add("Search")
       .setIcon(R.drawable.action_search)
       .setActionView(searchView)
       .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
       
       inflater.inflate(R.menu.main, (com.actionbarsherlock.view.Menu) menu);
       
       return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_new:
                post_cb();
                break;
            case R.id.action_filter:
                filter_cb();
                break;
            case R.id.action_refresh:
                refresh_cb();
                break;
            case R.id.nav_next:
                next_cb();
                break;
            case R.id.nav_previous:
                prev_cb();
                break;
            case android.R.id.home:
                home_cb();
                break;
            

        }

        return true;
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
//      savedInstanceState.putInt("mMyCurrentPosition", mPager.getPosition());
      if(thirdFragment!=null){
          savedInstanceState.putInt("PAGE", thirdFragment.getPage());
          savedInstanceState.putString("LOGBOOK", thirdFragment.getLogbook());
      }
    }
   
    private void home_cb() {
        // TODO Auto-generated method stub
        
        if(articleFrag!=null){//tablet screen 2, navigate back to previous fragment
            if(articleFrag.isVisible()){
                this.onBackPressed();
                return;
            }
        }
        if(thirdFragment!=null){ // case: handheld screen 2, navigate back to home fragment
            if(thirdFragment.isVisible()){
                this.onBackPressed();
                return;
            }
            
        }
        if(firstFragment!=null){ //case: home screen, reset app
            if(firstFragment.isVisible()){
                NavUtils.navigateUpFromSameTask(this);
                return;
            }
        }
        //final logic?
        NavUtils.navigateUpFromSameTask(this);
        return;
    }
    private void post_cb(){
        //TODO: POST
        Intent postintent = new Intent(MainActivity.this, PostActivity.class);
//        searchintent.putExtra("SEARCH", "");
        postintent.putExtra("LOGBOOKS", firstFragment.logbook_name_string);
        startActivity( postintent );
    }
    private void refresh_cb(){
        /**
        if (findViewById(R.id.fragment_container) == null) {
        LogbookFragment listFrag = (LogbookFragment)
                getSupportFragmentManager().findFragmentById(R.id.headlines_fragment);
        listFrag.refresh();
        }else{
            firstFragment.refresh();
        }**/
        if(secondFragment != null)
            secondFragment.refresh();
        if(firstFragment != null)
            firstFragment.refresh();
        if(thirdFragment != null)
            thirdFragment.refresh();
        
    }
    private void next_cb(){
        if ((secondFragment) != null) { 
            if(secondFragment.isVisible())
                secondFragment.next_cb();
            page = secondFragment.getPage();
        }else{
            //firstFragment.next_cb();
        }
        if ((thirdFragment) != null) { 
            if(thirdFragment.isVisible())
                thirdFragment.next_cb();
            page = thirdFragment.getPage();
         }else{
             //firstFragment.next_cb();
         }
        if ((articleFrag) != null) { 
            if(articleFrag.isVisible())
                articleFrag.next();
         }
    }
    private void prev_cb(){
        if (secondFragment != null) {
         //   LogbookFragment listFrag = (LogbookFragment)
            if(secondFragment.isVisible())
                secondFragment.prev_cb();
            page = secondFragment.getPage();
            return;
        }else{
            //firstFragment.prev_cb();
        }
        if ((thirdFragment) != null) { 
            // LogbookFragment listFrag = (LogbookFragment)
           //      getSupportFragmentManager().findFragmentById(R.id.headlines_fragment);
            if(thirdFragment.isVisible())
                thirdFragment.prev_cb();
            page = thirdFragment.getPage();
            return;
             
         }
        if ((articleFrag) != null) { 
            if(articleFrag.isVisible())
                articleFrag.previous();
         }
    }
    private void filter_cb(){
               LayoutInflater li = LayoutInflater.from(this);
               View promptsView = li.inflate(R.layout.prompts, null);

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                       this);

               alertDialogBuilder.setView(promptsView);

               final EditText userInput = (EditText) promptsView
                       .findViewById(R.id.editText1);
               final EditText userInput2 = (EditText) promptsView
                       .findViewById(R.id.editText2);

               alertDialogBuilder
                   .setCancelable(true)
                   .setNegativeButton("Cancel",
                     new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog,int id) {
                       dialog.cancel();
                       }
                     })
                   .setPositiveButton("OK",
                     new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog,int id) {
                       
                           String str = userInput.getText().toString();
                           if(str!=""){
                               str="&tag="+str;
                           }
                           FILTERS = ""+str;
                           if(thirdFragment!=null)
                               thirdFragment.setFilter(FILTERS);
//                           Intent searchintent = new Intent(MainActivity.this, SearchActivity.class);
//                           searchintent.putExtra("SEARCH", str);
//                           startActivity( searchintent );
                           
                       }
                     });

               // create alert dialog
               AlertDialog alertDialog = alertDialogBuilder.create();

               // show it
               alertDialog.show();

           
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(thirdFragment!=null)
            thirdFragment.page = page;
    }

    @Override
    public void onHeadlineSelected(int position) {
        FragmentManager fm = this.getSupportFragmentManager();
        if (secondFragment!=null){//DEVICE: TABLET
            if (articleFrag!=null) { 
                if(articleFrag.isVisible()){ //DEVICE: TABLET, Article visible on right side
                    articleFrag.updateArticleView(position);
                    
                } else { // DEVICE: TABLET, CASE: LOADING FROM FIRST LAYOUT
                    thirdFragment = new HeadlinesFragment();
                    Bundle args1 = new Bundle();
                    args1.putString("LOGBOOK", logbook);
                    thirdFragment.setArguments(args1);
                    thirdFragment.setLogbook(secondFragment.getLogbook());
                    thirdFragment.setPage(secondFragment.getPage());
                    articleFrag = new ArticleFragment();
                    Bundle args = new Bundle();
                    args.putInt("PAGE", position);
                    args.putString("LOGBOOK", logbook);
                    args.putString("QUERY", "*");
                    args.putInt(ArticleFragment.ARG_POSITION, position);
                    articleFrag.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                        .remove(secondFragment)
                        .replace(R.id.left_bin, thirdFragment,"HEADLINE2")
                        .replace(R.id.right_bin, articleFrag)
                        .addToBackStack("")
                        .commit();
                
                }

            } else { //CASE: LOADING FROM FIRST LAYOUT, INITIALIZE LOGBOOKS N SUCH
                thirdFragment = new HeadlinesFragment();
                Bundle args1 = new Bundle();
                args1.putString("LOGBOOK", secondFragment.getLogbook());
                thirdFragment.setArguments(args1);
            
                articleFrag = new ArticleFragment();
                Bundle args = new Bundle();
                args.putInt("PAGE", position);
                args.putString("LOGBOOK", logbook);
                args.putString("QUERY", "*");
                args.putInt(ArticleFragment.ARG_POSITION, position);
                articleFrag.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                    .remove(secondFragment)
                    .replace(R.id.left_bin, thirdFragment,"HEADLINE2")
                    .replace(R.id.right_bin, articleFrag)
                    .addToBackStack("")
                    .commit();
            
        }
            } else { //case: second fragment == null : IE:skipped second fragment because the device is handheld, use third
            
                if(thirdFragment == null)
                    thirdFragment = ((HeadlinesFragment) fm.findFragmentByTag("HEADLINE2"));
                
            if(articleFrag==null){    
                articleFrag = new ArticleFragment();
                Bundle args = new Bundle();
                args.putInt(ArticleFragment.ARG_POSITION, position);
                args.putInt("PAGE", position);
                args.putString("LOGBOOK", logbook);
                args.putString("QUERY", "*");
                articleFrag.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, articleFrag)
                .addToBackStack(null)
                .commit();
            }else if(articleFrag.isVisible()){
//                Bundle args = new Bundle();
//                args.putInt(ArticleFragment.ARG_POSITION, position);
//                articleFrag.setArguments(args);
                articleFrag.updateArticleView(position);
            }else {
                articleFrag = new ArticleFragment();
                Bundle args = new Bundle();
                args.putInt(ArticleFragment.ARG_POSITION, position);
                args.putInt("PAGE", position);
                args.putString("LOGBOOK", logbook);
                args.putString("QUERY", "*");
                articleFrag.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, articleFrag)
                .addToBackStack(null)
                .commit();
            }
        }
        
        if(thirdFragment!=null){
            page = thirdFragment.getPage();
        }
        
        
    }
    public void onPageFlipListener(int direction){
        if(direction==1){
            if(articleFrag!=null){
                articleFrag.next();
            }
        } 
        
        if(direction==0){
            if(articleFrag!=null){
                articleFrag.previous();
            }
        }
    }
    @Override
    public void onLogbookSelected(String name) {
        
        if(secondFragment!=null){
            if(secondFragment.isVisible()){
                if(name.contains("All")){
                    logbook = "*";
                    secondFragment.setLogbook("*");
                    secondFragment.refresh();
                    return;
                }else{
                    name = name.replaceAll(" ", "%20");
                }
                secondFragment.setLogbook(name);
                logbook = name;
                secondFragment.refresh();
            }
        }else if(findViewById(R.id.fragment_container) != null){ //case: secondFragment == null IE: handheld device
            thirdFragment = new HeadlinesFragment();
            Bundle args = new Bundle();
            if(name.contains("All")){
               name = "*";
            }
            args.putString("LOGBOOK",name.replaceAll(" ", "%20"));
            thirdFragment.setArguments(args);
            thirdFragment.setLogbook(name.replaceAll(" ", "%20"));
            //thirdFragment.refresh();
            logbook = name;
            getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, thirdFragment).addToBackStack(null).commit();
        }
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        //String str = userInput.getText().toString();
        //buildList(new String[] {str});
        Intent searchintent = new Intent(MainActivity.this, SearchActivity.class);
        searchintent.putExtra("SEARCH", query);
        startActivity( searchintent );
        //new SearchLogTask().execute(str);
        return true;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        return false;
    }

}