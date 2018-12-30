package apps.raymond.friendswholift;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import apps.raymond.friendswholift.DialogFragments.EditListItem;

/*
Class that generates the ListView view when 'Log' button is clicked.
 */
public class LiftsList extends AppCompatActivity implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener {
    final String[] from = {"type", "weight"};
    final int[] to = {R.id.type_text, R.id.weight_text};

    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lift_list);

        //This cursor contains all the data from our SQLite database lifts table.
        Log.d("Tag","Populating cursor.");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        Cursor data = dataBaseHelper.getAllLifts();

        ListView listView = (ListView) findViewById(R.id.list);

        CustomLiftAdapter customLiftAdapter = new CustomLiftAdapter(this,
                R.layout.lift_details,data, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView.setAdapter(customLiftAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    /*
    onItemLongClick will open a dialog that will prompt user for Removal of the item from table.
    OnItemClick will open a dialog (the same input_pr dialog) for the user to edit the item.
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long arg3){
        Toast.makeText(LiftsList.this, "You clicked an item for short time.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("EditItemDialog");
        if (prev != null){
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment newFragment = EditListItem.newInstance();
        newFragment.show(ft, "EditItemDialog");

        Toast.makeText(LiftsList.this, "You clicked an item for long time.",Toast.LENGTH_SHORT).show();
        return false;
    }

    //@Override
    public void DeleteItem(Context context, long id){
        //The id is passed from ListView via Interface.
        boolean deleteLift = dataBaseHelper.RemoveLift(context, id);

        if (deleteLift == true){
            Toast.makeText(LiftsList.this, "Lift deleted.",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LiftsList.this, "There was an error updating the database.",Toast.LENGTH_SHORT).show();
        }
    }
}

/*
onItemLongClick will open a dialog, depending on input received in dialog, we have to delete item or not.
Therefore, the dialog needs an interface to call the delete method on the item. The databasehelper will have have delete method.
 */