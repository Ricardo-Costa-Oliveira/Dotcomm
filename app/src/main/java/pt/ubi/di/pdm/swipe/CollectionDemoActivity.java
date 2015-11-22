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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class CollectionDemoActivity extends FragmentActivity implements AccelerometerListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

    /**
     * The {@link ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;

    private static final String PUNCTUATION = "\\p{Punct}+";
    private static final String VOWELS = "[aeiou]\\B";
    private static Vector<String> FILES_IN_DIR = new Vector<String>();

    private static final Pattern UNDESIRABLES_PUNCTUATION = Pattern.compile(PUNCTUATION); //pre-compile the regex
    private static final Pattern UNDESIRABLES_VOWELS = Pattern.compile(VOWELS);


    private static final Pattern TEXTFILES = Pattern.compile(".txt"); //pre-compile the regex
    //private static final Vector<String>  PONCTUATION = new Vector<String>(2);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        // Create an adapter that when requested, will return a fragment representing an object in
        // the collection.
        //
        // ViewPager and its adapters use support library fragments, so we must use
        // getSupportFragmentManager.
        FILES_IN_DIR = directoryListofFiles(getApplicationContext());

        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);

        Intent iCameFromActivity1 = getIntent();
        String file =iCameFromActivity1.getStringExtra("position");
        int i=0;
        for (i=0;i<FILES_IN_DIR.size();i++){
            if(FILES_IN_DIR.elementAt(i).toString().equals(file))
            break;
        }
        mViewPager.setCurrentItem(i);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int index = mViewPager.getCurrentItem();
        Fragment fragment =  mDemoCollectionPagerAdapter.getFragment(index);
        TextView title = ((TextView) fragment.getView().findViewById(R.id.details_entry_title));
        TextView title_textView = ((TextView) fragment.getView().findViewById(R.id.details_file_text));
        EditText textview = ((EditText) fragment.getView().findViewById(R.id.details_entry_text));
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, MainActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            case R.id.create_new_file:
                Log.i("Menu", "create_new");
                Intent iActivity = new Intent (this,CreateFile.class);//
                iActivity.putExtra("string1", "if you are able to do this, you are FABULOUS");
                startActivity(iActivity);
                FILES_IN_DIR = directoryListofFiles(this);
                return true;
            case R.id.open:
                if(fragment== null)
                    Log.i("Fragment", "fragment NULL");
                else{
                    if(title!= null && title_textView!= null && textview!= null ){
                        String words = ReadBtn(this, FILES_IN_DIR.elementAt(index));
                        //title_textView.setText(title_textView.getText().toString().replace(" (sem pontuação)", ""));
                        if(title_textView.getText().toString().contains("sem pontuação")){
                            String [] aux_title = title_textView.getText().toString().split("\\(sem pontuação");
                            title_textView.setText(aux_title[0]);
                        }
                        if(title_textView.getText().toString().contains("sem vogais")){
                            String [] aux_title = title_textView.getText().toString().split("\\(sem vogais");
                            title_textView.setText(aux_title[0]);
                        }
                        textview.setText(words);
                        Log.i("TAG", "Carregou ficheiro " + directoryListofFiles(this).elementAt(mViewPager.getCurrentItem()));
                    }
                }
                Log.i("Menu", "open");
                return true;

            case R.id.delete_file:
                if(fragment== null)
                    Log.i("Fragment", "fragment NULL");
                else{
                    if(textview!= null ){
                        File file = new File(getFilesDir(), FILES_IN_DIR.get(index));
                        boolean deleted = file.delete();
                        Toast.makeText(getApplicationContext(), "Ficheiro Eliminado", Toast.LENGTH_LONG).show();
                        Log.i("Menu", "delete_file");
                    }
                }

                return true;
            case R.id.save:
                if(fragment== null)
                    Log.i("Fragment", "fragment NULL");
                else{
                    if(textview!= null ){
                        WriteBtn(this, directoryListofFiles(this).elementAt(index), textview.getText().toString());
                    }
                }
                return true;
            case R.id.remove_punctuation_details:
                if(fragment== null)
                    Log.i("Fragment", "fragment NULL");
                else{
                    if(textview!= null ){
                        String words = textview.getText().toString().replace(PUNCTUATION, "");


                        String removed_chars = removedItensFromText(textview, PUNCTUATION).toString();
                        Log.i("OLA",removedItensFromText(textview, PUNCTUATION).toString());
                        String text = UNDESIRABLES_PUNCTUATION.matcher(words).replaceAll("");
                        if (textview.getText().toString().equals(text))
                            Log.i("TAG","Nao removeu");
                        else{
                            if(!title_textView.getText().toString().contains("sem pontuação"))
                                title_textView.setText(title_textView.getText()+" (sem pontuação "+removed_chars.replaceAll("\\[(.*?)\\]", "$1").replace(",","  ")+" )");
                            textview.setText(text);
                        }
                          Toast.makeText(this, "Removeu Pontuação!",Toast.LENGTH_SHORT).show();
                        Log.i("TAG", UNDESIRABLES_PUNCTUATION.matcher(words).replaceAll(""));
                    }
                }
                return true;
            case R.id.remove_vowels_details:
                if(fragment== null)
                    Log.i("Fragment", "fragment NULL");
                else{
                    if(textview!= null ){
                        // This should be fast enough for most purposes, assuming the JVM's
                        // regex engine optimizes the character class lookup.
                        // http://stackoverflow.com/questions/17531362/efficiently-removing-specific-characters-some-punctuation-from-strings-in-java
                        String words = textview.getText().toString().replace(VOWELS, "");
                        String removed_chars = removedItensFromText(textview, VOWELS).toString();

                        String text = UNDESIRABLES_VOWELS.matcher(words).replaceAll("");
                        if (textview.getText().toString().equals(text))
                            Log.i("TAG","Nao removeu");
                        else{
                            if(!title_textView.getText().toString().contains("sem vogais"))
                                title_textView.setText(title_textView.getText()+" (sem vogais " +removed_chars.replaceAll("\\[(.*?)\\]", "$1").replace(",","  ")+" )");
                            textview.setText(text);
                        }
                        Toast.makeText(this, "Removeu Vogais!",Toast.LENGTH_SHORT).show();
                        Log.i("TAG", UNDESIRABLES_VOWELS.matcher(words).replaceAll(""));
                    }
                }
                return true;


        }
        return super.onOptionsItemSelected(item);
    }
    public static List<String> removedItensFromText(TextView textview, String pattern){
        List<String> allMatches = new ArrayList<String>();
        Matcher m = Pattern.compile(pattern).matcher(textview.getText().toString());
        while (m.find()) {
            allMatches.add(m.group());
        }
        return removeDuplicated(allMatches);
    }
    public static List<String> removeDuplicated(List<String> allMatches){
        Set<String> hs = new HashSet<>();
        hs.addAll(allMatches);
        allMatches.clear();
        allMatches.addAll(hs);
        return allMatches;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(0).setVisible(false);
        return true;
    }
    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public static class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        private Map<Integer, Fragment> mPageReferenceMap = new HashMap<Integer, Fragment>();

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            args.putInt(DemoObjectFragment.ARG_OBJECT, i);
            fragment.setArguments(args);
            mPageReferenceMap.put(i, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            // For this contrived example, we have a 100-object collection.
            return FILES_IN_DIR.size();
        }
        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        public Fragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }
        /**
         * After an orientation change, the fragments are saved in the adapter, and
         * I don't want to double save them: I will retrieve them and put them in my
         * list again here.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container,position);
            mPageReferenceMap.put(position, fragment);
            return fragment;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }


    }
public static void remover(TextView textview, TextView title_textView, String s){
    // This should be fast enough for most purposes, assuming the JVM's
    // regex engine optimizes the character class lookup.
    // http://stackoverflow.com/questions/17531362/efficiently-removing-specific-characters-some-punctuation-from-strings-in-java
    Pattern UNDESIRABLE = Pattern.compile(s);
    String words = textview.getText().toString().replace(s, "");
    String removed_chars = removedItensFromText(textview, s).toString();

    String text = UNDESIRABLE.matcher(words).replaceAll("");
    if (textview.getText().toString().equals(text))
        Log.i("TAG","Nao removeu");
    else{
        if(!title_textView.getText().toString().contains("sem vogais"))
            title_textView.setText(title_textView.getText()+" (sem vogais " +removed_chars.replaceAll("\\[(.*?)\\]", "$1").replace(",","  ")+" )");
        textview.setText(text);
    }
    //Toast.makeText(getActivity(), "Removeu Pontuação!",Toast.LENGTH_SHORT).show();
    Log.i("TAG", UNDESIRABLES_VOWELS.matcher(words).replaceAll(""));
}
    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DemoObjectFragment extends Fragment {
        View view_a;
        public static final String ARG_OBJECT = "object";
        EditText textview;
        TextView title_textView;
        TextView title;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);

            final Bundle args = getArguments();
            final int posicao = args.getInt(ARG_OBJECT);
            title = ((TextView) rootView.findViewById(R.id.details_entry_title));
            title_textView = ((TextView) rootView.findViewById(R.id.details_file_text));
            textview = ((EditText) rootView.findViewById(R.id.details_entry_text));
            if (posicao>FILES_IN_DIR.size())
                return rootView;
            textview.setText(ReadBtn(getActivity(), directoryListofFiles(getActivity()).elementAt(posicao)));
            title.setText(directoryListofFiles(getActivity()).elementAt(posicao).replace(".txt", ""));





            // Create a LayoutParams...
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.FILL_PARENT);
// Quick & dirty pre-made list of text labels...
            List<String> removed_chars = removedItensFromText(textview, PUNCTUATION);
            Log.e("AQUI AQUI", removed_chars.toString());
// Get existing UI containers...
            LinearLayout nameButtons = (LinearLayout) rootView.findViewById(R.id.linearLayout2);
            Button tv = (Button) rootView.findViewById(R.id.puctuation_test);
            for (int i = 0; i < removed_chars.size(); i++) {
                // Grab the name for this "button"
                final String name = removed_chars.get(i).toString();

                tv = new Button(getContext());
                tv.setText(name);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("adasd", "Clicked button for " + name);
                        textview.setText(textview.getText().toString().replace(name, ""));
                        //remover(textview, title_textView, name);
                    }
                });

                nameButtons.addView(tv, params);
            }



            return rootView;
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

    public void onAccelerationChanged(float x, float y, float z) {
    }


    public void onShake(float force) {

        int index = mViewPager.getCurrentItem();
        Fragment fragment =  mDemoCollectionPagerAdapter.getFragment(index);

        if(fragment== null)
            Log.i("Sensor", "fragment NULL");
        else{
            TextView textview = (TextView) fragment.getView().findViewById(R.id.details_entry_text);
            TextView title_textView = ((TextView) fragment.getView().findViewById(R.id.details_file_text));
            if(textview!= null ){
                String words = textview.getText().toString().replace(PUNCTUATION, "");


                String removed_chars = removedItensFromText(textview, PUNCTUATION).toString();
                Log.i("OLA", removedItensFromText(textview, PUNCTUATION).toString());
                String text = UNDESIRABLES_PUNCTUATION.matcher(words).replaceAll("");
                if (textview.getText().toString().equals(text))
                    Log.i("TAG","Nao removeu");
                else{
                    if(!title_textView.getText().toString().contains("sem pontuação"))
                        title_textView.setText(title_textView.getText()+" (sem pontuação "+removed_chars.replaceAll("\\[(.*?)\\]", "$1").replace(",","  ")+" )");
                    textview.setText(text);
                }
                          Toast.makeText(this, "Removeu Pontuação!",Toast.LENGTH_SHORT).show();
                Log.i("TAG", UNDESIRABLES_PUNCTUATION.matcher(words).replaceAll(""));
            }
        }

        // Called when Motion Detected
        //Toast.makeText(getBaseContext(), "Motion Detected", Toast.LENGTH_SHORT).show();

    }
    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        Log.i("TAG", "SIZE "+String.valueOf(fragments.size()));
        for(Fragment fragment : fragments){
            Log.i("TAG", "ID "+fragment.getId());
            //if(fragment != null && fragment.isVisible())
               // return fragment;
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Sensor", "Service  destroy");
        if (AccelerometerManager.isListening()) {
            AccelerometerManager.stopListening();
        }

    }



    public static void saveToFile(String filename, Context ctx, String text) {
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(text.getBytes());
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String readFromFile(String filename, Context ctx) {
        FileInputStream fis = null;
        String str= "";
        try {
            fis = ctx.openFileInput(filename);
            byte[] buffer = new byte[(int) fis.getChannel().size()];
            fis.read(buffer);

            for(byte b:buffer) str+=(char)b;


            Log.i("TAG", String.format("GOT: [%s]", str));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (fis!=null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }
    }



    // write text to file
    public static void WriteBtn(Context ctx, String file, String text) {
        // add-write text into file
        try {
            FileOutputStream fileout=ctx.openFileOutput(file, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(text);
            outputWriter.close();

            //display file saved message
            Toast.makeText(ctx, "Ficheiro Gravado!",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Read text from file
    public static String ReadBtn(Context ctx, String file) {
        //reading text from file
        InputStreamReader InputRead=null;
        String s="";
        try {
            FileInputStream fileIn=ctx.openFileInput(file);
            InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[100];

            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }

            //Toast.makeText(ctx, s,Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(ctx, "Nao consegui carregar ficheiro",Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
        }finally {
            if (InputRead != null) {
                try {
                    InputRead.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return s;
        }
    }
}

