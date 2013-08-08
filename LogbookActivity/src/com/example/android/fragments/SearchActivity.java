
package com.example.android.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

public class SearchActivity extends SherlockFragmentActivity implements SearchListFragment.OnHeadlineSelectedListener, SearchView.OnQueryTextListener, ArticleFragment.OnPageFlipListener {
    
   SearchListFragment newFragment;
    /** Called when the activity is first created. */
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.news_articles_2);
       if (findViewById(R.id.fragment_container_s)!=null && savedInstanceState != null) {
           return;
       }
       if (findViewById(R.id.fragment_container_s) != null) {

           newFragment = new SearchListFragment();

           newFragment.setArguments(getIntent().getExtras());
           

           getSupportFragmentManager().beginTransaction()
                   .add(R.id.fragment_container_s, newFragment).commit();
       }
   }
    
    public void onArticleSelected(int position){

        ArticleFragment articleFrag = (ArticleFragment)
                getSupportFragmentManager().findFragmentById(R.id.article_fragment_2);

            /// POSITION = NUM FOR ARTICLE ID
        if (articleFrag != null) {
            articleFrag.updateArticleView(position);
            articleFrag.query="*"+getIntent().getExtras().getString("SEARCH")+"*";

        } else {
            
            ArticleFragment newFrag = new ArticleFragment();
            Bundle args = new Bundle();
            args.putString("QUERY", getIntent().getExtras().getString("SEARCH"));
            args.putInt(ArticleFragment.ARG_POSITION, position);
            newFrag.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            
            transaction.replace(R.id.fragment_container_s, newFrag);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    /**
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item1= menu.findItem(R.id.nav_next);
        item1.setEnabled(false);
        item1.setIcon(R.drawable.navigation_next_item_disabled);
        MenuItem item2= menu.findItem(R.id.nav_previous);
        item2.setEnabled(false);
        item2.setIcon(R.drawable.navigation_previous_item_disabled);
        return super.onPrepareOptionsMenu(menu);
    }**/

    public void onPageFlipListener(int direction){
//        if(direction==1){
//            if(thirdFragment!=null){
//                thirdFragment.next_log();
//            }else{
//                thirdFragment = new HeadlinesFragment();
//                thirdFragment.next_log();
//            }
//        } 
//        
//        if(direction==0){
//            if(thirdFragment!=null){
//                thirdFragment.previous_log();
//            }
//        }
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
        
        case R.id.action_refresh:
            refresh_cb_s();
            break;
        case R.id.nav_next:
            next_cb_s();
            break;
        case R.id.nav_previous:
            prev_cb_s();
            break;
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return true;
    }
    public void prev_cb_s(){
        SearchListFragment listFrag = (SearchListFragment)
                getSupportFragmentManager().findFragmentById(R.id.search_list_fragment);
        if(listFrag!=null){
            listFrag.prev_cb();
        }else{
            newFragment.prev_cb();
        }
    }
    public void next_cb_s(){
        SearchListFragment listFrag = (SearchListFragment)
                getSupportFragmentManager().findFragmentById(R.id.search_list_fragment);
        if(listFrag!=null){
            listFrag.next_cb();
        }else{
            newFragment.next_cb();
        }
    }
    
    public void refresh_cb_s(){
        SearchListFragment listFrag = (SearchListFragment)
                getSupportFragmentManager().findFragmentById(R.id.search_list_fragment);
        listFrag.refresh();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        
        //String str = userInput.getText().toString();
        //String str = "test";
        //buildList(new String[] {str});
        SearchListFragment listFrag = (SearchListFragment)
                getSupportFragmentManager().findFragmentById(R.id.search_list_fragment);
        if(listFrag!=null){
            listFrag.search(query);
        }else{
            newFragment.search(query);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        
        return false;
    }




}

