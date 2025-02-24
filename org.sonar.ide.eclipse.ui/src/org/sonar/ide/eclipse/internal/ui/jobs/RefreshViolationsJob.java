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
package org.sonar.ide.eclipse.internal.ui.jobs;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.*;
import org.eclipse.ui.progress.UIJob;
import org.sonar.ide.api.SourceCode;
import org.sonar.ide.eclipse.core.ISonarResource;
import org.sonar.ide.eclipse.core.SonarCorePlugin;
import org.sonar.ide.eclipse.internal.EclipseSonar;
import org.sonar.ide.eclipse.internal.core.resources.ProjectProperties;
import org.sonar.ide.eclipse.ui.SonarUiPlugin;
import org.sonar.ide.eclipse.ui.util.PlatformUtils;
import org.sonar.wsclient.services.Violation;

import java.util.*;

/**
 * This class load violations in background.
 * 
 * @link http://jira.codehaus.org/browse/SONARIDE-27
 * 
 * @author Jérémie Lagarde
 */
public class RefreshViolationsJob extends AbstractRefreshModelJob<Violation> {

  public RefreshViolationsJob(final List<IResource> resources) {
    super(resources, SonarCorePlugin.MARKER_ID);
  }

  @Override
  protected Collection<Violation> retrieveDatas(EclipseSonar sonar, IResource resource) {
    SourceCode sourceCode = sonar.search(resource);
    if (sourceCode == null) {
      return Collections.emptyList();
    }
    return sourceCode.getViolations();
  }

  @Override
  protected Integer getLine(final Violation violation) {
    return violation.getLine();
  }

  @Override
  protected String getMessage(final Violation violation) {
    return violation.getRuleName() + " : " + violation.getMessage();
  }

  @Override
  protected Integer getPriority(final Violation violation) {
    if ("blocker".equalsIgnoreCase(violation.getSeverity())) {
      return Integer.valueOf(IMarker.PRIORITY_HIGH);
    }
    if ("critical".equalsIgnoreCase(violation.getSeverity())) {
      return Integer.valueOf(IMarker.PRIORITY_HIGH);
    }
    if ("major".equalsIgnoreCase(violation.getSeverity())) {
      return Integer.valueOf(IMarker.PRIORITY_NORMAL);
    }
    if ("minor".equalsIgnoreCase(violation.getSeverity())) {
      return Integer.valueOf(IMarker.PRIORITY_LOW);
    }
    if ("info".equalsIgnoreCase(violation.getSeverity())) {
      return Integer.valueOf(IMarker.PRIORITY_LOW);
    }
    // TODO handle unknown severity
    return Integer.valueOf(IMarker.PRIORITY_LOW);
  }

  @Override
  protected Integer getSeverity(final Violation violation) {
    return Integer.valueOf(IMarker.SEVERITY_WARNING);
  }

  @Override
  protected Map<String, Object> getExtraInfos(final Violation violation) {
    final Map<String, Object> extraInfos = new HashMap<String, Object>();
    extraInfos.put("rulekey", violation.getRuleKey());
    extraInfos.put("rulename", violation.getRuleName());
    extraInfos.put("rulepriority", violation.getSeverity());
    if (violation.getId() != null) { // id available since Sonar 2.9
      extraInfos.put("violationId", Long.toString(violation.getId()));
    }
    if (violation.getReview() != null) {
      extraInfos.put("reviewId", Long.toString(violation.getReview().getId()));
    }
    return extraInfos;
  }

  public static void setupViolationsUpdater() {
    new UIJob("Prepare violations updater") {
      @Override
      public IStatus runInUIThread(IProgressMonitor monitor) {
        final IWorkbenchPage page = SonarUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        page.addPartListener(new ViolationsUpdater());
        return Status.OK_STATUS;
      }
    }.schedule();
  }

  private static class ViolationsUpdater implements IPartListener2 {
    public void partOpened(IWorkbenchPartReference partRef) {
      IWorkbenchPart part = partRef.getPart(true);
      if (part instanceof IEditorPart) {
        IEditorInput input = ((IEditorPart) part).getEditorInput();
        if (input instanceof IFileEditorInput) {
          IResource resource = ((IFileEditorInput) input).getFile();
          ISonarResource sonarResource = PlatformUtils.adapt(resource, ISonarResource.class);
          if (sonarResource != null) {
            ProjectProperties projectProperties = ProjectProperties.getInstance(resource);
            if (!projectProperties.isAnalysedLocally()) {
              new RefreshViolationsJob(Collections.singletonList(resource)).schedule();
            }
          }
        }
      }
    }

    public void partVisible(IWorkbenchPartReference partRef) {
    }

    public void partInputChanged(IWorkbenchPartReference partRef) {
    }

    public void partHidden(IWorkbenchPartReference partRef) {
    }

    public void partDeactivated(IWorkbenchPartReference partRef) {
    }

    public void partClosed(IWorkbenchPartReference partRef) {
    }

    public void partBroughtToTop(IWorkbenchPartReference partRef) {
    }

    public void partActivated(IWorkbenchPartReference partRef) {
    }
  }

}
