import React from "react";
import { Table } from "react-bootstrap";

type GenericTableProps<T> = {
  columns: {
    key: keyof T;
    label: string;
    render?: (row: T) => React.ReactNode;
  }[];
  data: T[];
};

function GenericTable<T>({ columns, data }: GenericTableProps<T>) {
  return (
    <Table striped bordered hover responsive>
      <thead className="table-dark">
        <tr>
          {columns.map((col) => (
            <th key={String(col.key)}>{col.label}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {data.map((row, idx) => (
          <tr key={idx}>
            {columns.map((col) => (
              <td key={String(col.key)}>
                {col.render ? col.render(row) : String(row[col.key])}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </Table>
  );
}

export default GenericTable;
