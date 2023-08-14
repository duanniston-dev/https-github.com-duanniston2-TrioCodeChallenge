package br.com.duannistontriocodechallenge.game.score.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.duannistontriocodechallenge.databinding.GameScoreFragmentBinding

class GameScoreFragment : Fragment() {

    private var _bind: GameScoreFragmentBinding? = null
    private val bind: GameScoreFragmentBinding get() = _bind!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = GameScoreFragmentBinding.inflate(inflater, container, false)
        return bind.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    private fun setUpView() {
        bind.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}