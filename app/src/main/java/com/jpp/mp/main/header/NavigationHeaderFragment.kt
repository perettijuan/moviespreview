package com.jpp.mp.main.header

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jpp.mp.R
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mp.databinding.FragmentNavHeaderBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * The application's UI shows a drawer navigation to allow the user redirect the app flow to different
 * places. This particular Fragment takes care of rendering the header of the drawer and allows the
 * user to login into the system or to be redirected to the account details.
 */
@ExperimentalCoroutinesApi
class NavigationHeaderFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: NavigationHeaderViewModelFactory

    private var viewBinding: FragmentNavHeaderBinding? = null

    private val viewModel: NavigationHeaderViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            viewModelFactory,
            this
        )
    }

    private var navHeaderLoginButton: Button? = null
    private var navHeaderAccountDetailsTv: TextView? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_nav_header, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.onInit()
    }

    override fun onDestroyView() {
        viewBinding = null
        navHeaderLoginButton = null
        navHeaderAccountDetailsTv = null
        super.onDestroyView()
    }

    private fun setUpViews(view: View) {
        navHeaderLoginButton = view.findViewById(R.id.navHeaderLoginButton)
        navHeaderAccountDetailsTv = view.findViewById(R.id.navHeaderAccountDetailsTv)

        navHeaderLoginButton?.setOnClickListener {
            viewModel.onNavigateToLoginSelected()
        }

        navHeaderAccountDetailsTv?.setOnClickListener {
            viewModel.onNavigateToAccountDetailsSelected()
        }
    }

    private fun renderViewState(viewState: HeaderViewState) {
        viewBinding?.viewState = viewState
    }
}
