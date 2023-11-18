package com.example.c001apk.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.c001apk.R
import com.example.c001apk.logic.model.UpdateCheckResponse
import com.example.c001apk.ui.activity.AppActivity
import com.example.c001apk.util.DateUtils
import com.example.c001apk.viewmodel.AppViewModel

class UpdateListAdapter(
    private val updateList: List<UpdateCheckResponse.Data>,
    private val viewModel: AppViewModel,
    private val activity: FragmentActivity
) :
    RecyclerView.Adapter<UpdateListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icon: ImageView = itemView.findViewById(R.id.appIcon)
        var appName: TextView = itemView.findViewById(R.id.appName)
        var codeName: TextView = itemView.findViewById(R.id.codeName)
        var size: TextView = itemView.findViewById(R.id.size)
        var updateLog = itemView.findViewById<TextView>(R.id.updateLog)
        val bitType = itemView.findViewById<TextView>(R.id.bitType)
        val btnUpdate = itemView.findViewById<Button>(R.id.btnUpdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_app_update, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = updateList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = updateList[position]
        Glide.with(holder.itemView.context).load(app.logo).into(holder.icon)
        holder.appName.text = app.title
        holder.appName.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT // 刷新宽度
        holder.codeName.text = "${app.apkversionname}(${app.apkversioncode})"
        holder.size.text = DateUtils.fromToday(app.lastupdate) + " " + app.apksize
        holder.updateLog.text = app.changelog
        holder.bitType.text = when (app.pkg_bit_type) {
            1 -> "32位"
            2, 3 -> "64位"
            else -> ""
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AppActivity::class.java)
            intent.putExtra("id", app.packageName)
            holder.itemView.context.startActivity(intent)
        }
        holder.btnUpdate.apply {
            setOnClickListener {
                viewModel.downloadLinkData.observe(activity) { result ->
                    val link = result.getOrNull()
                    if (link != null) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        activity.startActivity(intent)
                    } else {
                        result.exceptionOrNull()?.printStackTrace()
                    }
                }
                viewModel.packageName = app.packageName
                viewModel.versionCode = app.apkversioncode.toString()
                viewModel.getDownloadLink()
            }
        }
    }

}