package ru.telecor.gm.mobile.droid.presentation.updateVersion

import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.telecor.gm.mobile.droid.BuildConfig
import ru.telecor.gm.mobile.droid.model.BuildVersion
import ru.telecor.gm.mobile.droid.model.data.storage.GmServerPrefs
import ru.telecor.gm.mobile.droid.model.data.storage.SettingsPrefs
import ru.telecor.gm.mobile.droid.model.repository.CommonDataRepository
import ru.telecor.gm.mobile.droid.model.system.IResourceManager
import ru.telecor.gm.mobile.droid.presentation.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class UpdatingVersionSheetPresenter @Inject constructor(
    val gmServerPrefs: GmServerPrefs,
    private val commonDataRepository: CommonDataRepository,
    private val rm: IResourceManager,
    private var prefs: SettingsPrefs
) : BasePresenter<UpdatingVersionSheetView>() {
    var cVersion = BuildVersion.ALPHA

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        cVersion = BuildVersion.fromName(gmServerPrefs.getGmBuildCon().buildVersion)
        viewState.setCurrentVersion(BuildConfig.VERSION_NAME)
        checkForUpdates(BuildVersion.ALPHA)
        checkForUpdates(BuildVersion.BETA)
    }

    private fun checkForUpdates(version: BuildVersion = cVersion, autoCheck: Boolean = true) {
        viewState.setNewestVersion("Ожидайте...", version)
        viewState.setLoadingState(true, version)
        launch {
            val res = commonDataRepository.getLatestVersionInfo(version)
            handleResult(res, {
                viewState.setNewestVersion(it.data.toString(), version)
                if (!autoCheck) {
                    if (version.localName == gmServerPrefs.getGmBuildCon().buildVersion && it.data.toString() != BuildConfig.VERSION_NAME ){
                        viewState.showUpdateDialog(version)
                        //Сохраняем последнюю версию приложения
                        setLatestVersion(it.data.toString())
                    }else if (!autoCheck && it.data.toString() != BuildConfig.VERSION_NAME && version != BuildVersion.BETA){
                        viewState.showUpdateDialog(version)
                        //Сохраняем последнюю версию приложения
                        setLatestVersion(it.data.toString())
                    }else if (!autoCheck && it.data.toString() != BuildConfig.VERSION_NAME && version == BuildVersion.BETA){
                        viewState.showUpdateDialog(version)
                        //Сохраняем последнюю версию приложения
                        setLatestVersion(it.data.toString())
                    }
                } else {
//                    if (!autoCheck && it.data.biggerThan(BuildConfig.VERSION_NAME) && version != BuildVersion.BETA) {
//                        viewState.showUpdateDialog(version)
//                        //Сохраняем последнюю версию приложения
//                        setLatestVersion(it.data.toString())
//                    }
                }
            }, { handleError(it, rm) })
            viewState.setLoadingState(false, version)
        }
    }

    private fun setLatestVersion(string: String){
        prefs.isLatestVersion = string
    }

    //Отвечат за отоброжение окон обновление установка
    fun setInstallationRole(boolean: Boolean){
        prefs.isInstallationRole = boolean
    }

    fun onBackButtonClicked() {
        viewState.finishScreen()
    }

    fun onUpdateButtonClicked(version: BuildVersion, autoCheck: Boolean) {
        checkForUpdates(version, autoCheck)
    }

    fun onUpdateAccepted() {
        launch {
            val path =
                commonDataRepository.downloadLatestVersion(BuildVersion.fromName(gmServerPrefs.getGmBuildCon().buildVersion).serverName)
            handleResult(path, { viewState.installAPK(it.data) }, { handleError(it, rm) })
        }
    }
}
