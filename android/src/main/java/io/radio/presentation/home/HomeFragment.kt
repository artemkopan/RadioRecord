package io.radio.presentation.home

import android.os.Bundle
import android.view.View
import io.radio.R
import io.radio.di
import io.radio.shared.common.lazyNonSafety
import io.radio.shared.presentation.fragment.BaseFragment
import io.radio.shared.presentation.view.MotionCommand.*
import io.radio.shared.presentation.view.MotionCommand.Set
import io.radio.shared.presentation.view.MotionLayoutStateManager
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private val layoutStateManager by lazyNonSafety {
        MotionLayoutStateManager(
            viewScope,
            rootLayout
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        di { inject(this@HomeFragment) }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutStateManager.send(
            Set(R.id.start, R.id.end),
            ToState(R.id.animateToEnd),
            Await(R.id.end)
        )

        val pagesAdapter = HomePagesAdapter(this)
        pagerView.adapter = pagesAdapter
    }

}