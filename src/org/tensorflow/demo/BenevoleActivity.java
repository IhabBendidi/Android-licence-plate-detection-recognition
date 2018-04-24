package org.tensorflow.demo;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import org.tensorflow.demo.R;

import org.tensorflow.demo.fragment.TabAboutFragment;
import org.tensorflow.demo.fragment.TabGiftFragment;
import org.tensorflow.demo.fragment.TabOfferFragment;
import org.tensorflow.demo.util.ImageUtil;
import org.tensorflow.demo.view.PagerSlidingTabStrip;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BenevoleActivity extends AppCompatActivity {

    private MyPagerAdapter adapter; // pour la listes des offres
    private Toolbar toolbar;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private TextView toolbarFavorite;
    private TextView toolbarPoint;
    private TextView toolbarUser;

	String id,email,points,dis,idf,act,email2;
    DatabaseReference dbVol;
    Volontaire users;
    DataBase dbl;
    private FirebaseFirestore db;
    ArrayList usersList;
    int i,j,c,apl;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benevole);

        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }

		dbl=new DataBase(this);
        Bundle extra=this.getIntent().getExtras();
        if(extra!=null)
        {
            email2=extra.getString("email");
            act=extra.getString("act");
            id=extra.getString("id");
            email=extra.getString("email");
            points=extra.getString("points");
            dis=extra.getString("rec");
        }
        db = FirebaseFirestore.getInstance();
        i=0;
        j=0;
        c=0;
		apl=0;
        usersList=new ArrayList<>();
        dbVol= FirebaseDatabase.getInstance().getReference("volontaire");
        dbVol.setPriority(points);
		idf=dbVol.push().getKey();
		/* Thread logoTimer = new Thread() {
            public void run() {
                try {
                    sleep(15000);
                    ref();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        logoTimer.start();*/
		
        users=new Volontaire(idf,email,"","","",points);
        /******** header ********/
        /****add logo to toolbar***/
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setIcon(R.drawable.icon_notification_white);
        /*******************************/
        toolbar = (Toolbar) findViewById(R.id.toolbar_tab_benevole);

        ImageView toolbarImage = (ImageView) toolbar.findViewById(R.id.toolbar_tab_benevole_image);
        toolbarFavorite = (TextView) toolbar.findViewById(R.id.toolbar_tab_benevole_favorite);
        toolbarPoint = (TextView) toolbar.findViewById(R.id.toolbar_tab_benevole_point);
        toolbarPoint.setText(points);
        toolbarUser = (TextView) toolbar.findViewById(R.id.toolbar_tab_benevole_name);
        toolbarUser.setText(email);
        /*toolbarLike = (TextView) toolbar.findViewById(R.id.toolbar_tab_benevole_like);
        toolbarShare = (TextView) toolbar.findViewById(R.id.toolbar_tab_benevole_share);*/
        String imageUri = "drawable://" + R.drawable.default_image;

        ImageUtil.displayRoundImage(toolbarImage,imageUri , null);
        /*toolbarFavorite.setOnClickListener(this);*/
        setSupportActionBar(toolbar);
        /**************************/
        tabs = (PagerSlidingTabStrip) findViewById(R.id.activity_tab_benevole_tabs);
        pager = (ViewPager) findViewById(R.id.activity_tab_benevole_pager);

        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(2);

        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                //Toast.makeText(BenevoleActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });
		
		/*****************************/
		dbVol.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(i==0)
                {
                    i=1;
                    for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        Volontaire us = userSnapshot.getValue(Volontaire.class);
                        if(us.email.equals(email))
                        {
                            j=1;
                            idf=us.id;
                            break;
                        }
                    }
                    if(j==0)
                    {
                        dbVol.child(idf).setValue(users);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.logout:
			    dbl.delet_vo();
				dbVol.child(idf).removeValue();
				Intent intent=new Intent(BenevoleActivity.this,LoginActivity.class);
				startActivity(intent);
                finish();
                Toast.makeText(BenevoleActivity.this, "logout", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final ArrayList<String> tabNames = new ArrayList<String>() {{
            add(getResources().getString(R.string.tab_gift));
            add(getResources().getString(R.string.tab_offer));
            add(getResources().getString(R.string.tab_about));
        }};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames.get(position);
        }

        @Override
        public int getCount() {
            return tabNames.size();
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 2) {
                return TabAboutFragment.newInstance(position);
            }
            else if(position == 1) {
                return TabOfferFragment.newInstance(position);
            }else if(position == 0) {
                return TabGiftFragment.newInstance(position);
            } else{
                return TabAboutFragment.newInstance(position);
            }
        }
    }

	/***********************************************/
	    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        dbVol.child(idf).removeValue();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void onBackPressed()
    { 
                           
    }
    protected void onStart()
    {
        super.onStart();
        dbVol.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Volontaire us = userSnapshot.getValue(Volontaire.class);
                    apl=1;
                    if(us.email.equals(email) && !us.email2.equals("") && us.call.equals("") && us.rec2.equals("") && c==0)
                    {
                        if(dis!=null && dis.equals("dis") && us.email2.equals(email2)) {
                            break;
                        }else
                        {
                            Intent intent = new Intent(BenevoleActivity.this, CallVolActivity.class);
                            intent.putExtra("email2", "" + us.email2);
                            intent.putExtra("email", email);
                            intent.putExtra("id", us.id);
                            intent.putExtra("points", points);
                            dbVol.child(us.id).setValue(new Volontaire(us.id,us.email,us.email2,us.call,"in",us.points));
                            startActivityForResult(intent, 1);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

	
}