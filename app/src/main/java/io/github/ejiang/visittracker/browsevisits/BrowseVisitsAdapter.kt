package io.github.ejiang.visittracker.browsevisits

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import io.github.ejiang.visittracker.R
import io.github.ejiang.visittracker.addvisit.AddVisitActivity
import io.github.ejiang.visittracker.db.TypeConv
import io.github.ejiang.visittracker.db.VisitDB
import io.github.ejiang.visittracker.interfaces.DeleteVidListener
import io.github.ejiang.visittracker.util.Utils
import kotlinx.android.synthetic.main.card_visit.view.*
import java.util.Calendar

class BrowseVisitsAdapter(val context: Context,
                          var lis: List<VisitDB>,
                          private val deleteListener: DeleteVidListener,
                          private val readRestaurantListener: ReadRestaurantListener,
                          val showName: Boolean)
                          : RecyclerView.Adapter<BrowseVisitsAdapter.ViewHolder>() {

    private val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    override fun getItemCount(): Int {
        return lis.size
    }

    override fun onBindViewHolder(vh: ViewHolder, pos: Int) {
        val visit = lis[pos]
        vh.tvNote.text = visit.note
        vh.tvSpent.text = Utils.currencyFormat(visit.spending)

        val cal = Calendar.getInstance()
        cal.time = visit.timestamp
        val day = cal.get(Calendar.DAY_OF_MONTH).toString()
        val month = monthNames[cal.get(Calendar.MONTH)]
        val year = cal.get(Calendar.YEAR).toString()
        vh.tvDay.text = day
        vh.tvMonth.text = month
        vh.tvYear.text = year

        val rest = readRestaurantListener.readRestaurant(visit.rid)

        // show name, or hide
        if (showName) {
            vh.tvRestName.text = readRestaurantListener.readRestaurant(visit.rid).name
        } else {
            vh.tvRestName.visibility = View.GONE
            vh.bottomdivider.visibility = View.GONE
        }


        vh.trashVisit.setOnClickListener {
            deleteListener.delete(visit.vid.toString(), visit.rid)
        }

        vh.editVisit.setOnClickListener {
            val i = Intent(context, AddVisitActivity::class.java)
            i.apply {
                putExtra("actionType", "edit")
                putExtra("name", rest.name)
                putExtra("rid", visit.rid)
                putExtra("address", rest.address)
                putExtra("category", rest.category)
                putExtra("lat", rest.lat)
                putExtra("lng", rest.lng)

                // specific items
                putExtra("vid", visit.vid.toString())
                putExtra("note", visit.note)
                putExtra("spending", visit.spending)
                putExtra("timestamp", TypeConv.fromDate(visit.timestamp))
            }

            startActivity(context, i, null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_visit, parent, false)
        return ViewHolder(v)
    }

    fun setList(lis: List<VisitDB>) {
        if (this.lis.size - lis.size == 1) {
            var idx = 0
            // find first difference
            for (i in 0 until this.lis.size) {
                if (i == this.lis.size-1) {
                    idx = i
                } else {
                    if (lis[i].vid != this.lis[i].vid) {
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

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvNote  : TextView = v.tvNote
        val tvSpent : TextView = v.tvSpent
        val tvDay   : TextView = v.tvDay
        val tvMonth : TextView = v.tvMonth
        val tvYear  : TextView = v.tvYear

        val trashVisit : ImageView = v.trashVisit
        val editVisit : ImageView = v.editVisit

        val tvRestName : TextView = v.tvRestName
        val bottomdivider : View = v.bottomDivider
    }
}