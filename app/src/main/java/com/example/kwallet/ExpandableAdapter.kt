package com.example.kwallet

import ChildItem
import ParentItem
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpandableAdapter(private val parents: List<ParentItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_PARENT = 0
    private val VIEW_TYPE_CHILD = 1

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
        val checkBox: CheckBox = itemView.findViewById(R.id.cb_agree)
        val expandIcon: ImageView = itemView.findViewById(R.id.iv_expand)
    }

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
        val checkBox: CheckBox = itemView.findViewById(R.id.cb_agree)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PARENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_parent, parent, false)
                ParentViewHolder(view)
            }
            VIEW_TYPE_CHILD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_child, parent, false)
                ChildViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ParentItem -> bindParentViewHolder(holder as ParentViewHolder, item)
            is ChildItem -> bindChildViewHolder(holder as ChildViewHolder, item)
        }
    }

    private fun bindParentViewHolder(holder: ParentViewHolder, parentItem: ParentItem) {
        holder.titleTextView.text = parentItem.title
        holder.checkBox.isChecked = parentItem.isChecked
        updateExpandIcon(holder.expandIcon, parentItem, animate = false)

        holder.checkBox.setOnClickListener {
            val oldCheckedState = parentItem.isChecked
            parentItem.isChecked = holder.checkBox.isChecked
            if (oldCheckedState != parentItem.isChecked) {
                updateChildrenCheckState(parentItem)
                notifyItemChanged(parents.indexOf(parentItem))
            }
        }

        holder.expandIcon.setOnClickListener {
            toggleExpand(parentItem, holder.expandIcon)
        }
    }

    private fun bindChildViewHolder(holder: ChildViewHolder, childItem: ChildItem) {
        holder.titleTextView.text = childItem.title
        holder.checkBox.isChecked = childItem.isChecked

        holder.checkBox.setOnClickListener {
            val oldCheckedState = childItem.isChecked
            childItem.isChecked = holder.checkBox.isChecked
            if (oldCheckedState != childItem.isChecked) {
                updateParentCheckbox(childItem)
            }
        }
    }

    private fun updateExpandIcon(imageView: ImageView, parentItem: ParentItem, animate: Boolean = true) {
        if (parentItem.children.isEmpty()) {
            imageView.visibility = View.INVISIBLE
            return
        }

        imageView.visibility = View.VISIBLE
        val targetRotation = if (parentItem.isExpanded) 180f else 0f

        if (animate) {
            val valueAnimator = ValueAnimator.ofFloat(imageView.rotation, targetRotation)
            valueAnimator.addUpdateListener { animation ->
                imageView.rotation = animation.animatedValue as Float
            }
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.duration = 300
            valueAnimator.start()
        } else {
            imageView.rotation = targetRotation
        }

        imageView.setImageResource(R.drawable.ic_expand_more)
    }

    private fun toggleExpand(parentItem: ParentItem, expandIcon: ImageView) {
        val parentIndex = parents.indexOf(parentItem)
        val startPosition = getGlobalPosition(parentIndex)

        // 먼저 아이콘 애니메이션 시작
        updateExpandIcon(expandIcon, parentItem, animate = true)

        // 애니메이션이 끝나면 확장/축소 상태 변경 및 아이템 갱신
        expandIcon.animate().setDuration(300).setInterpolator(DecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    parentItem.isExpanded = !parentItem.isExpanded
                    if (parentItem.isExpanded) {
                        notifyItemRangeInserted(startPosition + 1, parentItem.children.size)
                    } else {
                        notifyItemRangeRemoved(startPosition + 1, parentItem.children.size)
                    }
                }
            })
            .start()
    }

    private fun updateChildrenCheckState(parentItem: ParentItem) {
        val parentIndex = parents.indexOf(parentItem)
        val startPosition = getGlobalPosition(parentIndex)
        parentItem.children.forEachIndexed { index, child ->
            if (child.isChecked != parentItem.isChecked) {
                child.isChecked = parentItem.isChecked
                if (parentItem.isExpanded) {
                    notifyItemChanged(startPosition + 1 + index)
                }
            }
        }
    }

    private fun updateParentCheckbox(childItem: ChildItem) {
        val parentItem = parents.find { it.children.contains(childItem) } ?: return
        val oldCheckedState = parentItem.isChecked
        parentItem.isChecked = parentItem.children.all { it.isChecked }
        if (oldCheckedState != parentItem.isChecked) {
            notifyItemChanged(parents.indexOf(parentItem))
        }
    }

    private fun getGlobalPosition(parentIndex: Int): Int {
        var position = parentIndex
        for (i in 0 until parentIndex) {
            if (parents[i].isExpanded) {
                position += parents[i].children.size
            }
        }
        return position
    }

    private fun getItem(position: Int): Any {
        var currentPosition = 0
        parents.forEach { parent ->
            if (currentPosition == position) return parent
            currentPosition++
            if (parent.isExpanded) {
                parent.children.forEach { child ->
                    if (currentPosition == position) return child
                    currentPosition++
                }
            }
        }
        throw IllegalArgumentException("Invalid position")
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ParentItem -> VIEW_TYPE_PARENT
            is ChildItem -> VIEW_TYPE_CHILD
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun getItemCount(): Int {
        return parents.sumOf { 1 + if (it.isExpanded) it.children.size else 0 }
    }
}