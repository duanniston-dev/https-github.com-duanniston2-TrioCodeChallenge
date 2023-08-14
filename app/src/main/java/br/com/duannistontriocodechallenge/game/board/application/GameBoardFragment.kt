package br.com.duannistontriocodechallenge.game.board.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import br.com.duannistontriocodechallenge.core.Resource
import br.com.duannistontriocodechallenge.core.toErrorDialog
import br.com.duannistontriocodechallenge.databinding.GameBoardFragmentBinding
import br.com.duannistontriocodechallenge.game.board.application.adapter.GameBoardAdapter
import br.com.duannistontriocodechallenge.game.board.application.viewmodel.GameBoardViewModel
import br.com.duannistontriocodechallenge.game.board.data.GameBoardAdapterItemData
import br.com.duannistontriocodechallenge.game.board.data.GameBoardScoreData
import br.com.duannistontriocodechallenge.game.board.data.GameBoardStateData
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

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

        bind.tvRobot1.text = "0"
        bind.tvRobot2.text = "0"
        bind.tvNoOneWin.text = "0"
    }

    private fun setUpObservers() {
        gameBoardViewModel.boardLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    showError(it)
                }

                Resource.Loading -> {
                    Snackbar.make(bind.root, "Loading", Snackbar.LENGTH_SHORT).show()
                }

                is Resource.Success -> {
                    setGameBoardData(it)
                }
            }

        }

        gameBoardViewModel.scoreRobotLiveData.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Error -> {
                    showError(it)
                }

                Resource.Loading -> {
                    Snackbar.make(bind.root, "Loading", Snackbar.LENGTH_SHORT).show()
                }

                is Resource.Success -> {
                    setScoreData(it)
                }
            }

        }
        gameBoardViewModel.gameFlowLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    showError(it)
                }

                Resource.Loading -> {
                    Snackbar.make(bind.root, "Loading", Snackbar.LENGTH_SHORT).show()
                }

                is Resource.Success -> {
                    setGameState(it.data)
                }
            }
        }
    }

    private fun showError(it: Resource.Error) {
        Snackbar.make(
            bind.root,
            "Error: ${it.error.toErrorDialog().message}",
            Snackbar.LENGTH_LONG
        ).show()
        Timber.e("Error: ${it.error.toErrorDialog()}")
    }

    private fun setGameState(data: GameBoardStateData) {
        Timber.i("Game state:${data.name}")
        when (data) {
            GameBoardStateData.RUNNING -> {
                Snackbar.make(bind.root, "Game started", Snackbar.LENGTH_SHORT).show()
            }

            GameBoardStateData.GAME_FINISHED -> {
                Snackbar.make(bind.root, "Game finished", Snackbar.LENGTH_SHORT).show()
            }

            else -> {

            }
        }
    }

    private fun setScoreData(it: Resource.Success<GameBoardScoreData>) {
        bind.tvRobot1.text = it.data.robot1.toString()
        bind.tvRobot2.text = it.data.robot2.toString()
        bind.tvNoOneWin.text = it.data.noOneWin.toString()
    }

    private fun setGameBoardData(it: Resource.Success<List<GameBoardAdapterItemData>>) {
        gameBoardAdapter.submitList(it.data)
        gameBoardAdapter.notifyDataSetChanged()
    }

}