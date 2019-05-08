package com.jpp.mpaccount.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jpp.mpaccount.R
import kotlinx.android.synthetic.main.fragment_user_account.*

class UserAccountFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //TODO JPP this needs to be done based on the inner app state
        loginButton.setOnClickListener {
            findNavController().navigate(R.id.toLoginFragment)
        }
    }
}