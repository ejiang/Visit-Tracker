package io.github.ejiang.roomtests2.search

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.github.ejiang.roomtests2.R
import io.github.ejiang.roomtests2.addvisit.AddEdit
import io.github.ejiang.roomtests2.addvisit.AddVisitActivity
import io.github.ejiang.roomtests2.networking.*
import kotlinx.android.synthetic.main.rv_search.view.*

class SearchAdapter
(val context: Context, var lis: List<Restaurant>, var req: GlideRequest<Drawable>)
    : androidx.recyclerview.widget.RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    var requested = 10

    override fun getItemCount(): Int {
        return lis.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.rv_search, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rest = lis[position]
        val resName = (position+1).toString() + ". " + rest.name
        val resDesc = if (rest.categories.isEmpty()) "Generic" else rest.categories[0].name
        val resAddr = rest.location.getAddress()

        holder.resName.text = resName
        holder.resDesc.text = resDesc
        holder.resAddr.text = resAddr

        req.load(rest.getURL()).into(holder.restIcon)
        // then do the animation
        setAnimation(holder.itemView, position)

        holder.itemView.setOnClickListener {
            val i = Intent(context, AddVisitActivity::class.java)

            i.apply {
                putExtra("actionType", "add")
                putExtra("name", rest.name)
                putExtra("rid", rest.id)
                putExtra("address", rest.location.getAddress())
                putExtra("category", resDesc)
                putExtra("lat", rest.location.lat)
                putExtra("lng", rest.location.lng)
            }

            startActivity(context, i, null)
        }
    }

    fun setData(lis: List<Restaurant>) {
        this.lis = lis
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                onAttach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        super.onAttachedToRecyclerView(recyclerView)
    }

    private var onAttach = true
    private var lastPosition = -1

    private fun setAnimation(v: View, position: Int) {
        if (position > lastPosition) {
            SearchItemAnimation.animateLeftRight(v, if (onAttach) position else -1)
            lastPosition = position
        }
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val resName : TextView = view.resName
        val restIcon : ImageView = view.restIcon
        val resDesc : TextView = view.resDesc
        val resAddr : TextView = view.resAddress
    }
}
