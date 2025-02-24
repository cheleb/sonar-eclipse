/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2010-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.ide.eclipse.internal.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.sonar.ide.eclipse.core.jobs.AnalyseProjectJob;
import org.sonar.ide.eclipse.internal.core.resources.ProjectProperties;
import org.sonar.ide.eclipse.internal.ui.jobs.RefreshAllViolationsJob;
import org.sonar.ide.eclipse.ui.util.SelectionUtils;

public class ChangeAnalysisModeAction implements IObjectActionDelegate {

  public static final String LOCAL_MODE = "org.sonar.ide.eclipse.runtime.ui.enableLocalAnalysisAction";
  public static final String REMOTE_MODE = "org.sonar.ide.eclipse.runtime.ui.enableRemoteAnalysisAction";

  /**
   * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
   */
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }

  /**
   * @see org.eclipse.ui.IActionDelegate#run(IAction)
   */
  public void run(IAction action) {
    ProjectProperties projectProperties = ProjectProperties.getInstance(project);
    projectProperties.setAnalysedLocally(isLocalAnalysis(action));
    projectProperties.save();

    if (isLocalAnalysis(action)) {
      new AnalyseProjectJob(project).schedule();
    } else {
      RefreshAllViolationsJob.createAndSchedule(project);
    }
  }

  private IProject project;

  /**
   * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
   */
  public void selectionChanged(IAction action, ISelection selection) {
    project = (IProject) SelectionUtils.getSingleElement(selection);
    if (project != null) {
      ProjectProperties projectProperties = ProjectProperties.getInstance(project);
      action.setChecked(isChecked(action, projectProperties));
      action.setEnabled(!action.isChecked());
    }
  }

  private boolean isChecked(IAction action, ProjectProperties projectProperties) {
    if (isLocalAnalysis(action)) {
      return projectProperties.isAnalysedLocally();
    } else {
      return !projectProperties.isAnalysedLocally();
    }
  }

  private boolean isLocalAnalysis(IAction action) {
    return LOCAL_MODE.equals(action.getId());
  }
}
