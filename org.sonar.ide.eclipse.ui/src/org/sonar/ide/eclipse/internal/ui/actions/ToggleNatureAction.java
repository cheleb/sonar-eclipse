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

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.slf4j.LoggerFactory;
import org.sonar.ide.eclipse.core.SonarCorePlugin;
import org.sonar.ide.eclipse.internal.ui.jobs.RefreshAllViolationsJob;

public class ToggleNatureAction implements IObjectActionDelegate {

  private ISelection selection;

  public void run(IAction action) {
    if (selection instanceof IStructuredSelection) {
      for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it.hasNext();) {
        Object element = it.next();
        IProject project = null;
        if (element instanceof IProject) {
          project = (IProject) element;
        } else if (element instanceof IAdaptable) {
          project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
        }
        if (project != null) {
          try {
            toggleNature(project);
          } catch (CoreException e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
          }
        }
      }
    }
  }

  private void toggleNature(IProject project) throws CoreException {
    if (project.hasNature(SonarCorePlugin.NATURE_ID)) {
      disableNature(project);
    } else {
      enableNature(project);
    }
  }

  public static void enableNature(IProject project) throws CoreException {
    IProjectDescription description = project.getDescription();
    String[] prevNatures = description.getNatureIds();
    String[] newNatures = new String[prevNatures.length + 1];
    System.arraycopy(prevNatures, 0, newNatures, 1, prevNatures.length);
    newNatures[0] = SonarCorePlugin.NATURE_ID;
    description.setNatureIds(newNatures);
    project.setDescription(description, null);

    // see http://jira.codehaus.org/browse/SONARIDE-167
    RefreshAllViolationsJob.createAndSchedule(project);
  }

  private void disableNature(IProject project) throws CoreException {
    project.deleteMarkers(SonarCorePlugin.MARKER_ID, true, IResource.DEPTH_INFINITE);

    IProjectDescription description = project.getDescription();
    List<String> newNatures = Lists.newArrayList();
    for (String natureId : description.getNatureIds()) {
      if (!SonarCorePlugin.NATURE_ID.equals(natureId)) {
        newNatures.add(natureId);
      }
    }
    description.setNatureIds(newNatures.toArray(new String[newNatures.size()]));
    project.setDescription(description, null);
  }

  public void selectionChanged(IAction action, ISelection selection) {
    this.selection = selection;
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }

}
