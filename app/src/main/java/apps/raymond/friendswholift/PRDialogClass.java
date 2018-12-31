package apps.raymond.friendswholift;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class PRDialogClass extends DialogFragment {

    private EditText prinput;
    private Spinner prspinner;
    public String prtype;
    public OnPRInputInterface prInputListener;
    private int spos;

    public interface OnPRInputInterface {
        //This interface calls a method from MainActivity (i.e. StorePR).
        void StorePR(String input, String prtype);
        void AddData(String input, String prtype);
    }

    @Override
    public void onStart(){
        super.onStart();
        getDialog().getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        getDialog().setTitle("Define your PR");
        View view = inflater.inflate(R.layout.pr_dialog, container, false);

        Button cancelbut = view.findViewById(R.id.cancel_button);
        Button prsavebut = view.findViewById(R.id.prsave_button);
        prinput = view.findViewById(R.id.prInput);
        prspinner = view.findViewById(R.id.liftsSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
          getActivity().getBaseContext(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.array_liftsSpinner)
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        prspinner.setAdapter(adapter);

        prsavebut.setOnClickListener(saveprclicked);
        cancelbut.setOnClickListener(cancelprclicked);
        prspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id){
                prtype = prspinner.getSelectedItem().toString();
                spos = position;
            }

            public void onNothingSelected(AdapterView parent){
            }
        });

        return view;
    }

    public View.OnClickListener saveprclicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(TextUtils.isEmpty(prinput.getText().toString())){
                prinput.setError("This field cannot be empty.");
                return;
            } else if(spos == 0) {
                Toast.makeText(getActivity().getBaseContext(),"SELECT A TYPE",
                        Toast.LENGTH_LONG).show();
            } else {
                String input = prinput.getText().toString();
                Log.d("Storing input", "Stored " + input + " as our input value");
                //Calls the StorePR method from the MainActivity via the Interface.
                prInputListener.StorePR(input, prtype);
                prInputListener.AddData(input, prtype);
                getDialog().dismiss();
            }
        }
    };

    public View.OnClickListener cancelprclicked = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            getDialog().dismiss();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            prInputListener = (OnPRInputInterface) getActivity();
        } catch (ClassCastException e){
            Log.e("PRDialogClass", "ClassCastException: " + e.getMessage() );
        }
    }
}
