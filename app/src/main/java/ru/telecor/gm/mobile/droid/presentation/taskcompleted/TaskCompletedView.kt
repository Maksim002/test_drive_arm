package ru.telecor.gm.mobile.droid.presentation.taskcompleted


import ru.telecor.gm.mobile.droid.entities.TaskItemPhotoModel
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.presentation.base.BaseView

interface TaskCompletedView : BaseView {

    fun setAddress(str: String)

    fun setContainersList(list: List<TaskItemPreviewData>)

    fun showListOfBeforePhoto(list: List<TaskItemPhotoModel>)

    fun showListOfAfterPhoto(list: List<TaskItemPhotoModel>)

    fun showListOfTroublePhoto(list: List<TaskItemPhotoModel>)

    fun showListOfTroubleTaskPhoto(list: List<TaskItemPhotoModel>)

    fun setLoadingState(value: Boolean)
}
