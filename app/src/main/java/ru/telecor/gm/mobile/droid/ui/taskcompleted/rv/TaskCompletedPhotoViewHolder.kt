package ru.telecor.gm.mobile.droid.ui.taskcompleted.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_completed_photo.*
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.TaskItemPhotoModel
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseViewHolder

class TaskCompletedPhotoViewHolder(override val containerView: View) :
    BaseViewHolder<TaskItemPhotoModel>(containerView) {

    override fun bind(entity: TaskItemPhotoModel) {
        entity.entity.forEach {
            Glide.with(containerView.context)
                .load(it.photoPath)
                .centerCrop()
                .into(ivPhoto)
        }
    }

    companion object {
        fun create(parent: ViewGroup) =
            TaskCompletedPhotoViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_completed_photo, parent, false)
            )
    }
}
