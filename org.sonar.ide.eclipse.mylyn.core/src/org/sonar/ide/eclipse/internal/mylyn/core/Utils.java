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
package org.sonar.ide.eclipse.internal.mylyn.core;

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

public final class Utils {

  private static final String[] PRIORITIES = {
    SonarClient.PRIORITY_BLOCKER,
    SonarClient.PRIORITY_CRITICAL,
    SonarClient.PRIORITY_MAJOR,
    SonarClient.PRIORITY_MINOR,
    SonarClient.PRIORITY_INFO
  };

  public static PriorityLevel toMylynPriority(String value) {
    for (int i = 0; i < PRIORITIES.length; i++) {
      if (PRIORITIES[i].equals(value)) {
        return PriorityLevel.fromLevel(i + 1);
      }
    }
    return null;
  }

  private Utils() {
  }

}
