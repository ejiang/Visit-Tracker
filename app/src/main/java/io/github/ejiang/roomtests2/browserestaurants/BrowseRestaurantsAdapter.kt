package io.github.ejiang.roomtests2.browserestaurants

import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.request.RequestOptions
import io.github.ejiang.roomtests2.R
import io.github.ejiang.roomtests2.interfaces.DeleteListener
import io.github.ejiang.roomtests2.search.GlideRequest
import kotlinx.android.synthetic.main.card_restaurant.view.*

class BrowseRestaurantsAdapter(private val req: GlideRequest<Drawable>,
                               var lis: RestCardList,
                               private val deleteListener: DeleteListener)
        : RecyclerView.Adapter<MatViewHolder>() {

    override fun onBindViewHolder(vh: MatViewHolder, pos: Int) {
        val rest = lis[pos]
        vh.tv3.text = rest.name
        /*
        if (rest.img_url != "") {
            req.load(rest.img_url).apply(RequestOptions.circleCropTransform()).into(vh.resIcon)
        }*/
        vh.tvAddress.text = rest.address
        vh.tvCategory.text = rest.category
        vh.tvDaysAgo.text = rest.daysAgo
        vh.tvVisitCount.text = rest.visits
        vh.tvSpending.text = rest.spent

        vh.trashRestaurant.setOnClickListener {
            deleteListener.deleteId(rest.rid)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): MatViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_restaurant, parent, false)
        return MatViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lis.size
    }

    fun setList(lis: RestCardList) {
        if (this.lis.size - lis.size == 1) {
            var idx = 0
            // find first difference
            for (i in 0 until this.lis.size) {
                if (i == this.lis.size-1) {
                    idx = i
                } else {
                    if (lis[i].rid != this.lis[i].rid) {
                        idx = i
                    }
                }
            }
            this.lis = lis
            notifyItemRemoved(idx)
        } else {
            this.lis = lis
            notifyDataSetChanged()
        }
    }
}

class MatViewHolder(val v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
    val tv3: TextView = v.textView3
    val bottom: LinearLayout = v.bottomLinear
    val resIcon: ImageView = v.resIcon
    val tvAddress: TextView = v.tvAddress
    val tvCategory: TextView = v.tvCategory
    val tvDaysAgo: TextView = v.tvDaysAgo
    val tvVisitCount: TextView = v.tvVisitCount
    val tvSpending: TextView = v.tvSpending
    val trashRestaurant: ImageView = v.trashRestaurant
}
