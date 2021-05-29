package com.ajcm.design.archi

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ajcm.design.R

abstract class BaseFragment<UI: UiState, AS : ActionState, VM: ScopedViewModel<UI, AS>>(@LayoutRes layout: Int): Fragment(layout) {

    abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.model.observe(this, Observer(::render))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            view.background = ContextCompat.getDrawable(it, R.color.colorPrimary)
        }
    }

    abstract fun render(state: UI)

}
