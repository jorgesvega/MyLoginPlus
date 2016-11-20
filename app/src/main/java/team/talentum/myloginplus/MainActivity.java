package team.talentum.myloginplus;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient apiCliente;
    private SignInButton loginButton;
    private Button logout, desconectar;
    private final int RESPUESTA = 12;
    private TextView tvNombre, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (SignInButton)findViewById(R.id.sibLogin);
        logout = (Button)findViewById(R.id.bLogout);
        desconectar = (Button)findViewById(R.id.bDesconectar);
        tvNombre = (TextView)findViewById(R.id.tvNombre);
        tvEmail = (TextView)findViewById(R.id.tvEmail);


        // Creation of a GoogleSIgnInOptions object to configure the info needed of the user
        // It is done throw his builder method
        // GoogleSignInOptions.DEFAULT_SIGN_IN offer the basic user information
        // Additionaly, we can user other methods, like .requestEmail() to retrieve user mail account
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // Creation of a GoogleApiClient object
        // The 'gso' object reference is the arg for the addApi() method
        apiCliente = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(GOOGLE_SIGN_IN_API, gso).build();

        // enableAutoManage() manages automatically the GPS connection
        // so, we have to implement in the class the interface OnConnectionFailedListener
        // and define the method onConnectionFailed()
        // all this outside onCreate

        // Call getSignInIntent() method adding our apiCLiente
        // With this intent, we create a new activity, who ask user for his account
        // It return the result of all, wich we can manage whit onActivityResult(), with the constant respuesta
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logueo = Auth.GoogleSignInApi.getSignInIntent(apiCliente);
                startActivityForResult(logueo, RESPUESTA);
            }
        });


        //This code provide functionality to the logout and disconnect buttons
        logout.setOnClickListener(new View.OnClickListener(){
                                      @Override
                                      public void onClick(View view) {
                                          Auth.GoogleSignInApi.signOut(apiCliente).setResultCallback(
                                                  new ResultCallback<Status>() {
                                                      @Override
                                                      public void onResult(@NonNull Status status) {
                                                          updateUI(false);
                                                      }
                                                  });
                                      }
                                  });

        desconectar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Auth.GoogleSignInApi.revokeAccess(apiCliente).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                updateUI(false);
                            }
                        });
            }
        });

    }

    // Send a toast to the user with the connection error
    @Override
    public void onConnectionFailed(ConnectionResult result){
        Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
        Log.e("GoogleSignIn", "OnConnectionFailed: " + result);
    };

    // With getSignInResultFromIntent() we get the process result
    // With the method handleSignInResult we manage the result of the proccess
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESPUESTA) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    // Perform serveral actions according to the login status
    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            //Login OK -> Show info
            GoogleSignInAccount gsa = result.getSignInAccount(); //getSignInAccount offer the user active account
            tvNombre.setText(gsa.getDisplayName()); //gsa.getBBB to get the account information
            tvEmail.setText(gsa.getEmail());
            updateUI(true);
        } else {
            //Login FAILED -> Show disconnected
            updateUI(false);
        }
    }

    // This method modifies the user interface in the login or fail situation
    private void updateUI(boolean logued){
        if (logued){
            loginButton.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
            desconectar.setVisibility(View.VISIBLE);
        } else {
            tvNombre.setText("Desconectado");
            tvEmail.setText("Desconectado");

            loginButton.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
            desconectar.setVisibility(View.GONE);
        }
    }

}
