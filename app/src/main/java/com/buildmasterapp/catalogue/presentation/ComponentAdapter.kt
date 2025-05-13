package com.buildmasterapp.catalogue.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.buildmasterapp.R
import com.buildmasterapp.catalogue.domain.model.Component

class ComponentAdapter(private val components: List<Component>) :
    RecyclerView.Adapter<ComponentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.componentName)
        val type = itemView.findViewById<TextView>(R.id.componentType)
        val price = itemView.findViewById<TextView>(R.id.componentPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.component_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = components.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val component = components[position]
        holder.name.text = component.name
        holder.type.text = component.type
        holder.price.text = "$${component.price}"
    }
}