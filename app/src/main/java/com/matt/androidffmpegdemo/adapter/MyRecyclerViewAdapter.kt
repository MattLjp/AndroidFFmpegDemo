package com.matt.androidffmpegdemo.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matt.androidffmpegdemo.R

class MyRecyclerViewAdapter(private val mContext: Context, private val mTitles: List<String>) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder?>(), View.OnClickListener {
    var selectIndex = 0
    private var mOnItemClickListener: OnItemClickListener? = null
    fun safeNotifyItemChanged(index: Int) {
        if (index > 0) notifyItemChanged(index)
    }

    fun addOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.sample_item_layout, parent, false)
        val myViewHolder = MyViewHolder(view)
        view.setOnClickListener(this)
        return myViewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.mTitle.setText(mTitles[position])
        if (position == selectIndex) {
            holder.mRadioButton.setChecked(true)
            holder.mTitle.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        } else {
            holder.mRadioButton.setChecked(false)
            holder.mTitle.setText(mTitles[position])
            holder.mTitle.setTextColor(Color.GRAY)
        }
        holder.itemView.setTag(position)
    }

    override fun getItemCount(): Int {
        return mTitles.size
    }

    override fun onClick(v: View) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener!!.onItemClick(v, v.getTag() as Int)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mRadioButton: RadioButton
        var mTitle: TextView

        init {
            mRadioButton = itemView.findViewById<RadioButton>(R.id.radio_btn)
            mTitle = itemView.findViewById<TextView>(R.id.item_title)
        }
    }
}