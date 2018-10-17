package io.github.ejiang.visittracker.addvisit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.lifecycle.Observer
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker

import io.github.ejiang.visittracker.databinding.FragmentAddVisitBinding
import io.github.ejiang.visittracker.R
import io.github.ejiang.visittracker.db.*
import io.github.ejiang.visittracker.networking.*
import io.github.ejiang.visittracker.search.GlideApp
import io.github.ejiang.visittracker.search.GlideRequest
import io.github.ejiang.visittracker.util.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_add_visit.*
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddVisitFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddVisitFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AddVisitFragment : androidx.fragment.app.Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: FragmentAddVisitBinding
    lateinit var vm: AddViewModel
    private lateinit var req: GlideRequest<Drawable>

    private lateinit var dispose : Disposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_add_visit, container, false)

        binding = FragmentAddVisitBinding.bind(v)

        vm = AddVisitActivity.obtainViewModel(activity as AddVisitActivity)
        binding.vm = vm

        return v
    }

    // this runs after onCreateView
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val actionType = arguments!!.getString("actionType")
        val name = arguments!!.getString("name")
        val rid = arguments!!.getString("rid")
        val address = arguments!!.getString("address")
        val category = arguments!!.getString("category")
        val lat = arguments!!.getDouble("lat")
        val lng = arguments!!.getDouble("lng")

        // set reading fields
        vm.rid = rid
        vm.name.set(name)
        vm.address.set(address)
        vm.category.set(category)
        vm.lat = lat
        vm.lng = lng

        // edit specific
        var addEdit : AddEdit = AddEdit.ADD
        if (actionType == "edit") {
            addEdit = AddEdit.EDIT
            val note = arguments!!.getString("note")
            val spending = arguments!!.getInt("spending")
            val timestamp = arguments!!.getString("timestamp")
            val vid = arguments!!.getString("vid")

            vm.note.set(note)
            if (spending != 0) vm.amount.set(Utils.currencyFormatNoSymbol(spending))

            val cal = Calendar.getInstance()
            cal.time = TypeConv.toDate(timestamp)
            vm.timestamp.value = cal
            vm.vid = vid!!
        }

        val vdb = RestTrackDB.getInstance(requireContext())
        vm.start(vdb!!, addEdit)

        vm.dateField.observe(this, Observer {
            tvDate.text = it
        })
        vm.timeField.observe(this, Observer {
            tvTime.text = it
        })

        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        val rid = arguments!!.getString("rid")
        req = GlideApp.with(this).asDrawable()
        getImage(rid)
    }

    private fun getImage(rid: String) {
        // get the image preview for this venue

        vm.imgUrl = ""


        /* image
        val sr = SearchRepo.getRepo()
        val o : Observable<RespPhotos> = sr.getPhoto(rid)
        dispose = o.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError { Unit }
                .subscribe { s ->
                    val rp : ResponsePhotos = s.response
                    val p : Photos = rp.photos
                    val pi : PhotoItem
                    val url : String
                    if (p.items.isEmpty()) {
                        url = ""
                    } else {
                        pi = p.items[0]
                        url = pi.getURL()
                    }
                    vm.imgUrl = url
                    req.load(url).into(restImage)
                }*/
    }

    private fun setClickListeners() {
        // occurs after vm starts
        tvTime.setOnClickListener { launchTimePicker() }
        tvDate.setOnClickListener { launchDatePicker() }
    }

    private fun launchDatePicker() {
        val d = vm.timestamp.value
        val year = d?.get(Calendar.YEAR)
        val month = d?.get(Calendar.MONTH)
        val dayOfModel = d?.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(context!!, this@AddVisitFragment, year!!, month!!, dayOfModel!!)
        dpd.show()
    }

    private fun launchTimePicker() {
        val d = vm.timestamp.value
        val hourOfDay = d?.get(Calendar.HOUR_OF_DAY)
        val minute = d?.get(Calendar.MINUTE)
        val tpd = TimePickerDialog(context!!, this@AddVisitFragment, hourOfDay!!, minute!!, false)
        tpd.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        vm.changeDate(year, month, dayOfMonth)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        vm.changeTime(hourOfDay, minute)
    }

    override fun onDestroy() {
        super.onDestroy()
        //dispose.dispose() // no image
    }
}
