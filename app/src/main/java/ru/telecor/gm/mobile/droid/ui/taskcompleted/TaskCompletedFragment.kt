package ru.telecor.gm.mobile.droid.ui.taskcompleted

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_task_completed.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.db.ProcessingPhoto
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.presentation.taskcompleted.TaskCompletedPresenter
import ru.telecor.gm.mobile.droid.presentation.taskcompleted.TaskCompletedView
import ru.telecor.gm.mobile.droid.ui.base.BaseFragment
import ru.telecor.gm.mobile.droid.ui.task.rv.TaskPreviewAdapter
import ru.telecor.gm.mobile.droid.ui.taskcompleted.rv.TaskCompletedPhotoAdapter
import toothpick.Toothpick

class TaskCompletedFragment : BaseFragment(), TaskCompletedView {

    companion object {
        private const val TASK_ID_KEY = "taskid"

        fun newInstance(taskId: Int) = TaskCompletedFragment().apply {
            arguments = Bundle().apply {
                putInt(TASK_ID_KEY, taskId)
            }
        }
    }

    override val layoutRes = R.layout.fragment_task_completed

    @InjectPresenter
    lateinit var presenter: TaskCompletedPresenter

    @ProvidePresenter
    fun providePresenter(): TaskCompletedPresenter {
        return Toothpick.openScope(Scopes.SERVER_SCOPE)
            .getInstance(TaskCompletedPresenter::class.java)
            .apply { taskId = arguments?.getInt(TASK_ID_KEY) ?: -1 }
    }

    private var recyclerAdapter = TaskPreviewAdapter()

    private var beforeRecyclerAdapter = TaskCompletedPhotoAdapter()
    private var afterRecyclerAdapter = TaskCompletedPhotoAdapter()
    private var troubleRecyclerAdapter = TaskCompletedPhotoAdapter()
    private var troubleTaskRecyclerAdapter = TaskCompletedPhotoAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvContainers.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvContainers.adapter = recyclerAdapter

        rvBeforePhotos.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvBeforePhotos.adapter = beforeRecyclerAdapter

        rvAfterPhotos.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvAfterPhotos.adapter = afterRecyclerAdapter

        rvTroublePhotos.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTroublePhotos.adapter = troubleRecyclerAdapter

        rvTroubleTaskPhotos.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTroubleTaskPhotos.adapter = troubleTaskRecyclerAdapter
    }

    override fun setAddress(str: String) {
        tvTaskAddress.text = str
    }

    override fun setStatus(str: String) {
        tvTaskStatus.text = str
    }

    override fun setReason(str: String) {
        tvReasonTitle.visible(str.isNotEmpty())
        tvReason.visible(str.isNotEmpty())
        tvReason.text = str
    }

    override fun setContainerReason(boolean: Boolean) {
        tvReasonContainerTitle.visible(boolean)
    }

    override fun setContainersList(list: List<TaskItemPreviewData>) {
        recyclerAdapter.setList(list)
    }

    override fun showListOfBeforePhoto(list: List<ProcessingPhoto>) {
        beforeRecyclerAdapter.setList(list)
        tvBefore.visible(list.isNotEmpty())
        rvBeforePhotos.visible(list.isNotEmpty())
    }

    override fun showListOfAfterPhoto(list: List<ProcessingPhoto>) {
        afterRecyclerAdapter.setList(list)
        tvAfter.visible(list.isNotEmpty())
        rvAfterPhotos.visible(list.isNotEmpty())
    }

    override fun showListOfTroublePhoto(list: List<ProcessingPhoto>) {
        troubleRecyclerAdapter.setList(list)
        tvTrouble.visible(list.isNotEmpty())
        rvTroublePhotos.visible(list.isNotEmpty())
    }

    override fun showListOfTroubleTaskPhoto(list: List<ProcessingPhoto>) {
        troubleTaskRecyclerAdapter.setList(list)
        tvTroubleTask.visible(list.isNotEmpty())
        rvTroubleTaskPhotos.visible(list.isNotEmpty())
    }

    override fun setLoadingState(value: Boolean) {
        pbLoading.visible(value)
    }

}
