package pt.ubi.di.pdm.swipe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListView extends Activity implements SearchView.OnQueryTextListener {

    private ArrayList<String> FILES_IN_DIR = new ArrayList<>();
    private StableArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        FILES_IN_DIR = directoryListofFiles();
        final android.widget.ListView listview = (android.widget.ListView) findViewById(R.id.listview);
        final ArrayList<String> list = FILES_IN_DIR;
        adapter = new StableArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        listview.setTextFilterEnabled(true);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent iActivity = new Intent(view.getContext(), CollectionDemoActivity.class);//
                Log.i("TAG", listview.getItemAtPosition(position).toString());
                iActivity.putExtra("position", listview.getItemAtPosition(position).toString());
                startActivityForResult(iActivity,1);

                //Toast.makeText(getApplicationContext(), "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view,
                                           final int position, long arg3) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                File file = new File(getFilesDir(), FILES_IN_DIR.get(position));
                                boolean deleted = file.delete();
                                if (deleted) {
                                    list.remove(item);
                                    adapter.notifyDataSetChanged();
                                    view.setAlpha(1);
                                    if (list.isEmpty()) {
                                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                    Toast.makeText(getApplicationContext(), "Ficheiro Eliminado", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                return true;
            }

        });
     }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        android.widget.ListView listview = (android.widget.ListView) findViewById(R.id.listview);
        if (TextUtils.isEmpty(newText))
        {
            listview.clearTextFilter();
        }
        else
        {
            listview.setFilterText(newText.toString());
        }
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(1).getSubMenu().getItem(1).setEnabled(false);
        menu.getItem(1).getSubMenu().getItem(2).setEnabled(false);
        menu.getItem(1).getSubMenu().getItem(3).setEnabled(false);
        menu.getItem(2).setVisible(false);
        return true;
    }

    /*After the system calls onCreateOptionsMenu(), it retains an instance of the Menuyou populate
    and will not call onCreateOptionsMenu() again unless the menu is invalidated for some reason.
    However, you should use onCreateOptionsMenu() only to create the initial menu state and not to
    make changes during the activity lifecycle.*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE );
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.create_new_file:
                Log.i("Menu","create_new");
                Intent iActivity = new Intent (this,CreateFile.class);//
                iActivity.putExtra("string1","if you are able to do this, you are FABULOUS");
                startActivity(iActivity);
                //ainda tenho de ir buscar o resultado do intent

                return true;
            case R.id.open:
                if (FILES_IN_DIR.size() > 0) {
                    Intent intent = new Intent(this, ListView.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Ainda nao tem ficheiros!", Toast.LENGTH_SHORT).show();
                }

                Log.i("Menu", "open");
                return true;
            case R.id.delete_file:
                if (FILES_IN_DIR.size() > 0) {
                    Intent intent = new Intent(this, ListView.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Ainda nao tem ficheiros!", Toast.LENGTH_SHORT).show();
                }
                Log.i("Menu", "delete_file");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private ArrayList<String> directoryListofFiles(){
        String text = "";
        ArrayList<String> all_texts = new ArrayList<>();
        File dirFiles = getFilesDir();
        for (String strFile : dirFiles.list()) {
            all_texts.add(strFile);
            Log.i("TAG", strFile);
        }
        return all_texts;
    }



    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}

