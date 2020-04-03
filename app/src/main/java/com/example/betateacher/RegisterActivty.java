package com.example.betateacher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;
import static com.example.betateacher.FBref.refAuth;
import static com.example.betateacher.FBref.refStudents;
import static com.example.betateacher.FBref.refTeachers;

public class RegisterActivty extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Phone";

    TextView tVtitle, tVregister, tVcustomer, tVmanager;
    EditText eTname, eTphone, eTcode, eTnc, eTnd;
    CheckBox cBstayconnect;
    Button btn, btnVerify;
    Switch Switch;
    boolean isCustomer;
    private String mVerificationId;
    String name, phone, numbercar,  numberDrivinig, TypeCar, text, uid = "";;
    Boolean stayConnect, registered, firstrun, Customer=false;
    Boolean mVerificationInProgress = false ,isUID = false;
    Spinner spinner;
    PhoneAuthCredential c;
    ValueEventListener usersListener;

    Student customer,currentCustomer;
    Teacher manager;
    FirebaseUser user;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        tVtitle = findViewById(R.id.tVtitle);
        eTname = findViewById(R.id.eTname);
        eTnd = findViewById(R.id.nd);
        eTnc=findViewById(R.id.ed1);
        eTphone = findViewById(R.id.eTphone);
        eTcode = findViewById(R.id.code);
        cBstayconnect = findViewById(R.id.cBstayconnect);
        tVregister = findViewById(R.id.tVregister);
        tVcustomer = findViewById(R.id.textView2);
        Switch = findViewById(R.id.switch1);
        tVmanager = findViewById(R.id.textView3);
        btn = findViewById(R.id.btn);
        btnVerify = findViewById(R.id.button2);
        spinner = findViewById(R.id.spinner1);
     /*   ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener( this);
       */ stayConnect = false;
        registered = true;

        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        firstrun = settings.getBoolean("firstRun", false);
        Toast.makeText(this, "" + firstrun, Toast.LENGTH_SHORT).show();
        if (firstrun) {
            tVtitle.setText("Register");
            eTname.setVisibility(View.VISIBLE);
            tVcustomer.setVisibility(View.VISIBLE);
            Switch.setVisibility(View.VISIBLE);
            tVmanager.setVisibility(View.VISIBLE);
            eTcode.setVisibility(View.VISIBLE);
            btnVerify.setVisibility(View.VISIBLE);
            btn.setText("Register");
            registered = false;
            logoption();
        } else regoption();

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                c=credential;
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    //etCode.setError("Invalid phone number.");
                }
                else
                if (e instanceof FirebaseTooManyRequestsException) {
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        Boolean isChecked = settings.getBoolean("stayConnect", false);
        Intent si = new Intent(RegisterActivty.this, CreditsActivity.class);
        if (refAuth.getCurrentUser() != null && isChecked) {
            stayConnect = true;
            startActivity(si);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (stayConnect) finish();
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        refAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(RegisterActivty.this, "Successful login", Toast.LENGTH_SHORT).show();
                            SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("stayConnect", cBstayconnect.isChecked());
                            editor.putBoolean("firstRun", false);
                            editor.commit();

                            FirebaseUser user = refAuth.getCurrentUser();
                            uid = user.getUid();
                            if (!isUID) {
                                if (Customer)
                                    refStudents.child("customer").child(name).child("uid").setValue(uid);
                                else
                                    refTeachers.child("Managers").child(name).child("uid").setValue(uid);
                            }

                            setUsersListener();

                        }

                        else {
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivty.this, "wrong!", Toast.LENGTH_LONG).show();
                        }
                    }


                    public void setUsersListener() {
                        user = refAuth.getCurrentUser();
                        usersListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    if (user.getUid().equals(data.getValue(Student.class).getUid())){
                                        currentCustomer=data.getValue(Student.class);
                                        if (currentCustomer.getisStudent()){
                                            Intent si = new Intent(RegisterActivty.this, CreditsActivity.class);
                                            startActivity(si);
                                        }
                                        else {
                                            Intent si = new Intent(RegisterActivty.this, CreditsActivity.class);
                                            startActivity(si);
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        };
                        refTeachers.child("Managers").addValueEventListener(usersListener);
                        refStudents.child("customer").addValueEventListener(usersListener);
                    }
                });
    };

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        mVerificationInProgress = true;
    }


    private boolean validatePhoneNumber() {
        String phoneNumber = eTphone.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            eTphone.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    private void regoption() {
        SpannableString ss = new SpannableString("Don't have an account?  Register here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                //   tVtitle.setText("Register");
                eTname.setVisibility(View.VISIBLE);
                tVcustomer.setVisibility(View.VISIBLE);
                eTnd.setVisibility(View.VISIBLE);
                eTnc.setVisibility(View.VISIBLE);
                Switch.setVisibility(View.VISIBLE);
                eTcode.setVisibility(View.VISIBLE);
                btnVerify.setVisibility(View.VISIBLE);
                tVmanager.setVisibility(View.VISIBLE);
                btn.setText("Register");
                registered = false;
                logoption();
            }
        };
        ss.setSpan(span, 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tVregister.setText(ss);
        tVregister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void logoption() {
        SpannableString ss = new SpannableString("Already have an account?  Login here!");
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                tVtitle.setText("Login");
                eTname.setVisibility(View.INVISIBLE);
                eTphone.setVisibility(View.INVISIBLE);
                btn.setText("Login");
                registered = true;
                regoption();
            }
        };
        ss.setSpan(span, 26, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tVregister.setText(ss);
        tVregister.setMovementMethod(LinkMovementMethod.getInstance());
    }


    public void Verify(View view) {
        if (!validatePhoneNumber()) {
            return;
        }
        startPhoneNumberVerification(eTphone.getText().toString());
    }

    public void logorreg(View view) {
        phone = eTphone.getText().toString();
        if (registered) {
            final ProgressDialog pd = ProgressDialog.show(this, "Login", "Connecting...", true);
            refAuth.signInWithCredential(c)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("stayConnect", cBstayconnect.isChecked());
                                editor.commit();
                                Log.d("MainActivity", "signinUserWithEmail:success");
                                Toast.makeText(RegisterActivty.this, "Login Success", Toast.LENGTH_LONG).show();
                                Intent si = new Intent(RegisterActivty.this, CreditsActivity.class);
                                startActivity(si);
                            } else {
                                Log.d("MainActivity", "signinUserWithEmail:fail");
                                Toast.makeText(RegisterActivty.this, "e-mail or password are wrong!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            if (Switch.isChecked()){
                Customer=true;
            }
            if (Customer)
                name = eTname.getText().toString();
            startPhoneNumberVerification(phone);
            numbercar = eTnc.getText().toString();
            phone = eTphone.getText().toString();
            isCustomer=true;
            final ProgressDialog pd = ProgressDialog.show(this, "Register", "Registering...", true);
        }

        if (Customer==false){

            name = eTname.getText().toString();
            numbercar = eTnc.getText().toString();
            phone = eTphone.getText().toString();
            isCustomer=false;
            startPhoneNumberVerification(phone);
            numberDrivinig = eTnd.getText().toString();
            if (name.isEmpty()) eTname.setError("you must enter a name");
            if (phone.isEmpty()) eTphone.setError("you must enter a phone number");
            if (numbercar.isEmpty())eTnc.setError("you must enter a numbercar");
            if(numberDrivinig.isEmpty())eTnd.setError("you must enter a numberDrivinig");
            final ProgressDialog pd = ProgressDialog.show(this, "Register", "Registering...", true);

        }

        startPhoneNumberVerification(phone);
        String code = eTcode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            eTcode.setError("Cannot be empty.");
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        refAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //      pd.dismiss();
                    SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("stayConnect", cBstayconnect.isChecked());
                    editor.putBoolean("firstRun", false);
                    editor.commit();
                    Log.d("MainActivity", "createUserWithEmail:success");
                    FirebaseUser user = refAuth.getCurrentUser();
                    uid = user.getUid();
                    if (Customer) {
                        customer=new Student (name, phone, TypeCar, uid,isCustomer);
                        refStudents.child("customer").child(phone).setValue(customer);
                        Toast.makeText(RegisterActivty.this, "Successful registration", Toast.LENGTH_LONG).show();
                        Intent si = new Intent(RegisterActivty.this, CreditsActivity.class);
                        startActivity(si);
                    }

                    else {
                        manager=new Teacher(name, phone,numbercar ,numberDrivinig,uid);
                        refTeachers.child("Managers").child(phone).setValue(manager);
                        Toast.makeText(RegisterActivty.this, "Successful registration", Toast.LENGTH_LONG).show();
                        Intent si = new Intent(RegisterActivty.this, CreditsActivity.class);
                        startActivity(si);
                    }

                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        eTcode.setError("Invalid code.");
                    }
                    else {
                        Log.w("MainActivity", "createUserWithCredential:failure", task.getException());
                        Toast.makeText(RegisterActivty.this, "User create failed.", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }


    public void visible1(View view) {
        if (Switch.isChecked()){
            Customer=true;
        }
        if (Customer) {
            spinner.setVisibility(View.VISIBLE);
            eTnd.setVisibility(View.INVISIBLE);
            Toast.makeText(RegisterActivty.this, "hello customer ", Toast.LENGTH_LONG).show();

        }
        if(Customer==false){
            spinner.setVisibility(View.INVISIBLE);
            eTnd.setVisibility(View.VISIBLE);
            Toast.makeText(RegisterActivty.this, "hello manager ", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TypeCar = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }
}
