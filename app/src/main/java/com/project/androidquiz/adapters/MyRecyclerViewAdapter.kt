package com.project.androidquiz.adapters

import android.os.Debug
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.androidquiz.R
import com.project.androidquiz.interfaces.MyActionModeCallback
import com.project.androidquiz.models.Users
import com.project.androidquiz.tools.DebugLog
import java.util.*

abstract class MyRecyclerViewAdapter(val activity:AppCompatActivity,val recyclerView: RecyclerView,
                                     val itemClick: (Any) -> Unit) : PagedListAdapter<Users,MyRecyclerViewAdapter.ViewHolder>(
    DIFF_CALLBACK) {
    protected val layoutInflater = activity.layoutInflater
    protected var actModeCallback: MyActionModeCallback
    protected var selectedKeys = LinkedHashSet<Int>()
    protected var positionOffset = 0

    private var actMode: ActionMode? = null
    private var lastLongPressedItem = -1

    abstract fun getActionMenuId(): Int

    abstract fun prepareActionMode(menu: Menu)

    abstract fun actionItemPressed(id: Int)

    abstract fun getSelectableItemCount(): Int

    abstract fun getIsItemSelectable(position: Int): Boolean

    abstract fun onActionModeCreated()

    abstract fun onActionModeDestroyed()

    protected fun isOneItemSelected() = selectedKeys.size == 1

    init {
        actModeCallback = object : MyActionModeCallback() {
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                actionItemPressed(item.itemId)
                return true
            }
            override fun onCreateActionMode(actionMode: ActionMode, menu: Menu?): Boolean {
                if (getActionMenuId() == 0) {
                    return true
                }
                isSelectable = true
                actMode = actionMode
                activity.menuInflater.inflate(getActionMenuId(), menu)
                onActionModeCreated()
                return true
            }

            override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
                prepareActionMode(menu)
                return true
            }

            override fun onDestroyActionMode(actionMode: ActionMode) {
                isSelectable = false
                (selectedKeys.clone() as HashSet<Int>).forEach {
                    val position = it
                    if (position != -1) {
                        toggleItemSelection(false, position)
                    }
                }
                selectedKeys.clear()
                actMode = null
                lastLongPressedItem = -1
                onActionModeDestroyed()
            }
        }
    }

    protected fun toggleItemSelection(select: Boolean, pos: Int) {
        if (select && !getIsItemSelectable(pos)) {
            return
        }
        if ((select && selectedKeys.contains(pos)) || (!select && !selectedKeys.contains(pos))) {
            return
        }
        val item = getItem(pos)
        if (select) {
            selectedKeys.add(pos)
            item?.isSelect = true
            DebugLog.d("myDebug","add position $pos")
        } else {
            selectedKeys.remove(pos)
            item?.isSelect = false
            DebugLog.d("myDebug","delete position $pos")
        }
        notifyItemChanged(pos + positionOffset)

    }


    fun itemLongClicked(position: Int) {
        //recyclerView.setDragSelectActive(position)
        lastLongPressedItem = if (lastLongPressedItem == -1) {
            toggleItemSelection(true,position)
            position
        }else{
            toggleItemSelection(false,lastLongPressedItem)
            toggleItemSelection(true,position)
            position
        }
    }



    fun finishActMode() {
        actMode?.finish()
    }


    protected fun createViewHolder(layoutType: Int, parent: ViewGroup?): ViewHolder {
        val view = layoutInflater.inflate(layoutType, parent, false)
        return ViewHolder(view)
    }

    protected fun bindViewHolder(holder: ViewHolder) {
        holder.itemView.tag = holder
    }

    open inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(any: Any, allowSingleClick: Boolean, allowLongClick: Boolean, callback: (itemView: View) -> Unit): View {
            return itemView.apply {
                callback(this)
                if (allowSingleClick) {
                    setOnClickListener { viewClicked(any) }
                    setOnLongClickListener { if (allowLongClick) viewLongClicked() else viewClicked(any); true }
                } else {
                    setOnClickListener(null)
                    setOnLongClickListener(null)
                }
            }
        }

        private fun viewClicked(any: Any) {
            if (actModeCallback.isSelectable) {
                val currentPosition = adapterPosition - positionOffset
                val isSelected = selectedKeys.contains(currentPosition)
                toggleItemSelection(!isSelected, currentPosition)
                finishActMode()
            } else {
                itemClick.invoke(any)
            }
            lastLongPressedItem = -1
        }

        private fun viewLongClicked() {
            onActionModeCreated()
            //adapterPosition from viewHolder
            val currentPosition = adapterPosition - positionOffset
            if (!actModeCallback.isSelectable) {
                activity.startSupportActionMode(actModeCallback)
            }
            itemLongClicked(currentPosition)
        }
    }

    companion object{
        val DIFF_CALLBACK = object:DiffUtil.ItemCallback<Users>(){
            override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}
