package com.lotus.ourhome.ui.bill

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lotus.ourhome.R
import kotlinx.android.synthetic.main.list_cell_bill.view.*

class BillRecyclerAdapter(context: Context?) : RecyclerView.Adapter<BillRecyclerAdapter.ViewHolder>() {

    private var mContext: Context? = null
    var mData: MutableList<String>? = null

    init {
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_cell_bill, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.text_view.text = mData?.get(position)?:""
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}