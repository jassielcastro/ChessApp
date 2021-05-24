package com.ajcm.design.archi

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher

abstract class ScopedViewModel<UI : UiState, AS : ActionState>(uiDispatcher: CoroutineDispatcher) : ViewModel(), Scope by Scope.Impl(uiDispatcher) {

    val mModel = MutableLiveData<UI>()
    abstract val model: LiveData<UI>

    init {
        initScope()
    }

    abstract fun dispatch(actionState: AS)

    @CallSuper
    override fun onCleared() {
        destroyScope()
        super.onCleared()
    }

    fun consume(state: UI) {
        mModel.value = state
    }
}