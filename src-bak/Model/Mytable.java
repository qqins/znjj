package Model;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class Mytable extends JTable {

	/**
	 * ¹¹Ôìº¯Êý
	 */
	public Mytable() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param numRows
	 * @param numColumns
	 */
	public Mytable(int numRows, int numColumns) {
		super(numRows, numColumns);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param rowData
	 * @param columnNames
	 */
	public Mytable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dm
	 * @param cm
	 * @param sm
	 */
	public Mytable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dm
	 * @param cm
	 */
	public Mytable(TableModel dm, TableColumnModel cm) {
		super(dm, cm);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dm
	 */
	public Mytable(TableModel dm) {
		super(dm);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param rowData
	 * @param columnNames
	 */
	public Mytable(Vector rowData, Vector columnNames) {
		super(rowData, columnNames);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCellSelected(int row, int column) {
		// TODO Auto-generated method stub
		return super.isCellSelected(row, column);
	}

	@Override
	public boolean isColumnSelected(int column) {
		// TODO Auto-generated method stub
		return super.isColumnSelected(column);
	}

	@Override
	public boolean isEditing() {
		// TODO Auto-generated method stub
		return super.isEditing();
	}

	@Override
	public boolean isRowSelected(int row) {
		// TODO Auto-generated method stub
		return super.isRowSelected(row);
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	
	

}
