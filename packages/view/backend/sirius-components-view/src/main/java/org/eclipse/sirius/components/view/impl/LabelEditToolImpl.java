/**
 * Copyright (c) 2021, 2022 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *      Obeo - initial API and implementation
 */
package org.eclipse.sirius.components.view.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.sirius.components.view.LabelEditTool;
import org.eclipse.sirius.components.view.ViewPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Label Edit Tool</b></em>'. <!-- end-user-doc
 * -->
 *
 * @generated
 */
public class LabelEditToolImpl extends ToolImpl implements LabelEditTool {
    /**
     * The default value of the '{@link #getInitialDirectEditLabelExpression() <em>Initial Direct Edit Label
     * Expression</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getInitialDirectEditLabelExpression()
     * @generated
     * @ordered
     */
    protected static final String INITIAL_DIRECT_EDIT_LABEL_EXPRESSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getInitialDirectEditLabelExpression() <em>Initial Direct Edit Label
     * Expression</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @see #getInitialDirectEditLabelExpression()
     * @generated
     * @ordered
     */
    protected String initialDirectEditLabelExpression = INITIAL_DIRECT_EDIT_LABEL_EXPRESSION_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    protected LabelEditToolImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ViewPackage.Literals.LABEL_EDIT_TOOL;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public String getInitialDirectEditLabelExpression() {
        return this.initialDirectEditLabelExpression;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void setInitialDirectEditLabelExpression(String newInitialDirectEditLabelExpression) {
        String oldInitialDirectEditLabelExpression = this.initialDirectEditLabelExpression;
        this.initialDirectEditLabelExpression = newInitialDirectEditLabelExpression;
        if (this.eNotificationRequired())
            this.eNotify(new ENotificationImpl(this, Notification.SET, ViewPackage.LABEL_EDIT_TOOL__INITIAL_DIRECT_EDIT_LABEL_EXPRESSION, oldInitialDirectEditLabelExpression,
                    this.initialDirectEditLabelExpression));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case ViewPackage.LABEL_EDIT_TOOL__INITIAL_DIRECT_EDIT_LABEL_EXPRESSION:
            return this.getInitialDirectEditLabelExpression();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case ViewPackage.LABEL_EDIT_TOOL__INITIAL_DIRECT_EDIT_LABEL_EXPRESSION:
            this.setInitialDirectEditLabelExpression((String) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case ViewPackage.LABEL_EDIT_TOOL__INITIAL_DIRECT_EDIT_LABEL_EXPRESSION:
            this.setInitialDirectEditLabelExpression(INITIAL_DIRECT_EDIT_LABEL_EXPRESSION_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case ViewPackage.LABEL_EDIT_TOOL__INITIAL_DIRECT_EDIT_LABEL_EXPRESSION:
            return INITIAL_DIRECT_EDIT_LABEL_EXPRESSION_EDEFAULT == null ? this.initialDirectEditLabelExpression != null
                    : !INITIAL_DIRECT_EDIT_LABEL_EXPRESSION_EDEFAULT.equals(this.initialDirectEditLabelExpression);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public String toString() {
        if (this.eIsProxy())
            return super.toString();

        StringBuilder result = new StringBuilder(super.toString());
        result.append(" (initialDirectEditLabelExpression: "); //$NON-NLS-1$
        result.append(this.initialDirectEditLabelExpression);
        result.append(')');
        return result.toString();
    }

} // LabelEditToolImpl
