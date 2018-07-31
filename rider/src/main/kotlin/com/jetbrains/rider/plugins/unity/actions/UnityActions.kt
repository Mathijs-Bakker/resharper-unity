package com.jetbrains.rider.plugins.unity.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.jetbrains.rider.isUnityGeneratedProject
import com.jetbrains.rider.isUnitySidecarProject
import com.jetbrains.rider.plugins.unity.UnityHost
import com.jetbrains.rider.plugins.unity.util.UnityIcons
import com.jetbrains.rider.util.idea.tryGetComponent
import com.jetbrains.rider.util.reactive.fire

class PlayInUnityAction : ToggleAction("Play/Edit", "Change Play/Edit mode in Unity", UnityIcons.Actions.Execute) {

    override fun isSelected(e: AnActionEvent):Boolean {
        val unityHost = e.getHost() ?: return false
        val play = unityHost.play.value
        return play != null && play
    }
    override fun setSelected(e: AnActionEvent?, value: Boolean) {
        val project = e?.project?: return
        UnityHost.CallBackendPlay(project, value)
    }
    override fun update(e: AnActionEvent) {
        if (!e.isUnityProject()) {
            e.presentation.isVisible = false
            return
        }

        e.presentation.isVisible = true

        val projectCustomDataHost = e.getHost() ?: return
        e.presentation.isEnabled = projectCustomDataHost.sessionInitialized.value
        super.update(e)
    }
}

class PauseInUnityAction : ToggleAction("Pause/Resume", "Pause/Resume play in Unity", UnityIcons.Actions.Pause) {
    override fun isSelected(e: AnActionEvent):Boolean {
        val unityHost = e.getHost() ?: return false
        return unityHost.pause.value
    }
    override fun setSelected(e: AnActionEvent?, value: Boolean) {
        val unityHost = e?.getHost() ?: return
        unityHost.pause.value = value
    }

    override fun update(e: AnActionEvent) {
        if (!e.isUnityProject()) {
            e.presentation.isVisible = false
            return
        }

        e.presentation.isVisible = true

        val projectCustomDataHost = e.getHost() ?: return
        val play = projectCustomDataHost.play.value ?: false
        e.presentation.isEnabled = play && projectCustomDataHost.sessionInitialized.value
        super.update(e)
    }
}

class StepInUnityAction : AnAction("Step", "Perform a single frame step.", UnityIcons.Actions.Step) {
    override fun actionPerformed(e: AnActionEvent) {
        e.getHost()?.model?.step?.fire()
    }

    override fun update(e: AnActionEvent) {
        if (!e.isUnityProject()) {
            e.presentation.isVisible = false
            return
        }

        e.presentation.isVisible = true

        val projectCustomDataHost = e.getHost() ?: return
        val play = projectCustomDataHost.play.value ?: false
        e.presentation.isEnabled = play && projectCustomDataHost.sessionInitialized.value
        super.update(e)
    }
}

fun AnActionEvent.getHost(): UnityHost? {
    val project = project?: return null
    return project.tryGetComponent()
}

fun AnActionEvent.isUnityProject(): Boolean {
    val project = this.project ?: return false
    return project.isUnityGeneratedProject() || project.isUnitySidecarProject()
}