package ru.telecor.gm.mobile.droid.ui.taskcompleted

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_task_completed.*
import kotlinx.android.synthetic.main.fragment_task_completed.pbLoading
import kotlinx.android.synthetic.main.fragment_task_completed.troubleRvUpContainers
import kotlinx.android.synthetic.main.fragment_task_completed.tvCustomer
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.telecor.gm.mobile.droid.R
import ru.telecor.gm.mobile.droid.di.Scopes
import ru.telecor.gm.mobile.droid.entities.TaskItemPhotoModel
import ru.telecor.gm.mobile.droid.entities.TaskItemPreviewData
import ru.telecor.gm.mobile.droid.entities.task.TaskRelations
import ru.telecor.gm.mobile.droid.extensions.emptyIfNull
import ru.telecor.gm.mobile.droid.extensions.visible
import ru.telecor.gm.mobile.droid.presentation.taskcompleted.TaskCompletedPresenter
import ru.telecor.gm.mobile.droid.presentation.taskcompleted.TaskCompletedView
import ru.telecor.gm.mobile.droid.ui.base.BaseFragment
import ru.telecor.gm.mobile.droid.ui.taskcompleted.rv.TaskCompletedAdapter
import ru.telecor.gm.mobile.droid.ui.taskcompleted.rv.TaskCompletedPhotoAdapter
import ru.telecor.gm.mobile.droid.utils.millisecondsDate
import toothpick.Toothpick
import java.text.SimpleDateFormat
import java.util.*

class TaskCompletedFragment : BaseFragment(), TaskCompletedView {

    private var setListTask: List<TaskRelations> = arrayListOf()
    private var setTaskId = 0

    companion object {
        private const val TASK_ID_KEY = "taskid"

        fun newInstance(taskId: Int, listTask: List<TaskRelations>) = TaskCompletedFragment().apply {
            arguments = Bundle().apply {
                putInt(TASK_ID_KEY, taskId)
                setTaskId = taskId
            }
            setListTask = listTask
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

    private var beforeRecyclerAdapter = TaskCompletedPhotoAdapter()
    private var afterRecyclerAdapter = TaskCompletedPhotoAdapter()
    private var troubleRecyclerAdapter = TaskCompletedPhotoAdapter()
    private var troubleTaskRecyclerAdapter = TaskCompletedPhotoAdapter()

    private var adapters = TaskCompletedAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
        setItemTask(setListTask, setTaskId)

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

    private fun initClick() {
        troubleArrowBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    override fun setAddress(str: String) {
        troubleTvAddress.text = str
    }

    override fun setContainersList(list: List<TaskItemPreviewData>) {
        adapters.update(list)
        troubleRvUpContainers.adapter = adapters
    }

    private fun setItemTask(listTask: List<TaskRelations>, taskId: Int){
        if (listTask.isNotEmpty() && taskId.toString().isNotEmpty()){
            val task = listTask.first{ it.task.id == taskId}

            if (task.task.priority != null){
                messageCon.visible(task.task.priority.name.isNotEmpty())
                tvTroublePriority.text = task.task.priority.name ?: ""
            }

            tvTroublePreferredTimeText.text = SimpleDateFormat(
                "E HH:mm",
                Locale("ru")
            ).format(Date(task.task.preferredTimeStart))
                .capitalize() + " - " + SimpleDateFormat(
                "E HH:mm", Locale("ru")
            ).format(Date(task.task.preferredTimeEnd)).capitalize()

            tvTroubleFactTimeText.text = millisecondsDate(task.task.planTimeStart)

            val text = task.task.taskItems.map { item -> item.customer.name }
            val oneText = text.toString().replace("[", "")
            val finText = oneText.replace("]", "")
            tvCustomer.text = finText

            if (task.task.contactPhone != null){
                iconTorsoLay.visible(task.task.contactPhone.isNotEmpty())
                tvTroubleUserPhoneText.text = task.task.contactPhone.emptyIfNull()
            }

            if (task.task.comment != null){
                layoutIconLay.visible(task.task.comment.isNotEmpty())
                tvTroubleUserCommentText.text = task.task.comment.emptyIfNull()
            }
        }
    }

    override fun showListOfBeforePhoto(list: List<TaskItemPhotoModel>) {
        list.forEach {
            beforeRecyclerAdapter.setList(list)
            tvBefore.visible(it.entity.isNotEmpty())
            rvBeforePhotos.visible(it.entity.isNotEmpty())
        }
    }

    override fun showListOfAfterPhoto(list: List<TaskItemPhotoModel>) {
        list.forEach {
            afterRecyclerAdapter.setList(list)
            tvAfter.visible(it.entity.isNotEmpty())
            rvAfterPhotos.visible(it.entity.isNotEmpty())
        }
    }

    override fun showListOfTroublePhoto(list: List<TaskItemPhotoModel>) {
        list.forEach {
            troubleRecyclerAdapter.setList(list)
            tvTrouble.visible(it.entity.isNotEmpty())
            rvTroublePhotos.visible(it.entity.isNotEmpty())
        }
    }

    override fun showListOfTroubleTaskPhoto(list: List<TaskItemPhotoModel>) {
        list.forEach {
            troubleTaskRecyclerAdapter.setList(list)
            tvTroubleTask.visible(it.entity.isNotEmpty())
            rvTroubleTaskPhotos.visible(it.entity.isNotEmpty())
        }
    }

    override fun setLoadingState(value: Boolean) {
        pbLoading.visible(value)
    }
}
