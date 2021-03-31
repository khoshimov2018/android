package ru.behetem.fragments

import android.content.DialogInterface
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.messenger_fragment.*
import ru.behetem.R
import ru.behetem.activities.AllReactionsActivity
import ru.behetem.adapters.ChatRoomsAdapter
import ru.behetem.adapters.ReceivedReactionAdapter
import ru.behetem.databinding.MessengerFragmentBinding
import ru.behetem.utils.*
import ru.behetem.viewmodels.MessengerViewModel

class MessengerFragment : Fragment() {

    companion object {
        fun newInstance() = MessengerFragment()
    }

    private lateinit var viewModel: MessengerViewModel
    private lateinit var binding: MessengerFragmentBinding

    private var receivedReactionAdapter: ReceivedReactionAdapter? = null
    private var chatRoomsAdapter: ChatRoomsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MessengerViewModel::class.java)
        viewModel.setLoggedInUser(getLoggedInUserFromShared(requireActivity()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.messenger_fragment, container, false)
        val view: View = binding.root
        initViewModel()

        return view
    }

    private fun initViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initObservers()
    }

    private fun initObservers() {
        viewModel.getReceivedReactionsList().observe(viewLifecycleOwner, {
            if(it != null) {
                if(receivedReactionAdapter == null) {
                    receivedReactionAdapter = ReceivedReactionAdapter(it, viewModel)
                    binding.receivedReactionsAdapter = receivedReactionAdapter
                }
                receivedReactionAdapter?.notifyDataSetChanged()
            }
        })

        viewModel.getChatRoomsLiveData().observe(viewLifecycleOwner, {
            if(it != null) {
                if(chatRoomsAdapter == null) {
                    chatRoomsAdapter = ChatRoomsAdapter(it, viewModel)
                    binding.chatRoomsAdapter = chatRoomsAdapter

                    val swipeHandler = object : SwipeToDeleteCallback(requireActivity()) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val adapter = chatRoomsRecyclerView.adapter as ChatRoomsAdapter
                            printLog("on swiped ${viewHolder.adapterPosition}")

                            showAlertDialog(
                                requireActivity(),
                                null,
                                getString(R.string.sure_delete_chat_room),
                                getString(R.string.yes),
                                { dialogInterface, _ ->
                                    dialogInterface.cancel()
                                    viewModel.deleteChatRoom(viewHolder.adapterPosition)
                                },
                                getString(R.string.no),
                                { dialogInterface, _ ->
                                    dialogInterface.cancel()
                                    chatRoomsAdapter?.notifyDataSetChanged()
                                }
                            )
//                            adapter.removeAt(viewHolder.adapterPosition)
                        }
                    }
                    val itemTouchHelper = ItemTouchHelper(swipeHandler)
                    itemTouchHelper.attachToRecyclerView(chatRoomsRecyclerView)
                }
                chatRoomsAdapter?.notifyDataSetChanged()
            }
        })

        viewModel.getAllClicked().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setAllClicked(false)
                openAllReactions()
            }
        })

        viewModel.getShowNoInternet().observe(viewLifecycleOwner, {
            if(it) {
                viewModel.setShowNoInternet(false)
                showInfoAlertDialog(requireActivity(), getString(R.string.no_internet))
                chatRoomsAdapter?.notifyDataSetChanged()
            }
        })

        viewModel.getBaseResponse().observe(viewLifecycleOwner, {
            it?.let {
                viewModel.setBaseResponse(null)
                validateResponse(requireActivity(), it)
                chatRoomsAdapter?.notifyDataSetChanged()
            }
        })
    }

    private fun openAllReactions() {
        val intent = Intent(requireActivity(), AllReactionsActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getReactions()
        viewModel.getChatRooms()
    }

    override fun onPause() {
        super.onPause()
        chatRoomsAdapter = null
        receivedReactionAdapter = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatRoomsAdapter = null
        receivedReactionAdapter = null
    }
}