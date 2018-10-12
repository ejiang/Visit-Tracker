package io.github.ejiang.roomtests2.browsevisits

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.ejiang.roomtests2.R
import io.github.ejiang.roomtests2.addvisit.AddVisitActivity
import io.github.ejiang.roomtests2.db.VisitDB
import io.github.ejiang.roomtests2.interfaces.DeleteListener
import io.github.ejiang.roomtests2.interfaces.DeleteVidListener
import io.github.ejiang.roomtests2.util.Utils
import kotlinx.android.synthetic.main.card_visit.view.*
import java.util.Calendar

class BrowseVisitsAdapter(var lis: List<VisitDB>,
                          private val deleteListener: DeleteVidListener)
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

        vh.trashVisit.setOnClickListener {
            deleteListener.delete(visit.vid.toString(), visit.rid)
        }

        vh.editVisit.setOnClickListener {
            ;
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
    }
}