package com.example.myapplication2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistroFragment extends Fragment {

    /** @noinspection RegExpUnnecessaryNonCapturingGroup, RegExpRedundantEscape */
    String patternString = "(?:[a-z0-9!#$%&'*+/=?^_`{-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+\\]))";

    Pattern pattern = Pattern.compile(patternString);
    EditText txtRut, txtNombre, txtCorreo, txtClave;
    String strRut, strNombre, strCorreo, strClave;

    FragmentManager fragmentManager;


    public RegistroFragment() {
        // Required empty public constructor
    }

    public static RegistroFragment newInstance() {
        return new RegistroFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        fragmentManager = requireActivity().getSupportFragmentManager();
        Button backButton = (Button) view.findViewById(R.id.btnBack);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });

        Button nextButton = (Button) view.findViewById(R.id.btn_continue);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Continue(v);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtNombre = view.findViewById(R.id.editTextNombre);
        txtRut = view.findViewById(R.id.editTextRut);
        txtCorreo = view.findViewById(R.id.editTextEmail);
        txtClave = view.findViewById(R.id.editTextClave);

    }

    public void Continue(View view) {
        strNombre = txtNombre.getText().toString();
        strRut = txtRut.getText().toString();
        strCorreo = txtCorreo.getText().toString();
        strClave = txtClave.getText().toString();

        if (!RutValidator.validateRut(strRut).equals("1")) {
            Toast.makeText(getActivity(), RutValidator.validateRut(strRut), Toast.LENGTH_SHORT).show();
            return;
        }

        if (strNombre.length() < 4) {
            Toast.makeText(getActivity(), "El nombre debe tener al menos 4 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pattern.matcher(strCorreo).matches()) {
            Toast.makeText(getActivity(), "El correo no es válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (strClave.length() < 5) {
            Toast.makeText(getActivity(), "La clave debe tener al menos 5 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        RegistroFragment2 nextFrag = RegistroFragment2.newInstance();

        Bundle args = new Bundle();
        args.putString("strRut", strRut);
        args.putString("strNombre", strNombre);
        args.putString("strCorreo", strCorreo);
        args.putString("strClave", strClave);

        nextFrag.setArguments(args);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction
                .setCustomAnimations(
                        R.anim.slide_in_right,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out_right  // popExit
                )
                .add(R.id.FrameContainer, nextFrag,"findThisFragment")
                .addToBackStack(null)
                .commit();
    }
}