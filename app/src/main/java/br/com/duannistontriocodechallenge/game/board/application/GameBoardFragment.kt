package br.com.duannistontriocodechallenge.game.board.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import br.com.duannistontriocodechallenge.databinding.GameBoardFragmentBinding
import br.com.duannistontriocodechallenge.game.board.application.adapter.GameBoardAdapter
import br.com.duannistontriocodechallenge.game.board.application.viewmodel.GameBoardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameBoardFragment : Fragment() {

    private var _bind: GameBoardFragmentBinding? = null
    private val bind: GameBoardFragmentBinding get() = _bind!!
    private val gameBoardAdapter by lazy {
        GameBoardAdapter()
    }
    private val gameBoardViewModel by viewModel<GameBoardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = GameBoardFragmentBinding.inflate(inflater, container, false)
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
        //Adapter
        bind.rvBoard.layoutManager = GridLayoutManager(requireContext(), 7)
        bind.rvBoard.adapter = gameBoardAdapter

        //Toolbar
        bind.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setUpObservers() {
        gameBoardViewModel.boardLiveData.observe(viewLifecycleOwner) {
            gameBoardAdapter.submitList(it)
        }
    }

}