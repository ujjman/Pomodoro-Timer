package com.ujjman.timer.pomodorotimer

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class OverlayViewHolder(val params: WindowManager.LayoutParams, context: Context) {
    val view = ComposeView(context)

    init {
        params.gravity = Gravity.TOP or Gravity.LEFT
        view.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        composeViewConfigure(view)
    }


}

fun composeViewConfigure(composeView: ComposeView) {
    val lifecycleOwner = MyLifecycleOwner()
    lifecycleOwner.performRestore(null)
    lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    ViewTreeLifecycleOwner.set(composeView, lifecycleOwner)
    composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

    val viewModelStore = ViewModelStore()
    ViewTreeViewModelStoreOwner.set(composeView) { viewModelStore }

    val coroutineContext = AndroidUiDispatcher.CurrentThread
    val runRecomposeScope = CoroutineScope(coroutineContext)
    val recomposer = Recomposer(coroutineContext)
    composeView.compositionContext = recomposer
    runRecomposeScope.launch {
        recomposer.runRecomposeAndApplyChanges()
    }
}

internal class MyLifecycleOwner : SavedStateRegistryOwner {
    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private var mSavedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.create(this)

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry


    fun handleLifecycleEvent(event: Lifecycle.Event) {
        mLifecycleRegistry.handleLifecycleEvent(event)
    }


    fun performRestore(savedState: Bundle?) {
        mSavedStateRegistryController.performRestore(savedState)
    }


}