package com.example.sanatkumarsaha.sendmessage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main_Screen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sp;
    boolean flag2 = true;
    ImageView imageView;
    static final int REQUEST_IMAGE_CAPTURE =1;
    static final int REQUEST_SELECT_FILE =2;
    RelativeLayout custom;
    RelativeLayout floating;
    LinearLayout main;
    DiscreteSeekBar stress, urgency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        floating = (RelativeLayout)findViewById(R.id.floating);
        main = (LinearLayout)findViewById(R.id.main);
        stress = (DiscreteSeekBar)findViewById(R.id.stress);
        urgency = (DiscreteSeekBar)findViewById(R.id.urgency);
        floating.setVisibility(View.INVISIBLE);



        com.rey.material.widget.Spinner spinner = (com.rey.material.widget.Spinner) findViewById(R.id.spin);
        List<String> categories = new ArrayList<String>();
        categories.add("Type1");
        categories.add("Type2");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_list_item_2, categories);
        dataAdapter.setDropDownViewResource(R.layout.custom_list_item_2);

        spinner.setAdapter(dataAdapter);

        sp = getSharedPreferences("Check", Context.MODE_PRIVATE);
        stress.setProgress(sp.getInt("stress",0));
        urgency.setProgress(sp.getInt("urgency",0));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (!sp.getBoolean("LogInStat",false))
            navigationView.inflateMenu(R.menu.activity_main_screen_drawer2);
        else navigationView.inflateMenu(R.menu.activity_main__screen_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.nav_header_main__screen, navigationView);
        //Accessing the nav_header_main layout
        imageView = (ImageView) v.findViewById(R.id.imageView);

        if (!sp.getString("uri","").equals("")){
            try {
               Uri uri  = Uri.parse(sp.getString("uri", ""));
                InputStream is = getContentResolver().openInputStream(uri);
                Bitmap photo = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(getCroppedBitmap(photo));

            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            Bitmap photo = BitmapFactory.decodeResource(getResources(),R.drawable.pic);
            imageView.setImageBitmap(getCroppedBitmap(photo));
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sp.getBoolean("LogInStat",false)) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                }else {
                    Toast.makeText(Main_Screen.this,"Please Sign In first to continue!",Toast.LENGTH_LONG).show();
                }
            }
        });
        custom = (RelativeLayout) findViewById(R.id.custom);

        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sp.getBoolean("LogInStat",false)) {

                    if (flag2) {
                        floating.setVisibility(View.VISIBLE);
                        floating.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                main.setEnabled(false);
                            }
                        });
                    } else {
                        floating.setVisibility(View.VISIBLE);

                        floating.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                main.setEnabled(false);
                            }
                        });
                    }
                }else {
                    Toast.makeText(Main_Screen.this,"Please Sign In first to continue",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (floating.getVisibility()==View.VISIBLE){
                close(floating);
            }
            else
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main__screen, menu);
        moveDown();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_signout) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("LogInStat",false);
            editor.putString("uri", "");
            editor.commit();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                Intent i = new Intent(this,LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

        }
        if (id == R.id.nav_signin) {
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_FILE  && resultCode == RESULT_OK && data!=null){
            Uri uri = data.getData();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("uri", uri.toString());
            editor.commit();
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                Bitmap photo = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(getCroppedBitmap(photo));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public Bitmap getCroppedBitmap(Bitmap bmp) {

        int size;
        if (bmp.getHeight()>bmp.getWidth())
            size = bmp.getWidth();
        else size = bmp.getHeight();

        Bitmap bitmap = Bitmap.createScaledBitmap(bmp, size, size, false);

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //return _bmp;
        return output;
    }

    public void close(View v){
        floating.animate().translationY(floating.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                floating.setVisibility(View.INVISIBLE);
                main.setEnabled(true);
                flag2=false;
            }
        });
    }

   public void moveDown(){
       floating.animate().translationY(floating.getHeight());
   }

    public void submit(View v){

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("stress",stress.getProgress());
        editor.putInt("urgency",urgency.getProgress());
        editor.apply();
        close(floating);

    }

}
