package com.jeremyliao.plugin;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

public class GenerateTestCaseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String name = virtualFile.getName();
        String path = virtualFile.getPath();
        String canonicalPath = virtualFile.getCanonicalPath();
        RunProfile.getState()
        Messages.showMessageDialog("Hello World !", "Information", Messages.getInformationIcon());
    }
}
