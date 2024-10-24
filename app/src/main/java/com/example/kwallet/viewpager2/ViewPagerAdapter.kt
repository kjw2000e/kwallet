package com.example.testviewpager

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kwallet.R

class ViewPagerAdapter(
    val list: List<Page>
) : RecyclerView.Adapter<ViewPagerAdapter.PageViewHolder>() {

    private var currentPageIndex = 0
    private var previousPageIndex = 0 // 이전에 선택된 페이지 인덱스

    fun updateCurrentPage(index: Int) {
        // 이전 페이지와 현재 페이지가 다를 때만 업데이트
        if (index != currentPageIndex) {
            previousPageIndex = currentPageIndex
            currentPageIndex = index

            // 이전 페이지와 현재 페이지만 갱신
            notifyItemChanged(previousPageIndex)
            notifyItemChanged(currentPageIndex)
        }
    }

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.tv_text)
        val iv = itemView.findViewById<ImageView>(R.id.iv)
        val ivCa = itemView.findViewById<ImageView>(R.id.iv_ca)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_viewpager, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val item = list[position]
        holder.text.text = list[position].text

//        holder.page.text = list[position].page.toString()
        holder.iv.setBackgroundColor(Color.parseColor(list[position].backgroundColor))

        if (item.page == 1) {
            holder.ivCa.setBackgroundResource(R.drawable.ic_1)
        } else if (item.page == 2) {
            holder.ivCa.setBackgroundResource(R.drawable.ic_2)
        } else if (item.page == 3) {
            holder.ivCa.setBackgroundResource(R.drawable.ic_5)
        } else if (item.page == 4) {
            holder.ivCa.setBackgroundResource(R.drawable.ic_4)
        } else {
            holder.ivCa.setBackgroundResource(R.drawable.ic_3)
        }
        holder.ivCa.visibility = if (position == currentPageIndex) View.VISIBLE else View.INVISIBLE
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
