/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.List;
import java.util.function.Supplier;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;

public class OutputClauseColumn extends DMNGridColumn<String> implements HasSingletonDOMElementResource {

    private final Supplier<List<HeaderMetaData>> headerMetaDataSupplier;

    private final TextAreaSingletonDOMElementFactory factory;

    public OutputClauseColumn(final Supplier<List<HeaderMetaData>> headerMetaDataSupplier,
                              final TextAreaSingletonDOMElementFactory factory,
                              final DecisionTableGrid gridWidget) {
        super(headerMetaDataSupplier.get(),
              new OutputClauseColumnRenderer(factory),
              gridWidget);
        this.headerMetaDataSupplier = PortablePreconditions.checkNotNull("headerMetaDataSupplier",
                                                                         headerMetaDataSupplier);
        this.factory = PortablePreconditions.checkNotNull("factory",
                                                          factory);
        setMovable(true);
        setResizable(true);
    }

    @Override
    public List<HeaderMetaData> getHeaderMetaData() {
        return headerMetaDataSupplier.get();
    }

    @Override
    public void edit(final GridCell<String> cell,
                     final GridBodyCellRenderContext context,
                     final Callback<GridCellValue<String>> callback) {
        factory.attachDomElement(context,
                                 (e) -> e.getWidget().setValue(assertCell(cell).getValue().getValue()),
                                 (e) -> e.getWidget().setFocus(true));
    }

    private GridCell<String> assertCell(final GridCell<String> cell) {
        if (cell != null) {
            return cell;
        }
        return new BaseGridCell<>(new BaseGridCellValue<>(""));
    }

    @Override
    public void flush() {
        factory.flush();
    }

    @Override
    public void destroyResources() {
        factory.destroyResources();
        getHeaderMetaData().stream()
                .filter(md -> md instanceof HasDOMElementResources)
                .map(md -> (HasDOMElementResources) md)
                .forEach(HasDOMElementResources::destroyResources);
    }

    @Override
    public void setWidth(final double width) {
        super.setWidth(width);
        updateWidthOfPeers();
    }
}
