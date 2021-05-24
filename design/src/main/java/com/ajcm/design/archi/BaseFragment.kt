package com.ajcm.design.archi

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

abstract class BaseFragment<UI: UiState, AS : ActionState, VM: ScopedViewModel<UI, AS>>(@LayoutRes layout: Int): Fragment(layout) {

    abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.model.observe(this, Observer(::render))
    }

    abstract fun render(state: UI)

}
