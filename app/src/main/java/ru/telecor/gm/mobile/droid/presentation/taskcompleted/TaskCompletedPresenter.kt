package ru.telecor.gm.mobile.droid.presentation.taskcompleted

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.TaskItemPhotoModel
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.entities.db.TaskProcessingResult
import ru.telecor.gm.mobile.droid.entities.db.TaskExtended
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations
import ru.telecor.gm.mobile.droid.model.PhotoType
import ru.telecor.gm.mobile.droid.model.interactors.PhotoInteractor
import ru.telecor.gm.mobile.droid.model.interactors.RouteInteractor
import ru.telecor.gm.mobile.droid.model.system.ResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

class TaskCompletedPresenter @Inject constructor(
    private val routeInteractor: RouteInteractor,
    private val photoInteractor: PhotoInteractor,
    private val rm: ResourceManager
) : BasePresenter<TaskCompletedView>() {

    private lateinit var localTaskCache: TaskExtended
    private var deliveredTask: TaskProcessingResult? = null

    var taskId: Int = -1
    var setListTask: List<TaskRelations> = arrayListOf()

    override fun attachView(view: TaskCompletedView?) {
        super.attachView(view)

        clearUI()
        deliveredTask = routeInteractor.getDeliveredTask(taskId.toLong())

        launch {
            val result = routeInteractor.getTaskById(taskId)

            handleResult(result, {
                localTaskCache = it.data
                updateUI()
            }, {
                handleError(it, rm)
                clearUI()
            })
        }
    }

    private fun clearUI() {
        viewState.setAddress("")
        viewState.setContainersList(emptyList())
        viewState.showListOfAfterPhoto(emptyList())
        viewState.showListOfBeforePhoto(emptyList())
        viewState.showListOfTroublePhoto(emptyList())
        viewState.showListOfTroubleTaskPhoto(emptyList())
    }

    private fun updateUI() {
        setActualInfo()
    }

    private fun setActualInfo() {
        viewState.setAddress(localTaskCache.stand?.address ?: "")
        val ctList = localTaskCache.stand?.containerGroups?.filter { cg ->
            localTaskCache.taskItems.map { ti -> ti.containerTypeId }.toList()
                .contains(cg.containerType.id)
        }?.distinct() ?: emptyList()
        val supportedGarbageTypes = routeInteractor.startedRoute?.unit?.vehicle?.supportedGarbageTypes?.map { it.id }
        val finalList = ctList.map {
            val supportedGarbageType =
                if (supportedGarbageTypes != null) it.garbageType.id in supportedGarbageTypes else true
            TaskItemPreviewData(
                it.containerType,
                it.garbageType,
                localTaskCache.containerAction,
                if (supportedGarbageType) localTaskCache.taskItems.filter { ti -> ti.containerTypeId == it.containerType.id }
                    .map { item -> item.planCount }.sum() else it.count,
                // TODO: нужно тут поставить определитель ilim continue
                supportedGarbageType,
                statusType = getStatus(localTaskCache.statusType),
                failureReason = deliveredTask?.failureReason?.name ?: ""
            )
        }.toMutableList()

        viewState.setContainersList(finalList.sortedByDescending { it.supportedGarbageType.toString() })
        viewState.showListOfAfterPhoto(
            arrayListOf(TaskItemPhotoModel(getPhotoList(PhotoType.LOAD_AFTER), PhotoType.LOAD_AFTER.toString()))
        )
        viewState.showListOfBeforePhoto(
            arrayListOf(TaskItemPhotoModel(getPhotoList(PhotoType.LOAD_BEFORE), PhotoType.LOAD_BEFORE.toString()))
        )
        viewState.showListOfTroublePhoto(
            arrayListOf(TaskItemPhotoModel(getPhotoList(PhotoType.LOAD_TROUBLE), PhotoType.LOAD_TROUBLE.toString())))

        viewState.showListOfTroubleTaskPhoto(
            arrayListOf(TaskItemPhotoModel(getPhotoList(PhotoType.TASK_TROUBLE), PhotoType.TASK_TROUBLE.toString())))
    }

    private fun getPhotoList(photoType: PhotoType): List<ProcessingPhoto> {
        //Это не грех, гуглы сами разрешают так делать
        return runBlocking {
            photoInteractor.getTaskPhotosFlow(
                routeInteractor.startedRoute?.id ?: -1, localTaskCache.id.toLong(), photoType
            ).first()
        }
    }

    private fun getStatus(statusType: StatusType): String {
        return when (statusType) {
            StatusType.SUCCESS -> rm.getString(R.string.task_completed_fragment_success)
            StatusType.PARTIALLY -> rm.getString(R.string.task_completed_fragment_partially)
            StatusType.FAIL -> rm.getString(R.string.task_completed_fragment_fail)
            else -> ""
        }
    }
}
