package org.activityinfo.ui.client.component.importDialog.data;

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.importing.SourceColumn;
import org.activityinfo.core.shared.importing.SourceRow;
import org.activityinfo.core.shared.importing.SourceTable;

import java.util.List;

/**
 * An import source pasted in to a text field by the user.
 */
public class PastedTable implements SourceTable {


    private String text;

    private List<SourceColumn> columns;
    private List<PastedRow> rows;
    private int headerRowCount;


    public PastedTable(String text) {
        this.text = text;
    }


    @Override
    public List<SourceColumn> getColumns() {
        ensureParsed();
        return columns;
    }

    private void ensureParsed() {
        if (rows == null) {
            parseRows();
        }
    }

    private void parseRows() {
        char delimiter = new DelimiterGuesser(text).guess();
        this.rows = new RowParser(text, delimiter).parseRows();
        parseHeaders(rows.get(0));
    }

    private void parseHeaders(PastedRow headerRow) {
        columns = Lists.newArrayList();
        for (int i = 0; i != headerRow.getColumnCount(); ++i) {
            SourceColumn column = new SourceColumn();
            column.setIndex(i);
            column.setHeader(headerRow.getColumnValue(i));
            columns.add(column);
        }
    }

    @Override
    public List<SourceRow> getRows() {
        ensureParsed();
        headerRowCount = 1;
        return (List)rows.subList(headerRowCount, rows.size());
    }

    public String get(int row, int column) {
        ensureParsed();
        return rows.get(row).getColumnValue(column);
    }

    private String maybeRemoveCarriageReturn(String row) {
        if (row.endsWith("\r")) {
            return row.substring(0, row.length() - 1);
        } else {
            return row;
        }
    }

    @Override
    public String getColumnHeader(Integer columnIndex) {
        return columns.get(columnIndex).getHeader();
    }
}
