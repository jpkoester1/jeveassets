/*
 * Copyright 2009-2023 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package net.nikr.eve.jeveasset.gui.tabs.materials;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import net.nikr.eve.jeveasset.Program;
import net.nikr.eve.jeveasset.data.settings.types.LocationType;
import net.nikr.eve.jeveasset.gui.images.Images;
import net.nikr.eve.jeveasset.gui.shared.components.JFixedToolBar;
import net.nikr.eve.jeveasset.gui.shared.components.JMainTabSecondary;
import net.nikr.eve.jeveasset.gui.shared.components.ListComboBoxModel;
import net.nikr.eve.jeveasset.gui.shared.filter.ExportDialog;
import net.nikr.eve.jeveasset.gui.shared.filter.SimpleFilterControl;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuColumns;
import net.nikr.eve.jeveasset.gui.shared.menu.JMenuInfo;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuData;
import net.nikr.eve.jeveasset.gui.shared.menu.MenuManager.TableMenu;
import net.nikr.eve.jeveasset.gui.shared.table.EnumTableFormatAdaptor;
import net.nikr.eve.jeveasset.gui.shared.table.EventListManager;
import net.nikr.eve.jeveasset.gui.shared.table.EventModels;
import net.nikr.eve.jeveasset.gui.shared.table.JSeparatorTable;
import net.nikr.eve.jeveasset.gui.shared.table.PaddingTableCellRenderer;
import net.nikr.eve.jeveasset.gui.shared.table.TableFormatFactory;
import net.nikr.eve.jeveasset.i18n.GuiShared;
import net.nikr.eve.jeveasset.i18n.TabsMaterials;


public class MaterialsTab extends JMainTabSecondary {

	private enum MaterialsAction {
		SELECTED,
		COLLAPSE,
		EXPAND,
		EXPORT
	}

	//GUI
	private final JComboBox<String> jOwners;
	private final JButton jExport;
	private final JButton jExpand;
	private final JButton jCollapse;
	private final JCheckBox jPiMaterial;
	private final JSeparatorTable jTable;
	private final JScrollPane jTableScroll;

	//Table
	private final EventList<Material> eventList;
	private final SeparatorList<Material> separatorList;
	private final DefaultEventSelectionModel<Material> selectionModel;
	private final DefaultEventTableModel<Material> tableModel;
	private final EnumTableFormatAdaptor<MaterialTableFormat, Material> tableFormat;

	//Dialog
	private final ExportDialog<Material> exportDialog;

	//Data
	private final MaterialsData materialsData;

	public static final String NAME = "materials"; //Not to be changed!

	public MaterialsTab(final Program program) {
		super(program, NAME, TabsMaterials.get().materials(), Images.TOOL_MATERIALS.getIcon(), true);

		materialsData = new MaterialsData(program);

		ListenerClass listener = new ListenerClass();

		JFixedToolBar jToolBarLeft = new JFixedToolBar();

		jOwners = new JComboBox<>();
		jOwners.setActionCommand(MaterialsAction.SELECTED.name());
		jOwners.addActionListener(listener);
		jToolBarLeft.addComboBox(jOwners, 200);

		jPiMaterial = new JCheckBox(TabsMaterials.get().includePI());
		jPiMaterial.setActionCommand(MaterialsAction.SELECTED.name());
		jPiMaterial.addActionListener(listener);
		jToolBarLeft.add(jPiMaterial);

		jToolBarLeft.addSpace(10);

		jToolBarLeft.addSeparator();

		jExport = new JButton(GuiShared.get().export(), Images.DIALOG_CSV_EXPORT.getIcon());
		jExport.setActionCommand(MaterialsAction.EXPORT.name());
		jExport.addActionListener(listener);
		jToolBarLeft.addButton(jExport);

		JFixedToolBar jToolBarRight = new JFixedToolBar();

		jCollapse = new JButton(TabsMaterials.get().collapse(), Images.MISC_COLLAPSED.getIcon());
		jCollapse.setActionCommand(MaterialsAction.COLLAPSE.name());
		jCollapse.addActionListener(listener);
		jToolBarRight.addButton(jCollapse);

		jExpand = new JButton(TabsMaterials.get().expand(), Images.MISC_EXPANDED.getIcon());
		jExpand.setActionCommand(MaterialsAction.EXPAND.name());
		jExpand.addActionListener(listener);
		jToolBarRight.addButton(jExpand);

		//Table Format
		tableFormat = TableFormatFactory.materialTableFormat();
		//Backend
		eventList = EventListManager.create();
		//Separator
		eventList.getReadWriteLock().readLock().lock();
		separatorList = new SeparatorList<>(eventList, new MaterialSeparatorComparator(), 1, Integer.MAX_VALUE);
		eventList.getReadWriteLock().readLock().unlock();
		//Table Model
		tableModel = EventModels.createTableModel(separatorList, tableFormat);
		//Table
		jTable = new JSeparatorTable(program, tableModel, separatorList);
		jTable.setSeparatorRenderer(new MaterialsSeparatorTableCell(jTable, separatorList));
		jTable.setSeparatorEditor(new MaterialsSeparatorTableCell(jTable, separatorList));
		PaddingTableCellRenderer.install(jTable, 3);
		//Selection Model
		selectionModel = EventModels.createSelectionModel(separatorList);
		selectionModel.setSelectionMode(ListSelection.MULTIPLE_INTERVAL_SELECTION_DEFENSIVE);
		jTable.setSelectionModel(selectionModel);
		//Listeners
		installTable(jTable);
		//Scroll
		jTableScroll = new JScrollPane(jTable);
		//Menu
		installTableTool(new MaterialTableMenu(), tableFormat, tableModel, jTable, eventList, Material.class);

		exportDialog = new ExportDialog<>(program.getMainWindow().getFrame(), NAME, null, new MaterialsFilterControl(), tableFormat, eventList);

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(jToolBarLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Integer.MAX_VALUE)
					.addGap(0)
					.addComponent(jToolBarRight)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(jToolBarLeft, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jToolBarRight, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				)
				.addComponent(jTableScroll, 0, 0, Short.MAX_VALUE)
		);
	}

	@Override
	public void updateData() {
		if (!program.getOwnerNames(false).isEmpty()) {
			jExport.setEnabled(true);
			jExpand.setEnabled(true);
			jCollapse.setEnabled(true);
			jOwners.setEnabled(true);
			String selectedItem = (String) jOwners.getSelectedItem();
			jOwners.setModel(new ListComboBoxModel<>(program.getOwnerNames(true)));
			if (selectedItem != null && program.getOwnerNames(true).contains(selectedItem)) {
				jOwners.setSelectedItem(selectedItem);
			} else {
				jOwners.setSelectedIndex(0);
			}
		} else {
			jExport.setEnabled(false);
			jExpand.setEnabled(false);
			jCollapse.setEnabled(false);
			jOwners.setEnabled(false);
			jOwners.setModel(new ListComboBoxModel<>());
			jOwners.getModel().setSelectedItem(TabsMaterials.get().no());
		}
	}

	@Override
	public void clearData() {
		try {
			eventList.getReadWriteLock().writeLock().lock();
			eventList.clear();
		} finally {
			eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void updateCache() { }

	@Override
	public Collection<LocationType> getLocations() {
		try {
			eventList.getReadWriteLock().readLock().lock();
			return new ArrayList<>(eventList);
		} finally {
			eventList.getReadWriteLock().readLock().unlock();
		}
	}

	private void updateTable() {
		beforeUpdateData();
		String owner = (String) jOwners.getSelectedItem();
		boolean piMaterials = jPiMaterial.isSelected();
		//Save separator expanded/collapsed state
		jTable.saveExpandedState();
		//Update Data
		boolean isEmpty = materialsData.updateData(eventList, owner, piMaterials);
		//Restore separator expanded/collapsed state
		jTable.loadExpandedState();
		if (!isEmpty) {
			jExport.setEnabled(true);
			jExpand.setEnabled(true);
			jCollapse.setEnabled(true);
		} else {
			jExport.setEnabled(false);
			jExpand.setEnabled(false);
			jCollapse.setEnabled(false);
		}
		jTableScroll.getViewport().setViewPosition(new Point(0, 0));
		afterUpdateData();
	}

	private class MaterialTableMenu implements TableMenu<Material> {
		@Override
		public MenuData<Material> getMenuData() {
			return new MenuData<>(selectionModel.getSelected());
		}

		@Override
		public JMenu getFilterMenu() {
			return null;
		}

		@Override
		public JMenu getColumnMenu() {
			return new JMenuColumns<>(program, tableFormat, tableModel, jTable, NAME);
		}

		@Override
		public void addInfoMenu(JPopupMenu jPopupMenu) {
			JMenuInfo.material(jPopupMenu, selectionModel.getSelected(), eventList);
		}

		@Override
		public void addToolMenu(JComponent jComponent) { }
	}

	private class ListenerClass implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (MaterialsAction.SELECTED.name().equals(e.getActionCommand())) {
				updateTable();
			}
			if (MaterialsAction.COLLAPSE.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(false);
			}
			if (MaterialsAction.EXPAND.name().equals(e.getActionCommand())) {
				jTable.expandSeparators(true);
			}
			if (MaterialsAction.EXPORT.name().equals(e.getActionCommand())) {
				exportDialog.setVisible(true);
			}
		}
	}

	private class MaterialsFilterControl implements SimpleFilterControl<Material> {

		@Override
		public void saveSettings(final String msg) {
			program.saveSettings("Materials Table " + msg); //Save Material Export Settings (Filters not used)
		}
	}
}
