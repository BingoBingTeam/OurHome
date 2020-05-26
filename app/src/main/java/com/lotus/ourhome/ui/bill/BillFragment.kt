package com.lotus.ourhome.ui.bill

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lotus.ourhome.R
import kotlinx.android.synthetic.main.fragment_home.*

class BillFragment : Fragment() {

    lateinit var mAdapter:BillRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    fun initView(){
        recycler_view.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        mAdapter = BillRecyclerAdapter(context)
        recycler_view.adapter = mAdapter
        var dataMutableList = mutableListOf("11111111","2222222","3333333333","4444444444")
        for(i in 1..20){
            dataMutableList.add(i.toString())
        }
        mAdapter.mData = dataMutableList
        mAdapter.notifyDataSetChanged()
    }
}