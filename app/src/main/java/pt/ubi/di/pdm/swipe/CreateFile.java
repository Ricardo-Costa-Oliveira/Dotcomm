package pt.ubi.di.pdm.swipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class CreateFile extends Activity {
        /** Called when the activity is first created. */
        EditText titleEditText;
        EditText textEditText;
    @Override
    public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_file);

            titleEditText = (EditText) findViewById(R.id.entry_title);
            textEditText = (EditText) findViewById(R.id.entry_text);




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
                Log.i("Menu","create_new");
                Intent iActivity = new Intent (this,CreateFile.class);//
                iActivity.putExtra("string1","if you are able to do this, you are FABULOUS");
                startActivity(iActivity);
                //ainda tenho de ir buscar o resultado do intent

                return true;
            case R.id.save:
                String title = titleEditText.getText().toString();
                if (title.isEmpty() || title.matches("^\\s*$"))
                    Toast.makeText(this, "É obrigatório definir um título.", Toast.LENGTH_SHORT).show();
                else
                    WriteBtn(titleEditText.getText().toString(), textEditText.getText().toString());
                Log.i("Menu", "open");
                return true;
            case R.id.delete_file:
                if (directoryListofFiles().size() > 0) {
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
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.getItem(0).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(1).getSubMenu().getItem(2).setEnabled(false);
        menu.getItem(1).getSubMenu().getItem(1).setEnabled(false);
        return true;
    }
    // write text to file
    public void WriteBtn(String file, String text) {
        // add-write text into file
        try {
            FileOutputStream fileout=openFileOutput(file, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write(text);
            outputWriter.close();

            //display file saved message
            Toast.makeText(this, "Ficheiro Gravado!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.i("Ficheiros","Nao gravou ficheiro que criou");
            e.printStackTrace();
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
}
