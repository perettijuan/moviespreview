package com.jpp.mp.testutils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.support.HasSupportFragmentInjector

/**
 * Empty [AppCompatActivity] to perform tests over the Fragments used in the application.
 * Implements [HasSupportFragmentInjector] in order to allow the injection of the fragments
 * by providing a custom [AndroidInjector] when a new Fragment is being initialized.
 */
class FragmentTestActivity : AppCompatActivity(), HasSupportFragmentInjector {

    private lateinit var injector: AndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    fun startFragment(fragment: Fragment, fInjector: AndroidInjector<Fragment>) {
        injector = fInjector
        supportFragmentManager.beginTransaction()
                .add(android.R.id.content, fragment, "TAG")
                .commit()
    }

    inline fun <reified T : Fragment> startFragment(fragment: T, crossinline injector: (T) -> Unit) {
        startFragment(fragment, AndroidInjector { if (it is T) injector(it) })
    }
}