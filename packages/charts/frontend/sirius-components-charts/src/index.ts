/*******************************************************************************
 * Copyright (c) 2022 Obeo and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/

import { BarChartRepresentation } from './bar/BarChart.types';
import { PieChartRepresentation } from './pie/PieChart.types';

export type Chart = BarChartRepresentation | PieChartRepresentation;

export * from './bar/BarChart';
export * from './bar/BarChart.types';
export * from './pie/PieChart';
export * from './pie/PieChart.types';
