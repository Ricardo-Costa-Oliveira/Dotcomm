/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.ubi.di.pdm.swipe;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;


public class MainActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;

    private static final int PageNumber = 1;
    private static Vector<String> FILES_IN_DIR = new Vector<String>();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        //final android.app.ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        //actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                //actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        /*for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }*/

        String [][] example_texts = getExampleTexts();
        FILES_IN_DIR = directoryListofFiles(getApplicationContext());
    }


    /*After the system calls onCreateOptionsMenu(), it retains an instance of the Menuyou populate
    and will not call onCreateOptionsMenu() again unless the menu is invalidated for some reason.
    However, you should use onCreateOptionsMenu() only to create the initial menu state and not to
    make changes during the activity lifecycle.*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.create_new_file:
                Log.i("Menu", "create_new");
                Intent iActivity = new Intent(this, CreateFile.class);//
                iActivity.putExtra("string1", "if you are able to do this, you are FABULOUS");
                startActivity(iActivity);
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

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        FILES_IN_DIR = directoryListofFiles(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(1).getSubMenu().getItem(1).setEnabled(false);
        menu.getItem(1).getSubMenu().getItem(2).setEnabled(false);
        menu.getItem(1).getSubMenu().getItem(3).setEnabled(false);
        menu.getItem(0).setVisible(false);
        menu.getItem(2).setVisible(false);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return new LaunchpadSectionFragment();
        }

        @Override
        public int getCount() {
            return PageNumber;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }
    private String[][] getExampleTexts(){
        Resources res = getResources();
        TypedArray ta = res.obtainTypedArray(R.array.text_examples);
        int n = ta.length();
        String[][] array = new String[n][];
        for(int i = 0;i<n;++i){
            int id = ta.getResourceId(i, 0);
            if (id > 0) {
                array[i] = res.getStringArray(id);
                WriteBtn(this,array[i][0].toString(), array[i][1].toString());
                Log.i("ARRAY", String.valueOf(array[i][0].toString()+"---->" +array[i][1].toString()));
            } else {
                Log.e("GET EXAMPLES","ESTA ALGO DE ERRADO COM O XAML");
            }
        }
        ta.recycle(); // Important!
        return array;
    }
    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class LaunchpadSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

            // Demonstration of a collection-browsing activity.
            rootView.findViewById(R.id.listFiles)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (FILES_IN_DIR.size() > 0) {
                                Intent intent = new Intent(getActivity(), ListView.class);//CollectionDemoActivity
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "Ainda nao tem ficheiros!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


            /*rootView.findViewById(R.id.demo_external_activity)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create an intent that asks the user to pick a photo, but using
                            // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                            // the application from the device home screen does not return
                            // to the external activity.
                            Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                            externalActivityIntent.setType("image/*");
                            externalActivityIntent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            startActivity(externalActivityIntent);
                        }
                    });*/
            rootView.findViewById(R.id.exit)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getActivity().finish();
                            System.exit(0);
                        }
                    });

            return rootView;
        }
    }

    // write text to file
    public void WriteBtn(Context ctx, String file, String text) {
        // add-write text into file
        try {
            FileOutputStream fileout = ctx.openFileOutput(file, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(text);
            outputWriter.close();

            //display file saved message
            //Toast.makeText(ctx, "Ficheiro Gravado!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Vector<String> directoryListofFiles(Context ctx){
        Vector<String> all_texts = new Vector<>();
        File dirFiles = ctx.getFilesDir();
        for (String strFile : dirFiles.list()) {
            all_texts.add(strFile);
            Log.i("TAG", strFile);
        }
        return all_texts;
    }
}
