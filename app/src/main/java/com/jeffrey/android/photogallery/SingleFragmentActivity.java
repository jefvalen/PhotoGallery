package com.jeffrey.android.photogallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

        @Override
        protected void onCreate(Bundle savedInstanceState) {  //BIJ HET CREEEREN WORDT DE SAVEDINSTANCE AANGEROEPEN, STANDAARD
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_fragment);

            FragmentManager fm = getSupportFragmentManager();                   //1e stap om FM te halen (pagina 142)
            Fragment fragment = fm.findFragmentById(R.id.fragment_container); // Wanneer ragment moet worden opgehaald doe je dat hier met de ID

            if (fragment == null) {
                fragment = createFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)     //Deze code creert en commits een fragment transaction (Id verwijst naar Fragment
                        .commit();                                 // pagina 143, er staat creeer nieuw fragment incl. een toevoeg operatie (add)
            }
        }


}



