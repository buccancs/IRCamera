//package com.infisense.usbir.adapter
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.TextureView
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.RelativeLayout
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.infisense.usbir.R
//import com.infisense.usbir.bean.PseudocolorBean
//
//class PseudocolorAdapter : RecyclerView.Adapter<PseudocolorAdapter.ViewHolder>() {
//
//
//    private var context: Context? = null
//    private var mDataList: ArrayList<PseudocolorBean>? = null
//    private var listenter: OnItemOnclickListenter? = null
//
//    interface OnItemOnclickListenter {
//        fun onClick(position: Int)
//    }
//
//    /**
//     * @param context
//     * @param mMyLiveList
//     * @param listenter
//     */
//    fun PseudocolorAdapter(
//        context: Context?,
//        mMyLiveList: ArrayList<PseudocolorBean>?,
//        listenter: OnItemOnclickListenter?
//    ) {
//        this.context = context
//        mDataList = mMyLiveList
//        this.listenter = listenter
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view: View =
//            LayoutInflater.from(context).inflate(R.layout.item_pseudocolor_filter, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
//        val filterBean = mDataList!![position]
//        holder.tvName!!.text = filterBean.titleName
//        holder.Background_iv!!.setImageResource(filterBean.img)
//        holder.rlRoot!!.setOnClickListener { listenter!!.onClick(position) }
//    }
//
//    override fun getItemCount(): Int {
//        return mDataList!!.size
//    }
//
//    /**
//     *
//     */
//    class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
//
//        var textureView: TextureView = view.textureView
//        var tvName: TextView = view.tv_Name
//        var filter_img: ImageView = view.filter_img
//        var Background_iv: ImageView = view.Background_iv
//        var rlRoot: View = view.rl_root
//
//
//    }
//}