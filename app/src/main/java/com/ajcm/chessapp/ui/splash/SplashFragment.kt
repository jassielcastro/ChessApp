package com.ajcm.chessapp.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ajcm.chessapp.R
import com.ajcm.chessapp.databinding.SplashFragmentBinding
import com.ajcm.design.archi.BaseFragment
import com.ajcm.design.extensions.navigateTo
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : BaseFragment<SplashState, SplashAction, SplashViewModel>(R.layout.splash_fragment) {

    override val viewModel: SplashViewModel by viewModel()

    private lateinit var binding: SplashFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View {
        binding = SplashFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dispatch(SplashAction.Init)
    }

    override fun render(state: SplashState) {
        when (state) {
            SplashState.StartAnimation -> startAnim()
            is SplashState.MoveNextScreen -> navigateTo(state.screenId)
        }
    }

    private fun startAnim() {
        binding.splashImage.postDelayed({
            viewModel.dispatch(SplashAction.GetNextScreen)
        }, 500)
    }
}
