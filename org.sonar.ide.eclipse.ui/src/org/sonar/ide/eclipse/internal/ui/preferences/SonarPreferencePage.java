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
package org.sonar.ide.eclipse.internal.ui.preferences;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.sonar.ide.eclipse.core.SonarCorePlugin;
import org.sonar.ide.eclipse.internal.ui.AbstractTableLabelProvider;
import org.sonar.ide.eclipse.internal.ui.Messages;
import org.sonar.ide.eclipse.internal.ui.wizards.EditServerLocationWizard;
import org.sonar.ide.eclipse.internal.ui.wizards.NewServerLocationWizard;
import org.sonar.ide.eclipse.ui.SonarUiPlugin;
import org.sonar.ide.eclipse.ui.util.SelectionUtils;
import org.sonar.wsclient.Host;

/**
 * Preference page for the workspace.
 */
public class SonarPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  private TableViewer serversViewer;

  private List<Host> servers;

  public SonarPreferencePage() {
    super(Messages.SonarPreferencePage_title);
    noDefaultAndApplyButton();
  }

  public void init(IWorkbench workbench) {
    setDescription(Messages.SonarPreferencePage_description);
  }

  @Override
  protected Control createContents(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(3, false);
    container.setLayout(layout);

    createTable(container);

    initTable();

    return container;
  }

  private Host getSelectedServer() {
    return (Host) SelectionUtils.getSingleElement(serversViewer.getSelection());
  }

  private void initTable() {
    // retrieve list of servers
    servers = SonarCorePlugin.getServersManager().getHosts();
    serversViewer.setInput(servers);
  }

  private void createTable(Composite composite) {
    serversViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
    serversViewer.setContentProvider(ArrayContentProvider.getInstance());
    serversViewer.setLabelProvider(new ServersLabelProvider());

    final Table table = serversViewer.getTable();
    GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false, 2, 3);
    gridData.heightHint = 300;
    table.setLayoutData(gridData);

    final Button addButton = new Button(composite, SWT.NONE);
    final Button editButton = new Button(composite, SWT.NONE);
    final Button removeButton = new Button(composite, SWT.NONE);

    addButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
    addButton.setText(Messages.SonarPreferencePage_action_add);
    addButton.setToolTipText(Messages.SonarPreferencePage_action_add_tooltip);
    addButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD).createImage());
    addButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        NewServerLocationWizard wiz = new NewServerLocationWizard();
        wiz.init(SonarUiPlugin.getDefault().getWorkbench(), null);
        WizardDialog dialog = new WizardDialog(addButton.getShell(), wiz);
        dialog.create();
        if (dialog.open() == Window.OK) {
          initTable();
        }
      }
    });

    editButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
    editButton.setText(Messages.SonarPreferencePage_action_edit);
    editButton.setToolTipText(Messages.SonarPreferencePage_action_edit_tooltip);
    editButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD).createImage());
    editButton.setEnabled(false);
    editButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        EditServerLocationWizard wizard = new EditServerLocationWizard(getSelectedServer());
        wizard.init(SonarUiPlugin.getDefault().getWorkbench(), null);
        WizardDialog dialog = new WizardDialog(editButton.getShell(), wizard);
        dialog.create();
        if (dialog.open() == Window.OK) {
          initTable();
        }
        removeButton.setEnabled(false);
        editButton.setEnabled(false);
      }
    });

    removeButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
    removeButton.setText(Messages.SonarPreferencePage_action_delete);
    removeButton.setToolTipText(Messages.SonarPreferencePage_action_delete_tooltip);
    removeButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE).createImage());
    removeButton.setEnabled(false);
    removeButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Host selected = getSelectedServer();
        if (MessageDialog.openConfirm(SonarPreferencePage.this.getShell(), "Remove sonar server connection",
            MessageFormat.format("Confirm removing {0}",
                new Object[] { selected.getHost() }))) {
          SonarCorePlugin.getServersManager().removeServer(selected.getHost());
          servers.remove(selected);
          serversViewer.refresh();
          removeButton.setEnabled(false);
          editButton.setEnabled(false);
        }
      }
    });

    serversViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {
        removeButton.setEnabled(!servers.isEmpty());
        editButton.setEnabled(true);
      }
    });
  }

  private class ServersLabelProvider extends AbstractTableLabelProvider {
    @Override
    public String getColumnText(Object element, int columnIndex) {
      Host host = (Host) element;
      return host.getHost();
    }
  }

}
