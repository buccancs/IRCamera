package com.topdon.module.thermal.ir.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AppUtils
import com.topdon.lib.core.bean.GalleryBean
import com.topdon.lib.core.bean.HouseRepPreviewProjectItemBean
import com.topdon.lib.core.utils.AppUtil
import com.topdon.module.thermal.ir.R

@SuppressLint("NotifyDataSetChanged")
class ReportPreviewFloorAdapter(
    val cxt: Context,
    var dataList: List<HouseRepPreviewProjectItemBean>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemView(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_report_floor_child, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bean = dataList[position]
        val itemHolder = holder as ItemView
        itemHolder.ivProblemState.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        itemHolder.ivRepairState.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        itemHolder.ivReplaceState.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        itemHolder.tvProblem.visibility = if (position == 0) View.VISIBLE else View.INVISIBLE
        itemHolder.tvRepair.visibility = if (position == 0) View.VISIBLE else View.INVISIBLE
        itemHolder.tvReplace.visibility = if (position == 0) View.VISIBLE else View.INVISIBLE
        holder.itemView.findViewById<RelativeLayout>(R.id.rly_parent).setBackgroundColor(
            if (position == 0) Color.parseColor("#393643") else Color.parseColor(
                "#23202E"
            )
        )

        if (position == 0) {
            itemHolder.tvProject.text = cxt.getString(R.string.pdf_project_item)
            itemHolder.tvRemark.text = cxt.getString(R.string.report_remark)
        } else {
            itemHolder.tvProject.text = bean.projectName
            itemHolder.tvRemark.text = bean.remark
            when (bean.state) {
                1 -> {
                    itemHolder.ivProblemState.visibility = View.VISIBLE
                    itemHolder.ivRepairState.visibility = View.INVISIBLE
                    itemHolder.ivReplaceState.visibility = View.INVISIBLE
                }

                2 -> {
                    itemHolder.ivProblemState.visibility = View.INVISIBLE
                    itemHolder.ivRepairState.visibility = View.VISIBLE
                    itemHolder.ivReplaceState.visibility = View.INVISIBLE
                }

                3 -> {
                    itemHolder.ivProblemState.visibility = View.INVISIBLE
                    itemHolder.ivRepairState.visibility = View.INVISIBLE
                    itemHolder.ivReplaceState.visibility = View.VISIBLE
                }

                else -> {
                    itemHolder.ivProblemState.visibility = View.INVISIBLE
                    itemHolder.ivRepairState.visibility = View.INVISIBLE
                    itemHolder.ivReplaceState.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ItemView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProject: TextView = itemView.findViewById(R.id.tv_project)
        val tvProblem: TextView = itemView.findViewById(R.id.tv_problem)
        val ivProblemState: ImageView = itemView.findViewById(R.id.iv_problem)
        val tvRepair: TextView = itemView.findViewById(R.id.tv_repair)
        val ivRepairState: ImageView = itemView.findViewById(R.id.iv_repair)
        val tvReplace: TextView = itemView.findViewById(R.id.tv_replace)
        val ivReplaceState: ImageView = itemView.findViewById(R.id.iv_replace)
        val tvRemark: TextView = itemView.findViewById(R.id.tv_remark)
    }
}