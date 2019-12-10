package com.jpp.mp.main.header

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.jpp.mp.R
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mp.databinding.FragmentNavHeaderBinding
import kotlinx.android.synthetic.main.fragment_nav_header.*

/**
 * The application's UI shows a drawer navigation to allow the user redirect the app flow to different
 * places. This particular Fragment takes care of rendering the header of the drawer and allows the
 * user to login into the system or to be redirected to the account details.
 */
class NavigationHeaderFragment : MPFragment<NavigationHeaderViewModel>() {

    private lateinit var viewBinding: FragmentNavHeaderBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_nav_header, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navHeaderLoginButton.setOnClickListener {
            withViewModel { onNavigateToLoginSelected() }
        }

        navHeaderAccountDetailsTv.setOnClickListener {
            withViewModel { onNavigateToAccountDetailsSelected() }
        }

        withViewModel {
            viewState.observe(this@NavigationHeaderFragment.viewLifecycleOwner, Observer { viewState -> viewBinding.viewState = viewState })
            onInit()
        }
    }

    override fun withViewModel(action: NavigationHeaderViewModel.() -> Unit) = withViewModel<NavigationHeaderViewModel>(viewModelFactory) { action() }
}
