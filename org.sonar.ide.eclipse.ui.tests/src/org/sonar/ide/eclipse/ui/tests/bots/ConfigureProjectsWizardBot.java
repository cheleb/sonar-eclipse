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
package org.sonar.ide.eclipse.ui.tests.bots;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;

public class ConfigureProjectsWizardBot {
  private SWTBot bot = new SWTBot();

  public ConfigureProjectsWizardBot() {
    bot.shell("Associate with Sonar").activate();
  }

  public String getStatus() {
    return bot.text(0).getText();
  }

  public void cancel() {
    bot.button("Cancel").click();
  }

  public void finish() {
    SWTBotButton button = bot.button("&Finish");
    button.click();
    bot.waitUntil(Conditions.widgetIsEnabled(button), 1000 * 30);
  }

  public void find() {
    SWTBotButton button = bot.button("Find on server");
    button.click();
    bot.waitUntil(Conditions.widgetIsEnabled(button), 1000 * 30);
  }
}
