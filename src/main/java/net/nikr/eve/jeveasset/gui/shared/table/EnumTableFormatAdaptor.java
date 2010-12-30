package net.nikr.eve.jeveasset.gui.shared.table;

import ca.odell.glazedlists.gui.AdvancedTableFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Candle
 */
public class EnumTableFormatAdaptor<T extends Enum<T> & TableColumn<Q>, Q> implements AdvancedTableFormat<Q> {
	private static final Logger LOG = LoggerFactory.getLogger(EnumTableFormatAdaptor.class);

	List<T> shownColumns;
	List<T> orderColumns;
	ColumnComparator columnComparator;

	public EnumTableFormatAdaptor(Class<T> enumClass) {
		shownColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
		orderColumns = new ArrayList<T>(Arrays.asList(enumClass.getEnumConstants()));
		columnComparator = new ColumnComparator();
	}

	public List<T> getShownColumns(){
		return shownColumns;
	}

	public List<T> getOrderColumns(){
		return orderColumns;
	}

	public void moveColumn(int from, int to){
		if (from == to) return;
		T fromColumn = getColumn(from);
		T toColumn = getColumn(to);

		int fromIndex = orderColumns.indexOf(fromColumn);
		orderColumns.remove(fromIndex);

		int toIndex = orderColumns.indexOf(toColumn);
		if (to > from) toIndex++;
		orderColumns.add(toIndex, fromColumn);

		updateColumns();
	}

	public void hideColumn(T column){
		if (!shownColumns.contains(column)) return;
		shownColumns.remove(column);
		updateColumns();
	}

	public void showColumn(T column){
		if (shownColumns.contains(column)) return;
		shownColumns.add(column);
		updateColumns();
	}

	private T getColumn(int i){
		return shownColumns.get(i);
	}

	private void updateColumns(){
		Collections.sort(shownColumns, columnComparator);
	}

	private List<T> getColumns() {
		return shownColumns;
	}

	@Override public Class getColumnClass(int i) {
		return getColumn(i).getType();
	}

	@Override public Comparator getColumnComparator(int i) {
		return getColumn(i).getComparator();
	}

	@Override public int getColumnCount() {
		return getColumns().size();
	}

	@Override public String getColumnName(int i) {
		return getColumn(i).getColumnName();
	}

	@Override public Object getColumnValue(Q e, int i) {
		return getColumn(i).getColumnValue(e);
	}

	class ColumnComparator implements Comparator<T>{

		@Override
		public int compare(T o1, T o2) {
			return orderColumns.indexOf(o1) - orderColumns.indexOf(o2);
		}

	}
}