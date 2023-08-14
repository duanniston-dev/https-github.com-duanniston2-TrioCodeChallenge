package br.com.duannistontriocodechallenge.game.score.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.duannistontriocodechallenge.core.Resource
import br.com.duannistontriocodechallenge.databinding.GameScoreFragmentBinding
import br.com.duannistontriocodechallenge.game.score.application.viewmodel.GameScoreViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameScoreFragment : Fragment() {

    private var _bind: GameScoreFragmentBinding? = null
    private val bind: GameScoreFragmentBinding get() = _bind!!
    private val viewModel: GameScoreViewModel by viewModel()

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
        setUpObservers()
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

    private fun setUpObservers() {
        viewModel.gameScoreLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {

                }

                Resource.Loading -> {

                }

                is Resource.Success -> {
                    setData(it.data)
                }
            }
        }
    }

    private fun setData(data: GameScoreViewData) {
        bind.tvRobot1.text = data.robot1
        bind.tvRobot2.text = data.robot2
        bind.tvNoOneWin.text = data.noOneWin
    }
}