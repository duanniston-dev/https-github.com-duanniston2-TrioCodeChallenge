package br.com.duannistontriocodechallenge.game.board.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import br.com.duannistontriocodechallenge.databinding.GameBoardFragmentBinding
import br.com.duannistontriocodechallenge.game.board.application.adapter.GameBoardAdapter
import br.com.duannistontriocodechallenge.game.board.data.GameBoardAdapterItemData

class GameBoardFragment : Fragment() {

    private var _bind: GameBoardFragmentBinding? = null
    private val bind: GameBoardFragmentBinding get() = _bind!!
    private val gameBoardAdapter by lazy {
        GameBoardAdapter()
    }

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
        setUpData()
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

    private fun setUpData() {
        val sampleList = listOf(
            GameBoardAdapterItemData(GameBoardAdapterItemData.GameBoardAdapterType.EMPTY),
            GameBoardAdapterItemData(GameBoardAdapterItemData.GameBoardAdapterType.PRIZE),
            GameBoardAdapterItemData(GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_CURRENT),
            GameBoardAdapterItemData(GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_CURRENT),
            GameBoardAdapterItemData(GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_LINE),
            GameBoardAdapterItemData(GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_LINE),
            GameBoardAdapterItemData(GameBoardAdapterItemData.GameBoardAdapterType.EMPTY),
            GameBoardAdapterItemData(GameBoardAdapterItemData.GameBoardAdapterType.PRIZE),
            GameBoardAdapterItemData(GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_1_LINE),
            GameBoardAdapterItemData(GameBoardAdapterItemData.GameBoardAdapterType.ROBOT_2_LINE)
        )
        gameBoardAdapter.submitList(sampleList)
    }

}