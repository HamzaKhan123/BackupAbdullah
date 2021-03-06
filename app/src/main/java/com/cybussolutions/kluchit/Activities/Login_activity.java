package com.cybussolutions.kluchit.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cybussolutions.kluchit.Network.Analytics;
import com.cybussolutions.kluchit.Network.EndPoints;
import com.cybussolutions.kluchit.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Login_activity extends AppCompatActivity implements SurfaceHolder.Callback{


    Tracker t;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000 ;
    private String email;
    AutoCompleteTextView userName;
    EditText userPassword;

    private CallbackManager callbackManager;
    private LoginButton loginButton;

    TextInputLayout User,Password;

    Button login;

    String user,pass,username,useremail;

    CheckBox checkBox;

    ProgressDialog ringProgressDialog;

    ScrollView layout_interact;

    int wait_for=1000;

    private Animation move;
    private Animation mr_move;
    private Animation lmove;
    private Animation ml_move;

    public static final String PREFS_NAME = "AOP_PREFS";
    public static final String PREFS_KEY = "AOP_PREFS_String";


    String [] arr;

    private ArrayAdapter<String> adapter ;


    private MediaPlayer mp;
    private SurfaceView mPreview;
    private SurfaceHolder holder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        ((EditText)findViewById(R.id.userpass1)).setTransformationMethod(new PasswordTransformationMethod());



        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String user_session= pref.getString("user_session", null);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(user_session != null)
        {
            Intent intent= new Intent(Login_activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();



        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            final String email = object.getString("email");
                            final String name = object.getString("name");
                            String id= object.getString("id");
                            Toast.makeText(getApplicationContext(),email+" Facebook Login Successful",Toast.LENGTH_LONG).show();
                            ImageRequest ir = new ImageRequest("http://graph.facebook.com/"+id+"/picture?type=large", new Response.Listener<Bitmap>() {

                                @Override
                                public void onResponse(Bitmap response) {
                                    Intent intent = new Intent(Login_activity.this, FBregistration.class);
                                    intent.putExtra("email",email);
                                    intent.putExtra("name",name);
                                    intent.putExtra("image",response);
                                    intent.putExtra("bool","1");
                                    LoginManager.getInstance().logOut();
                                    startActivity(intent);
                                }
                            },0, 0, null,  new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Intent intent = new Intent(Login_activity.this, FBregistration.class);
                                    intent.putExtra("email",email);
                                    intent.putExtra("name",name);
                                    //intent.putExtra("image",R.drawable.person);
                                    intent.putExtra("bool","00");
                                    LoginManager.getInstance().logOut();
                                    startActivity(intent);
                                }
                            });


                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueue.add(ir);


                        } catch (JSONException e) {

                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();

            }
        });



        User = (TextInputLayout) findViewById(R.id.userid);
        Password = (TextInputLayout) findViewById(R.id.userpass);
        login = (Button) findViewById(R.id.login);
        userName = (AutoCompleteTextView) findViewById(R.id.userid1);
        userPassword = (EditText) findViewById(R.id.userpass1);
        checkBox = (CheckBox) findViewById(R.id.checkBox);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( userName.getText().toString().isEmpty() || userPassword.getText().toString().isEmpty())
                {
                    if (userName.getText().toString().isEmpty())
                    userName.setError("Please enter Username");
                    if (userPassword.getText().toString().isEmpty());
                    userPassword.setError("Please enter Password");
                }
                else
                {
                    getdata();
                    Jsonsend();
                   // login.callOnClick();
                }
            }
        });
        t= Analytics.getInstance(this).getDefaultTracker();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );


        start_animations();


        arr=loadArray("arr",this);



        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arr);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.userid1);
        textView.setAdapter(adapter);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        mPreview = (SurfaceView)findViewById(R.id.surface);
        holder = mPreview.getHolder();
        holder.setFixedSize(800, 480);

        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mp = new MediaPlayer();

    }

    public boolean saveArray(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }

    public String[] loadArray(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        String array[] = new String[size];
        for(int i=0;i<size;i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }

    public String[] updateArray(String add,String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("preferencename", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        arr=new String[size+1];
        String array[] = new String[size];
        int i;
        for(i=0;i<size;i++) {
            array[i] = prefs.getString(arrayName + "_" + i, null);
            arr[i]=array[i];
        }
        arr[i]=add;
        saveArray(arr,"arr",this);
        return arr;

    }

    void start_animations()
    {
        move=AnimationUtils.loadAnimation(this, R.anim.move);
        mr_move=AnimationUtils.loadAnimation(this, R.anim.middle_right);
        lmove=AnimationUtils.loadAnimation(this, R.anim.move_left);
        ml_move=AnimationUtils.loadAnimation(this, R.anim.middle_left);


        findViewById(R.id.userid1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Tada).duration(1500).playOn(findViewById(R.id.userid));
            }
        });

        findViewById(R.id.userpass1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Tada).duration(1500).playOn(findViewById(R.id.userpass));
            }
        });


        layout_interact = (ScrollView) findViewById(R.id.sc);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_bottom);
        fadeIn.setDuration(wait_for);
        layout_interact.startAnimation(fadeIn);



        move.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        findViewById(R.id.imageView).startAnimation(mr_move);

                    }
                }, 10/* 1sec delay */);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mr_move.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        findViewById(R.id.imageView).startAnimation(lmove);
                    }
                }, 10/* 1sec delay */);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        lmove.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        findViewById(R.id.imageView).startAnimation(ml_move);
                    }
                }, 10/* 1sec delay */);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
       /* ml_move.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        findViewById(R.id.imageView).startAnimation(move);
                    }
                }, 10);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });*/


        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                findViewById(R.id.imageView).startAnimation(move);
            }
        }, wait_for/* 1sec delay */);

    }



    @Override
    protected void onStart()
    {
        super.onStart();

        t.send(new HitBuilders.ScreenViewBuilder().build());

        t.send(new HitBuilders.ScreenViewBuilder().setNewSession().build());
    }


    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    void getdata()
    {

        user = userName.getText().toString();
        pass = userPassword.getText().toString();
        boolean flag=false;
        for (int i=0;i<arr.length;i++)
        {
            if (arr[i].equals(user)) {
                flag = true;
                break;
            }
        }
        if (flag==false) {
            arr = updateArray(user, "arr", this);
            adapter.add((String) arr[arr.length - 1]);
            adapter.notifyDataSetChanged();
        }

    }


    public void Jsonsend()
    {
        ringProgressDialog = ProgressDialog.show(this, "Please wait ...",	"Checking Credentials ...", true);
        ringProgressDialog.setCancelable(true);
        ringProgressDialog.show();


        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.LOGIN,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        ringProgressDialog.dismiss();
                        if(response.equals(""))
                        {
                            Toast.makeText(Login_activity.this, "Incorrect user name or password ",Toast.LENGTH_SHORT).show();
                            YoYo.with(Techniques.Tada).duration(1500).playOn(findViewById(R.id.userid));
                            YoYo.with(Techniques.Tada).duration(1500).playOn(findViewById(R.id.userpass));
                        }

                        else
                        {


                            String userid;

                            try {

                                JSONObject object = new JSONObject(response);


                                userid = object.getString("id");
                                username = object.getString("first_name");
                                useremail = object.getString("email");


                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("user_id", userid);// Saving string

                                editor.putString("user_name",username);
                                editor.putString("user_email",useremail);

                                if(checkBox.isChecked())

                                {
                                    editor.putString("user_session", "logeed_in");
                                }

                                editor.commit();



                                Intent intent= new Intent(Login_activity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                ringProgressDialog.dismiss();
                Toast.makeText(getApplication(),error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {

                Map<String,String> params = new HashMap<>();
                params.put("username",user);
                params.put("password",pass);
                return params;

            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);


    }


    protected void onResume()
    {
        super.onResume();
        start_animations();
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }


    protected void onPause(){
        super.onPause();
       // mp.release();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mp.setDisplay(holder);
        play();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    void play(){

        try {
            AssetFileDescriptor afd;
            afd = getResources().openRawResourceFd(R.raw.bkt);
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
 //           mp.setVideoScalingMode(1);
            mp.setLooping(true);
            mp.setVolume(0,0);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        mp.prepareAsync();
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Do something. For example: playButton.setEnabled(true);
                mp.start();

            }
        });

    }


}


