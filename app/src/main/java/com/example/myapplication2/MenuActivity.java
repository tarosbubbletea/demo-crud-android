package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends AppCompatActivity {


    FragmentManager fragmentManager = getSupportFragmentManager();
    private static final String REST = "https://link/api/";
    private static final String BearerToken = "";
    private Context context = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        PersonasAdapter adapter = new PersonasAdapter(null, MenuActivity.this);
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.box),
                new OnApplyWindowInsetsListener() {
                    @NonNull
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                        // Adjusting padding only on the top to account for status bar
                        v.setPadding(v.getPaddingLeft(), insets.getSystemWindowInsetTop(),
                                v.getPaddingRight(), v.getPaddingBottom());
                        return insets;
            }
        });

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public okhttp3.Response intercept(@NotNull Chain chain) throws java.io.IOException {
                        Request originalRequest = chain.request();

                        Request.Builder builder = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + BearerToken);

                        Request newRequest = builder.build();
                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(REST)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PersonaGETService api = retrofit.create(PersonaGETService.class);
        UserDELETEService api2 = retrofit.create(UserDELETEService.class);
        Call<List<Persona2>> call = api.getPersonas("persona");


        class CustomCallback<T> implements Callback<T> {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                if (response.isSuccessful()) {
                    List<Persona2> personas = (List<Persona2>) response.body();
                    adapter.personas = personas;
                    adapter.personas_orig = personas;
                    adapter.notifyDataSetChanged();

                } else {
                    // it won't fail so no need :)
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        }

        call.enqueue(new CustomCallback<>());

        Button boxA = findViewById(R.id.boxA);
        boxA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAddActivity(RegistroFragment.class, view, null);
            }
        });

        Button boxE = findViewById(R.id.boxE);
        boxE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getSelectedPersonas().size() == 0) return;
                if (adapter.getSelectedPersonas().size() > 1) return;
                List<Persona2> editGroup = new ArrayList<>(adapter.getSelectedPersonas());
                callAddActivity(EditarFragment.class, view, editGroup.get(0));
            }
        });



        EditText editText = findViewById(R.id.txtSearch);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filterData(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ImageButton boxS = findViewById(R.id.boxS);
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        boxS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(rotateAnimation);
                editText.setText("");
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View v = recyclerView.getChildAt(i);
                    CheckBox cb = v.findViewById(R.id.select_button);
                    cb.setChecked(false);
                }
                adapter.resetSelectedPersonas();
                adapter.notifyDataSetChanged();
                Call<List<Persona2>> callt = api.getPersonas("persona");
                callt.enqueue(new CustomCallback<>());
            }
        });

        Button boxD = findViewById(R.id.boxD);
        boxD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getSelectedPersonas().size() == 0) return;

                // confirm dialog
                new AlertDialog.Builder(context)
                        .setTitle("Confirmar eliminación")
                        .setMessage("¿Está seguro de que desea borrar estos elementos?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation

                                List<Persona2> deleteGroup = new ArrayList<>(adapter.getSelectedPersonas());
                                adapter.resetSelectedPersonas();
                                for (Persona2 persona : deleteGroup) {
                                    //retrofit DELETE
                                    Call<Void> call2 = api2.deleteUser("user/"+persona.getUser_id());
                                    call2.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(context, "Eliminada persona (ID interna #" + persona.getUser_id() + ")", Toast.LENGTH_SHORT).show();
                                                //shitty spammy fix
                                                Call<List<Persona2>> callt = api.getPersonas("persona");
                                                callt.enqueue(new CustomCallback<>());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Log.d("DELETE", "Failed to delete persona with id " + persona.getUser_id());
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        if (fragmentManager.getBackStackEntryCount() > 0) {
//            fragmentManager.popBackStackImmediate();
//            fragmentManager.beginTransaction()
//                    .setCustomAnimations(
//                    R.anim.slide_in_right,  // enter
//                    R.anim.fade_out,  // exit
//                    R.anim.fade_in,   // popEnter
//                    R.anim.slide_out_right  // popExit
//                        )
//                    .commit();
//        } else {
//            super.onBackPressed();
//        }
//    }

    // Quiero morir.
    /** @noinspection FieldMayBeFinal*/
    public static class PersonasAdapter extends RecyclerView.Adapter<PersonasAdapter.PersonViewHolder> {

        public List<Persona2> personas_orig;
        private List<Persona2> personas;
        private Context context;
        private List<Persona2> selectedPersonas = new ArrayList<>();


        public static class PersonViewHolder extends RecyclerView.ViewHolder {

            TextView name;
            CheckBox selectBox;

            public PersonViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.persona_name);
                selectBox = itemView.findViewById(R.id.select_button);
            }
        }

        public PersonasAdapter(List<Persona2> personas, Context context) {
            this.personas = personas;
            this.context = context;
        }

        public List<Persona2> getSelectedPersonas() {
            return selectedPersonas;
        }
        public void resetSelectedPersonas() { selectedPersonas = new ArrayList<>();}

        public void filterData(String query) {
            this.restoreData();
            this.personas = personas.stream()
                    .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            this.notifyDataSetChanged();
        }

        public void restoreData() {
            if (personas_orig == null) return;
            this.personas = this.personas_orig;
        }

        @NonNull
        @Override
        public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.persona_layout, parent, false);
            return new PersonViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PersonViewHolder holder, int position) {
            if (personas == null) return;
            Persona2 persona = personas.get(position);
            holder.name.setText(persona.getName());

            holder.selectBox.setChecked(selectedPersonas.contains(persona));

            //Set a click listener for select checkbox
            holder.selectBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // If it was checked, add it to the selected list.
                        selectedPersonas.add(persona);
                    } else {
                        // Otherwise remove it from the list.
                        selectedPersonas.remove(persona);
                    }
                    //notifyDataSetChanged(); //To refresh the view.
                    // no need since the user is clicking on the boxes
                }
            });

        }

        @Override
        public int getItemCount() {
            if (personas == null) return 0;
            return personas.size();
        }
    }



    public void callAddActivity(Class<? extends Fragment> fragmentClass, View view, @Nullable Persona2 persona) {
        Fragment fragment = null;

        try {
            // Create a new instance of the fragment.
            fragment = fragmentClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        if(fragment != null){
            if (persona != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("persona", persona);
                fragment.setArguments(bundle);
            }

            // Need a new transaction every time.
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.fade_out,  // exit
                            R.anim.fade_in,   // popEnter
                            R.anim.slide_out_right  // popExit
                    )
                    .add(R.id.FrameContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Log.e("TAG", "Failed to create a new instance of " + fragmentClass.getName());
        }
    }



}