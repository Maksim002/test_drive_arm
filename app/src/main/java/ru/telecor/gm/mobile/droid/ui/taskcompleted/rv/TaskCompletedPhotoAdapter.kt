package ru.telecor.gm.mobile.droid.ui.taskcompleted.rv

import android.view.ViewGroup
import ru.telecor.gm.mobile.droid.entities.TaskItemPhotoModel
import ru.telecor.gm.mobile.droid.ui.base.rv.BaseAdapter

class TaskCompletedPhotoAdapter : BaseAdapter<TaskCompletedPhotoViewHolder, TaskItemPhotoModel>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskCompletedPhotoViewHolder =
        TaskCompletedPhotoViewHolder.create(parent)
}
