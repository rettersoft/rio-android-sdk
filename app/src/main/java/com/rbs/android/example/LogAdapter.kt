package com.rbs.android.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by semihozkoroglu on 7.08.2021.
 */
class LogAdapter constructor(val items: ArrayList<String>) :
    RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.tvLog.text = "${(position + 1)}- ${items[position]}"
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class LogViewHolder constructor(item: View) : RecyclerView.ViewHolder(item) {
        val tvLog = item.findViewById<AppCompatTextView>(R.id.tvLog)
    }
}