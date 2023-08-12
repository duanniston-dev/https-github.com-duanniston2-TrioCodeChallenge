package br.com.duannistontriocodechallenge.game.onboarding.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.duannistontriocodechallenge.databinding.GameOnboardingFragmentBinding

class GameOnboardingFragment : Fragment() {
    private var _bind: GameOnboardingFragmentBinding? = null
    private val bind: GameOnboardingFragmentBinding get() = _bind!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _bind = GameOnboardingFragmentBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

}