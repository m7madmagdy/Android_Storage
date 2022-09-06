package com.example.android_storage.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android_storage.R
import com.example.android_storage.data.Person
import com.example.android_storage.databinding.RecyclerItemsBinding

class RecyclerViewAdapter(private val person: List<Person>) :
    RecyclerView.Adapter<RecyclerViewAdapter.PersonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        return PersonViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.recycler_items,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(person[position])
    }

    override fun getItemCount(): Int {
        return person.size
    }

    inner class PersonViewHolder(private val binding: RecyclerItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(person: Person) {
            binding.person = person
        }
    }
}