package com.example.android_storage.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_storage.R
import com.example.android_storage.util.Constants.listData
import com.example.android_storage.databinding.ActivityRecyclerBinding
import com.example.android_storage.ui.adapter.RecyclerViewAdapter

class RecyclerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclerBinding
    private lateinit var recyclerAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recycler)
        title = getString(R.string.recycler_view_activity)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerAdapter = RecyclerViewAdapter(listData)
        binding.recyclerView.apply {
            adapter = recyclerAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@RecyclerActivity)
        }
    }
}