package ru.telecor.gm.mobile.droid.ui.taskcompleted.rv

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task_completed.view.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.ui.base.rv.GenericRecyclerAdapter
import ru.telecor.gm.mobile.droid.ui.base.rv.ViewHolder

class TaskCompletedAdapter(item: ArrayList<TaskItemPreviewData> = arrayListOf()): GenericRecyclerAdapter<TaskItemPreviewData>(item) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_task_completed)
    }

    override fun bind(item: TaskItemPreviewData, holder: ViewHolder): Unit = with(holder.itemView) {
        if (item.containerAction != null){
            garbageStatus.text = item.containerAction.caption
        }
        if (item.statusType != null){
            when (item.statusType) {
                "Успешно" -> {
                    statusDescription.setTextColor(ContextCompat.getColor(context, R.color.grey_color))
                    mark.background = resources.getDrawable(R.drawable.circle_grin_mark_background_layout, null)
                    containerImage.setColorFilter(ContextCompat.getColor(context, R.color.grey_color))
                }
                "Частично успешно" -> {
                    statusDescription.setTextColor(ContextCompat.getColor(context, R.color.yellow_color))
                    mark.background = resources.getDrawable(R.drawable.circle_yellow_mark_background_layout, null)
                    containerImage.setColorFilter(ContextCompat.getColor(context, R.color.yellow_color))
                }
                "Невывоз" -> {
                    statusDescription.setTextColor(ContextCompat.getColor(context, R.color.black_to_red_color))
                    mark.background = resources.getDrawable(R.drawable.circle_red_mark_background_layout, null)
                    containerImage.setColorFilter(ContextCompat.getColor(context, R.color.black_to_red_color))
                }
            }
            statusDescription.isVisible = item.statusType?.isNotEmpty()!= false
            statusDescription.text = item.statusType
        }
        containerTxt.text = item.containerType.name
        garbageTxt.text = item.garbageType.name
    }
}